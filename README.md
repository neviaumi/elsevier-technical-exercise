# Elsevier technical exercise

## To start
```sh
SPRING_PROFILES_ACTIVE=development ./mvnw spring-boot:run
```

## To test
```sh
./mvnw test
```

## To check style
```sh
./mvnw checkstyle:check
```

## To Check API Documentation with Swagger UI

- Start the server following [here](#to-start)
- In browser open [http://localhost:8080/v1/swagger-ui](http://localhost:8080/v1/swagger-ui)