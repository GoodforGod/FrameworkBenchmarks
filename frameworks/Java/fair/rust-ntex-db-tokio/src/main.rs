#[cfg(not(target_os = "macos"))]
#[global_allocator]
static GLOBAL: mimalloc::MiMalloc = mimalloc::MiMalloc;

use std::cell::{Cell, RefCell};
use std::cmp;
use std::env;
use std::error::Error;
use std::rc::Rc;

use atoi::FromRadix10;
use nanorand::Rng;
use ntex::http::header::{CONTENT_LENGTH, CONTENT_TYPE, SERVER};
use ntex::util::Bytes;
use ntex::{http, web};
use serde::Serialize;
use tokio_postgres::{connect, Client, NoTls, Statement};

type AppResult<T> = Result<T, Box<dyn Error>>;

const PLAINTEXT_BODY: &[u8] = b"Hello, World!";

thread_local! {
    static POOL: RefCell<Option<Rc<PgPool>>> = const { RefCell::new(None) };
}

#[derive(Serialize)]
struct Message {
    message: &'static str,
}

#[derive(Clone, Serialize)]
struct World {
    id: i32,
    #[serde(rename = "randomNumber")]
    random_number: i32,
}

struct Fortune {
    id: i32,
    message: String,
}

struct PgClient {
    client: Client,
    select_world: Statement,
    select_fortunes: Statement,
    update_world: Statement,
}

impl PgClient {
    async fn connect(database_url: &str) -> Result<Self, tokio_postgres::Error> {
        let (client, connection) = connect(database_url, NoTls).await?;
        ntex::rt::spawn(async move {
            if let Err(error) = connection.await {
                eprintln!("postgres connection closed: {error}");
            }
        });

        let select_world = client
            .prepare("SELECT id, randomnumber FROM world WHERE id = $1")
            .await?;
        let select_fortunes = client.prepare("SELECT id, message FROM fortune").await?;
        let update_world = client
            .prepare("UPDATE world SET randomnumber = $1 WHERE id = $2")
            .await?;

        Ok(Self {
            client,
            select_world,
            select_fortunes,
            update_world,
        })
    }
}

struct PgPool {
    database_url: String,
    clients: RefCell<Vec<Rc<PgClient>>>,
    next: Cell<usize>,
}

impl PgPool {
    async fn connect(database_url: String, size: usize) -> Result<Self, tokio_postgres::Error> {
        let mut clients = Vec::with_capacity(size);
        for _ in 0..size {
            clients.push(Rc::new(PgClient::connect(&database_url).await?));
        }

        Ok(Self {
            database_url,
            clients: RefCell::new(clients),
            next: Cell::new(0),
        })
    }

    /// Round-robin client; a connection closed by the server or the network is replaced on first use.
    async fn get(&self) -> Result<Rc<PgClient>, tokio_postgres::Error> {
        let (index, client) = {
            let clients = self.clients.borrow();
            let index = self.next.get() % clients.len();
            self.next.set(index.wrapping_add(1));
            (index, clients[index].clone())
        };
        if !client.client.is_closed() {
            return Ok(client);
        }

        let replacement = Rc::new(PgClient::connect(&self.database_url).await?);
        self.clients.borrow_mut()[index] = replacement.clone();
        Ok(replacement)
    }
}

#[web::get("/plaintext")]
async fn plaintext() -> web::HttpResponse {
    let mut response = web::HttpResponse::with_body(
        http::StatusCode::OK,
        http::body::Body::Bytes(Bytes::from_static(PLAINTEXT_BODY)),
    );
    response
        .headers_mut()
        .insert(SERVER, http::header::HeaderValue::from_static("ntex"));
    response.headers_mut().insert(
        CONTENT_TYPE,
        http::header::HeaderValue::from_static("text/plain"),
    );
    response.headers_mut().insert(
        CONTENT_LENGTH,
        http::header::HeaderValue::from_static("13"),
    );
    response
}

#[web::get("/json")]
async fn json() -> web::HttpResponse {
    respond(json_response(&Message {
        message: "Hello, World!",
    }))
}

