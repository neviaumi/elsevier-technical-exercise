# Persistence for Periodic Table

- **Date**: 2025-05-01

## Context and Problem Statement

- **Business goal**:
  The technical exercise requires developing backend to retrieve data about elements in the periodic table.

- **Technical problem**:
  We need a storage solution to persist periodic table data that can handle occasional updates and efficient queries.

- **Scope**:
  This decision governs the persistence strategy for the periodic table data.


---

## Decision Drivers
- **Distribution**: The selected solution should enable stateless application design
- **Concurrency**: The solution should support update operations without race-conditions
- **Complexity**: How much effort it takes when integrating into the application
- **Flexibility**: Support for local development and cloud deployment
---

## Considered Options

### Persistence Strategy Options
- **Option 1**: Object Storage with JSON (S3 or compatible)
  - Store complete periodic table as JSON file in object storage
  - Use ETag for optimistic concurrency control
  - Cache data in application memory
- **Option 2**: Relational Database with JPA/Hibernate
  - Create normalized schema for elements
  - Use Spring Data JPA for object-relational mapping
  - Implement database transactions for updates
- **Option 3**: Local Filesystem JSON Storage
  - Store data as a JSON file on the local disk
  - Use file-level locking or checksums for safe concurrent updates
  - Suitable for development and small-scale deployments

---

## Decision Outcome

### Persistence Strategy
- **Chosen Option**: Option 1 (Object Storage with JSON)
  - **Reasoning**: Given the static nature of periodic table data (rarely updated), an object storage solution provides simplicity while meeting requirements. The dataset is small enough to load into memory, and ETag-based concurrency control addresses update concerns with minimal complexity.

---
## Analysis of the Options

### Option 1: Object Storage with JSON (S3 or compatible)
- **Good, because** it’s simple to implement and maintain for mostly-read, rarely-updated, relatively small datasets.
- **Good, because** S3 and similar storages are already highly available and easy to use in cloud environments.
- **Good, because** ETag-based concurrency control helps prevent conflicting updates with low complexity.
- **Good, because** it migrates easily between local and cloud (with solutions such as LocalStack).
- **Bad, because** does not support rich queries or transactional updates—requires loading the whole file for any update.
- **Bad, because** managing complex data evolution (e.g., migrations/partial updates) is trickier than with a database.

### Option 2: Relational Database with JPA/Hibernate
- **Good, because** it provides powerful querying, strong transactional guarantees, and mature tools.
- **Good, because** it easily supports more sophisticated business logic and partial updates.
- **Bad, because** setup and maintenance add operational overhead for a simple dataset.
- **Bad, because** persistence layer and ORM add complexity/layering that may be overkill for this use-case.
- **Bad, because** local development requires running a database server.

### Option 3: Local Filesystem JSON Storage
- **Good, because** it’s extremely easy to set up—just read and write files.
- **Good, because** fast for small datasets and requires minimal dependencies.
- **Bad, because** not suitable for cloud or distributed environments.
- **Bad, because** handling concurrency and consistency is manual and error-prone.
- **Bad, because** scaling is limited to local deployments.
