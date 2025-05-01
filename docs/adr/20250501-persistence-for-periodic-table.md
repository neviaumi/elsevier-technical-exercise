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
