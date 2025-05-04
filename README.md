# Elsevier technical exercise

---

## Project Overview

This repository contains my solution for the Elsevier technical exercise.

For more details about the planning and the steps taken, please refer to
the [planning notes in Notion](https://www.notion.so/Elsevier-Technical-Exercise-1e546f1119ae8095b6e8e19bc1c27b50?pvs=4).

**Summary:**  
The main goal of this project is to create an API that lets users:

- List all elements in the periodic table (showing their names and atomic numbers)
- Filter elements by group
- Get detailed information about a specific element by its atomic number

The periodic table data may be updated from time to time with minor changes.

---

## Project Assumptions

### AWS Hosting

To make the application easy to scale and manage, I assume it will be hosted on AWS. AWS offers reliable and proven
options for storing and updating application data.

### Infrequent Data Updates

The data in the periodic table is not expected to change very often. Updates will usually be made by one authorized
person. It is very unlikely for two people to update the data at the same time.

---

## Key Decisions Making

### Persistence Layer for the Periodic Table

I chose to use **AWS S3** for storing the periodic table data because it is low-cost, distributed, reliable, and can
handle concurrent updates to some degree.

For more details about the decision process and alternative options considered, see
the [ADR here](./docs/adr/20250501-persistence-for-periodic-table.md).

### Handling Periodic Table Updates

To handle concurrent updates on the same file, I use the **etag** feature of AWS S3. This approach is simple to
implement, helps prevent race conditions, and leverages built-in AWS capabilities.

For more details about this decision and alternatives considered, see
the [ADR here](./docs/adr/20250503-handle-update-for-periodic-table.md).

---

## Review Guideline

This section offers guidance for reviewing the submission, including project stack, style conventions, application
running instructions, testing strategies, and code organization.

### Stack

- **Java** (Spring Boot MVC)
- **AWS S3** (for persistent storage)

### Style Guide

- **API Responses**: Follows
  the [Google JSON Style Guide](https://google.github.io/styleguide/jsoncstyleguide.xml#JSON_Structure_&_Reserved_Property_Names).
    - Implementation
      examples: [SuccessResponseDto](./src/main/java/com/elsevier/technicalexercise/api/SuccessResponseDto.java), [ErrorResponseDto](./src/main/java/com/elsevier/technicalexercise/api/ErrorResponseDto.java)
- **Java Code Style**: Adheres to the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) with
  a pre-configured [CheckStyle](https://checkstyle.sourceforge.io/google_style.html) setup.

### Running the Application

**Requirements:**

- Java JDK v24
- Docker Compose

**To start the application:**

```sh
docker compose up -d
SPRING_PROFILES_ACTIVE=development ./mvnw spring-boot:run
```

**To run tests:**

```sh
sh ./scripts/ci/test.sh
```

**To check style compliance:**

```sh
./mvnw checkstyle:check
```

**To view API documentation (Swagger UI):**

- Start the server as described above
- Open [http://localhost:8080/v1/swagger-ui](http://localhost:8080/v1/swagger-ui) in your browser

### Testing Guide

- Use `@SpringBootTest` for integration tests involving API endpoints and modules that connect with third-party
  dependencies (e.g., Databases or Cloud Provider SDKs).
    - Requires `docker compose` running to provide any needed services locally.
- Use `@ExtendWith(MockitoExtension.class)` for unit testing other modules, isolating dependencies using mocks. These
  tests focus on edge cases and verify behavior consistency with controlled outputs.

### Project Logical Layers

In the context of Spring Boot, the code is organized into logical layers to separate responsibilities. Each layer
typically uses a consistent naming convention:

#### Controller

- **Purpose**: Entry point for API calls. Handles path definitions, variables, and response formats.
- **Responsibility**: Receives API calls, transforms input/output as needed, delegates business logic to Services.
- **Naming**: Classes end with `Controller`.

#### Service

- **Purpose**: Contains business logic.
- **Responsibility**: Processes input from Controllers, coordinates with Repositories as necessary, and returns results.
- **Naming**: Classes end with `Service`.

#### Repository

- **Purpose**: Handles persistence concerns (querying and mutating data).
- **Responsibility**: Interacts with storage layers such as databases or S3.
- **Naming**: Classes end with `Repository`.

#### Entity

- **Purpose**: Represents the data model reflecting the persisted structure.
- **Responsibility**: Serves as the mapping between Java objects and persisted data.
- **Naming**: Classes end with `Entity`.

#### DTO (Data Transfer Object)

- **Purpose**: Structures data for API request/response.
- **Responsibility**: Maps between API inputs/outputs and Java types; may include validation annotations.
- **Naming**: Classes end with `Dto`.

## Possible Improvements and Next Steps

Given more time, I would consider implementing:

- **Structured Logging**: Use JSON Lines format for logs instead of the default human-readable one, making log search
  and streaming more efficient.
- **Deployment**: Deploy the application to a suitable environment (cloud or on-premises) for public access or
  real-world use.
- **Caching**: Add in-memory caching of the periodic table to avoid fetching from S3 on every request.
- **Test Output Clarity**: Reduce or filter startup/test-phase warnings to make it easier to spot actual test failures
  or errors.
- **Monitoring**: Add a health check endpoint to enable external monitoring of service availability.