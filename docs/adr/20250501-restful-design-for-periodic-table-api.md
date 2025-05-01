# RESTful Resource Design for Periodic Table API

- **Date**: 2025-05-01

## Context and Problem Statement

- **Business goal**: [what business objective is driving the decision?]
The technical exercise requires designing three API endpoints to retrieve data about elements in the periodic table. These endpoints must support:
  - Listing all elements
  - Filtering elements by group
  - Fetching a specific element by atomic number



- **Technical problem**: [what limitations or challenges need to be solved?]
A clear and RESTful path structure is needed, particularly for the main listing endpoint as well as for filtering and retrieving individual elements.

- **Scope**:
This decision governs the structure of API endpoints related to periodic table elements, specifically focusing on resource naming and access patterns.


---

## Decision Drivers
- **Intuitive Design**: The API paths should be logical and self-explanatory for developers new to the API
- **RESTful Compliance**: Endpoints should follow established RESTful API best practices
- **Flexibility**: The design should accommodate future extensions and additional filtering options
- **Semantic Accuracy**: Resource names and structure should accurately represent the domain concepts

---

## Considered Options

### Resource Name Options
- **Option 1**: Using `periodic-table` as the resource name (e.g., `/periodic-table`)
- **Option 2**: Using `elements` as the resource name (e.g., `/elements`)

### Filtering Approach Options
- **Option 1**: Using query string parameters for filtering (e.g., `/elements?group=alkali-metals`)
- **Option 1**: Using path parameters for filtering (e.g., `/groups/alkali-metals/elements`)

---

## Decision Outcome

- **Chosen Option**: Option 2 (Resource Name: `elements`) and Option 1 (Filtering: Query String)

    - **Reasoning**: The resource name `elements` follows RESTful conventions by using a plural noun that directly represents the resources being accessed. For filtering, query string parameters provide the most flexible and standard approach that clearly represents the filtering operation rather than implying a resource hierarchy.

### Final API Endpoints
- List all elements: `GET /elements`
- Filter elements by group: `GET /elements?group={group-name}`
- Get specific element: `GET /elements/{atomic-number}`

### Positive Consequences
- Endpoints follow RESTful conventions and are intuitive for API consumers
- The API structure is flexible and can be easily extended with additional filters
- Resource naming avoids exposing implementation details of the periodic table structure
- Query string approach allows for multiple filter combinations in the future

### Negative Consequences
- Limited opportunity to represent strong hierarchical relationships if they emerge later

---

## Analysis of the Options

### Resource Name Options

#### Option 1: `/periodic-table`
- **Good, because** it directly references the domain concept, making it descriptive for users unfamiliar with chemistry
- **Good, because** it allows for future endpoints related to the table itself, not just the elements
- **Bad, because** it's less conventional for RESTful APIs, which typically use plural nouns
- **Bad, because** it's more verbose and potentially exposes implementation details

#### Option 2: `/elements`
- **Good, because** it follows REST conventions of using plural nouns for collections
- **Good, because** it has direct meaning - users understand `/elements` will list individual elements
- **Good, because** it works well for both collection and single-resource endpoints
- **Good, because** it's more scalable if expanding to other scientific resources
- **Bad, because** it assumes some domain knowledge from the user

### Filtering Approach Options

#### Option 1: Query String Parameters (`/elements?group=alkali-metals`)
- **Good, because** it's the standard way to filter or search within a collection in RESTful APIs
- **Good, because** it clearly indicates you're requesting a filtered view of a collection
- **Good, because** it easily supports combining multiple filters
- **Good, because** it's common in popular APIs and expected by developers
- **Bad, because** it can make URLs lengthy when many filters are applied

#### Option 2: Path Parameters (`/groups/alkali-metals/elements`)
- **Good, because** it can represent a strong ownership or containment hierarchy
- **Good, because** it results in cleaner URLs without query strings
- **Bad, because** it implies groups "own" elements, which isn't conceptually accurate
- **Bad, because** it's less flexible for combinations of filters
- **Bad, because** it creates potentially misleading resource hierarchies

---

## Links <!-- optional -->

- [Link type] [Link to ADR]
<!-- Example: Refined by [ADR-0005](0005-example.md) -->
- … <!-- numbers of links can vary -->

---