#[web::get("/db")]
async fn db() -> web::HttpResponse {
    respond(async { json_response(&find_random_world().await?) }.await)
}

#[web::get("/queries")]
async fn queries(req: web::HttpRequest) -> web::HttpResponse {
    let count = query_count(req.query_string());
    respond(async { json_response(&find_random_worlds(count).await?) }.await)
}

#[web::get("/updates")]
async fn updates(req: web::HttpRequest) -> web::HttpResponse {
    let count = query_count(req.query_string());
    respond(async { json_response(&update_worlds(count).await?) }.await)
}

#[web::get("/fortunes")]
async fn fortunes_handler() -> web::HttpResponse {
    respond(
        async {
            let items = fortunes().await?;
            let body = render_fortunes(&items);

            let mut response =
                web::HttpResponse::with_body(http::StatusCode::OK, body.into());
            response
                .headers_mut()
                .insert(SERVER, http::header::HeaderValue::from_static("ntex"));
            response.headers_mut().insert(
                CONTENT_TYPE,
                http::header::HeaderValue::from_static("text/html;charset=utf-8"),
            );
            Ok(response)
        }
        .await,
    )
}

#[ntex::main]
async fn main() -> std::io::Result<()> {
    println!("AVAILABLE CORES: {}", std::thread::available_parallelism().map_or(1, usize::from));

    let database_url = database_url();
    // connections per worker thread; ntex starts one worker per CPU
    let pool_size = env_usize("POSTGRES_POOL_SIZE", 8);

    ntex::server::build()
        .backlog(1024)
        .bind("fair-ntex-db", "0.0.0.0:8080", move |_| {
            let database_url = database_url.clone();
            async move {
                let initialized = POOL.with(|cell| cell.borrow().is_some());
                if !initialized {
                    // without a database at startup there is nothing to serve
                    let pool = PgPool::connect(database_url, pool_size)
                        .await
                        .expect("failed to connect to postgres at startup");
                    POOL.with(|cell| {
                        *cell.borrow_mut() = Some(Rc::new(pool));
                    });
                }

                http::HttpService::h1(
                    web::App::new()
                        .service(plaintext)
                        .service(json)
                        .service(db)
                        .service(queries)
                        .service(updates)
                        .service(fortunes_handler)
                        .finish(),
                )
            }
        })?
        .run()
        .await
}

async fn find_world(id: i32) -> AppResult<World> {
    let pg = pg_client().await?;
    let row = pg.client.query_one(&pg.select_world, &[&id]).await?;
    Ok(World {
        id: row.try_get(0)?,
        random_number: row.try_get(1)?,
    })
}

async fn find_random_world() -> AppResult<World> {
    find_world(random_world()).await
}

async fn find_random_worlds(count: usize) -> AppResult<Vec<World>> {
    let mut worlds = Vec::with_capacity(count);
    for _ in 0..count {
        worlds.push(find_random_world().await?);
    }
    Ok(worlds)
}

async fn update_worlds(count: usize) -> AppResult<Vec<World>> {
    let mut worlds = find_random_worlds(count).await?;
    for world in &mut worlds {
        world.random_number = random_world_excluding(world.random_number);
    }
    worlds.sort_by_key(|world| world.id);

    // all updates of the request go out together on one connection (the tokio-postgres equivalent of a JDBC batch)
    let pg = pg_client().await?;
    let pending = worlds.iter().map(|world| {
        let pg = pg.clone();
        async move {
            pg.client
                .execute(&pg.update_world, &[&world.random_number, &world.id])
                .await
        }
    });
    for result in ntex::util::join_all(pending).await {
        result?;
    }

    Ok(worlds)
}

async fn fortunes() -> AppResult<Vec<Fortune>> {
    let pg = pg_client().await?;
    let rows = pg.client.query(&pg.select_fortunes, &[]).await?;
    let mut items = Vec::with_capacity(rows.len() + 1);
    items.push(Fortune {
        id: 0,
        message: "Additional fortune added at request time.".to_string(),
    });
    for row in rows {
        items.push(Fortune {
            id: row.try_get(0)?,
            message: row.try_get(1)?,
        });
    }
    items.sort_by(|a, b| a.message.cmp(&b.message));
    Ok(items)
}

