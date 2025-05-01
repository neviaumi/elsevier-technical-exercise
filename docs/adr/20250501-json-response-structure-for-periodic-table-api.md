# JSON Response Structure for Periodic Table API

- **Date**: 2025-05-01

## Context and Problem Statement

- **Business goal**: 
The technical exercise requires developing three API endpoints to retrieve data about elements in the periodic table. These endpoints must support:
  - Listing all elements
  - Filtering elements by group
  - Fetching a specific element by atomic number

- **Technical problem**: 
We need to define a standard JSON response structure that is consistent, extensible, and follows industry best practices. Specifically, we need to decide on the format for returning resources, handling errors, and organizing metadata.

- **Scope**:
This decision governs the JSON structure for all API responses in the project.


---

## Decision Drivers
- **Consistency**: Responses should follow a predictable pattern
- **Extensibility**: Response structure should allow for future additions (pagination, metadata, etc.)
- **Error handling**: Clear separation between successful and error responses
- **Developer experience**: Easy extraction of response data by API consumers
- **Industry standards**: Following established practices where appropriate
---

## Considered Options

### Response Structure Options
- **Option 1**: Returning bare resources (no wrapper)
- **Option 2**: Using a `data` wrapper key for resources
- **Option 3**: Following full JSON:API specification

### Style Guide Options
- **Option 1**: Google JSON Style Guide
- **Option 2**: JSON:API Specification

---

## Decision Outcome

- **Chosen Option**: Option 2 (using a `data` wrapper key) following Google JSON Style Guide principles

    - **Reasoning**: The `data` wrapper provides a consistent extraction point for consumers while allowing for future extensions. The Google JSON Style Guide offers a pragmatic approach that balances simplicity with structure.

### Response Format Examples

#### Success Response (Collection)
```json
{
  "data": {"items": [
    {
      "atomicNumber": 1,
      "name": "Hydrogen",
      "symbol": "H",
      "group": "nonmetal"
    },
    {
      "atomicNumber": 2,
      "name": "Helium",
      "symbol": "He",
      "group": "noble-gas"
    }
  ]}
}
```

#### Success Response (Single Item)
```json
{
  "data":  {
      "atomicNumber": 1,
      "name": "Hydrogen",
      "symbol": "H",
      "group": "nonmetal"
    }
  
}
```