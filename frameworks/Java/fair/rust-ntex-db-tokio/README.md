# ntex + tokio-postgres Benchmark

TechEmpower `fair-ntex-db-tokio` implementation. A non-JVM reference point for the fair suite.

## Stack

- Framework: ntex `3.12.3` (tokio runtime feature)
- HTTP: ntex HTTP/1.1 server, one worker thread per CPU
- Database: `tokio-postgres` `0.7.18`, prepared statements, no TLS
- JSON: `serde_json`
- Templates: hand-written HTML with `v_htmlescape`
- Allocator: mimalloc
- Runtime: Rust `1.99` (image `rust:1.99`), dependencies pinned by `Cargo.lock`

## Execution model

- Every worker thread runs its own single-threaded async runtime and owns its own set of database connections.
- `POSTGRES_POOL_SIZE` is **per worker thread** (default 8): 24 workers open 192 connections.
- Connections are handed out round-robin. `tokio-postgres` can send several queries on one connection
  without waiting for the replies, so under high concurrency queries may be pipelined.
- A failed query answers `500 Internal Server Error` and is logged; a closed connection is replaced on its
  next use. The process only exits if the database is unreachable at startup.
- ntex sizes its worker pool from the host CPU count and ignores container CPU limits.

## Commands

```bash
cargo build --release --locked --features tokio
./target/release/fair-ntex-db
```

Docker image used by the benchmark (run from `frameworks/Java/fair`):

```bash
docker build -t fair-ntex-db-tokio -f fair-ntex-db-tokio.dockerfile .
```

After changing `Cargo.toml`, regenerate the lock file with `cargo update`, otherwise the `--locked` build fails.

## Configuration

```bash
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DATABASE=postgres
POSTGRES_USER=postgres
POSTGRES_PASS=postgres
POSTGRES_POOL_SIZE=8        # connections per worker thread
POSTGRES_URL=postgres://... # optional, overrides the five variables above
```

## Test URLs

```text
http://localhost:8080/plaintext
http://localhost:8080/json
http://localhost:8080/db
http://localhost:8080/queries?queries=5
http://localhost:8080/updates?queries=5
http://localhost:8080/fortunes
```
