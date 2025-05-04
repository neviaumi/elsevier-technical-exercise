# Elsevier technical exercise

## To start
```sh
docker compose up -d
SPRING_PROFILES_ACTIVE=development ./mvnw spring-boot:run
```

## To test
```sh
sh ./scripts/ci/test.sh
```

## To check style
```sh
./mvnw checkstyle:check
```

## To Check API Documentation with Swagger UI

- Start the server following [here](#to-start)
- In browser open [http://localhost:8080/v1/swagger-ui](http://localhost:8080/v1/swagger-ui)