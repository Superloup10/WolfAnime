FROM rust:1.75-bookworm AS rust_builder
LABEL org.opencontainers.image.authors="Superloup10@gmail.com"

WORKDIR /usr/src/rust_lib
COPY --chown=rust:rust back/src/main/rust/Cargo.toml .
COPY --chown=rust:rust back/src/main/rust/build.rs .
COPY --chown=rust:rust back/src/main/rust/src ./src

RUN cargo build --release

FROM gradle:8.6-jdk21 AS gradle_builder
LABEL org.opencontainers.image.authors="Superloup10@gmail.com"

WORKDIR /home/gradle/src

COPY --chown=gradle:gradle . .
RUN gradle buildFatJar --no-daemon --build-cache

FROM eclipse-temurin:21.0.1_12-jdk
LABEL org.opencontainers.image.authors="Superloup10@gmail.com"
EXPOSE 8080
RUN mkdir /app
COPY --from=rust_builder /usr/src/rust_lib/target/release/librust_lib.so /app/libs/
COPY --from=gradle_builder /home/gradle/src/back/build/libs/*-fat.jar /app/wolfanime.jar
ENTRYPOINT ["java", "-Djava.library.path=/app/libs/", "-jar", "/app/wolfanime.jar"]
