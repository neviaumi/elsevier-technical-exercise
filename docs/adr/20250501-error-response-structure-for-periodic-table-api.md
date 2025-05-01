# Error Response Structure for Periodic Table API

- **Date**: 2025-05-01

## Context and Problem Statement

- **Business goal**:
  The technical exercise requires developing three API endpoints to retrieve data about elements in the periodic table. These endpoints must support:
  - Listing all elements
  - Filtering elements by group
  - Fetching a specific element by atomic number

- **Technical problem**:
  We need to define how to use HTTP Status and Response body to indicate errors while adopting Google JSON Style Guide.

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

### Error Structure Options
- **Option 1**: Flat error structure with code, reason, and message
- **Option 2**: Nested structure following full Google JSON Style Guide (with errors array)
- **Option 3**: HTTP status codes only (no standardized JSON body)

### HTTP Status Code Usage
- **Option A**: Using 400 for all client errors including not found resources
- **Option B**: Using specific status codes (404 for not found, 400 for validation errors)

---

## Decision Outcome

- **Chosen Options**: Option 1 (Flat error structure) and Option B (Specific HTTP status codes)

  - **Reasoning**: A flat error structure provides clarity and simplicity while maintaining enough information for both programmatic and human consumption. Using specific HTTP status codes properly follows RESTful principles and provides immediate context about the error type.

### Error Response Format Examples

#### Not Found (404) Response

```json
{
  "error": {
    "code": 404,
    "reason": "ERR_NOT_FOUND",
    "message": "Resource not found"
  }
}
```
