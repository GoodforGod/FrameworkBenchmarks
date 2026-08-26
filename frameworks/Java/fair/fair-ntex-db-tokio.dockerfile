FROM rust:1.98

RUN apt-get update -yqq && apt-get install -yqq cmake g++

COPY rust-ntex-db-tokio /app
WORKDIR /app

RUN cargo build --release --features tokio

ENV POSTGRES_HOST="tfb-database"
ENV POSTGRES_PORT="5432"
ENV POSTGRES_DATABASE="hello_world"
ENV POSTGRES_USER="benchmarkdbuser"
ENV POSTGRES_PASS="benchmarkdbpass"
ENV POSTGRES_POOL_SIZE="32"

EXPOSE 8080

CMD ["./target/release/fair-ntex-db"]
