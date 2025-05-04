FROM bellsoft/liberica-runtime-container:jdk-24-musl AS base
WORKDIR /app

COPY ./src ./src/
COPY ./mvnw ./mvnw.cmd ./pom.xml ./
COPY ./.mvn ./.mvn/
COPY ./scripts/docker/ ./scripts/docker/

FROM base AS builder
RUN sh ./scripts/docker/build.sh

FROM bellsoft/liberica-runtime-container:jre-24-slim-musl
WORKDIR /app

COPY --from=builder /app/scripts/ ./scripts/
COPY --from=builder /app/target/technicalexercise-latest.jar ./target/