async fn pg_client() -> AppResult<Rc<PgClient>> {
    let pool = POOL
        .with(|cell| cell.borrow().clone())
        .ok_or("postgres pool is not initialized")?;
    Ok(pool.get().await?)
}

/// A failed request answers 500 and is logged; it never takes the worker or the process down.
fn respond(result: AppResult<web::HttpResponse>) -> web::HttpResponse {
    match result {
        Ok(response) => response,
        Err(error) => {
            eprintln!("request failed: {error}");
            let mut response = web::HttpResponse::with_body(
                http::StatusCode::INTERNAL_SERVER_ERROR,
                http::body::Body::Bytes(Bytes::from_static(b"Internal Server Error")),
            );
            response
                .headers_mut()
                .insert(SERVER, http::header::HeaderValue::from_static("ntex"));
            response.headers_mut().insert(
                CONTENT_TYPE,
                http::header::HeaderValue::from_static("text/plain"),
            );
            response
        }
    }
}

fn json_response<T: Serialize>(value: &T) -> AppResult<web::HttpResponse> {
    let body = serde_json::to_vec(value)?;
    let mut response = web::HttpResponse::with_body(http::StatusCode::OK, body.into());
    response
        .headers_mut()
        .insert(SERVER, http::header::HeaderValue::from_static("ntex"));
    response.headers_mut().insert(
        CONTENT_TYPE,
        http::header::HeaderValue::from_static("application/json"),
    );
    Ok(response)
}

fn render_fortunes(items: &[Fortune]) -> Vec<u8> {
    let mut body = String::with_capacity(1024 + items.len() * 64);
    body.push_str("<!DOCTYPE html><html><head><title>Fortunes</title></head><body><table><tr><th>id</th><th>message</th></tr>");
    for fortune in items {
        body.push_str("<tr><td>");
        body.push_str(&fortune.id.to_string());
        body.push_str("</td><td>");
        v_htmlescape::escape_string(&fortune.message, &mut body);
        body.push_str("</td></tr>");
    }
    body.push_str("</table></body></html>");
    body.into_bytes()
}

fn query_count(query: &str) -> usize {
    let value = query
        .split('&')
        .find_map(|part| part.strip_prefix("queries=").or_else(|| part.strip_prefix("q=")))
        .unwrap_or("1");
    let parsed = u16::from_radix_10(value.as_bytes()).0 as usize;
    cmp::min(500, cmp::max(1, parsed))
}

fn random_world() -> i32 {
    (nanorand::tls_rng().generate::<u32>() % 10_000 + 1) as i32
}

fn random_world_excluding(previous: i32) -> i32 {
    loop {
        let next = random_world();
        if next != previous {
            return next;
        }
    }
}

fn database_url() -> String {
    if let Ok(url) = env::var("POSTGRES_URL") {
        return url;
    }
    let host = env::var("POSTGRES_HOST").unwrap_or_else(|_| "localhost".to_string());
    let port = env::var("POSTGRES_PORT").unwrap_or_else(|_| "5432".to_string());
    let database = env::var("POSTGRES_DATABASE").unwrap_or_else(|_| "postgres".to_string());
    let user = env::var("POSTGRES_USER").unwrap_or_else(|_| "postgres".to_string());
    let password = env::var("POSTGRES_PASS")
        .or_else(|_| env::var("POSTGRES_PASSWORD"))
        .unwrap_or_else(|_| "postgres".to_string());
    format!("postgres://{user}:{password}@{host}:{port}/{database}")
}

fn env_usize(name: &str, fallback: usize) -> usize {
    env::var(name)
        .ok()
        .and_then(|value| value.parse::<usize>().ok())
        .filter(|value| *value > 0)
        .unwrap_or(fallback)
}
