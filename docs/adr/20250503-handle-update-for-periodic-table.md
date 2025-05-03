# Handle Updates for Periodic Table

- **Date**: 2025-05-01

## Context and Problem Statement

- **Business goal**:  
  The technical exercise requires that the periodic table supports periodic minor updates.

- **Technical problem**:  
  We have chosen S3 for storage of the periodic table. While S3 is not ACID compliant, this raises questions about how to handle race conditions and versioning.

- **Scope**:  
  This decision governs the update strategy for the periodic table data.

---

## Decision Drivers

- Implementation complexity
- Approaches to handling concurrency (rejection or waiting)

---

## Considered Options

### Versioning

- S3 versioning for backup
- Manually copy the existing object before putting the new object

### Race Condition

- ETag-based optimistic locking
- Distributed process lock

---

## Decision Outcome

- **Chosen Options:**
    - Versioning: S3 versioning for backup
    - Race condition: ETag-based optimistic locking

- **Reasoning:** These options provide the most straightforward and maintainable approach for handling infrequent, human-driven updates while ensuring data integrity and auditability.

### Positive Consequences

- Automated version management with no custom backup logic required
- Simplified rollback capabilities for error recovery
- Clean integration with S3's lifecycle rules for version cleanup
- No additional infrastructure dependencies needed
- Optimistic locking prevents data corruption without complex distributed systems

### Negative Consequences

- S3 PUT operations with conditional checks are slightly more complex to implement
- Update failures require client-side retry logic

---

## Risks and Mitigations

- **Risk 1:** Concurrent updates could lead to frequent conflicts with ETag-based locking
    - **Mitigation:** Due to the low frequency of updates to the periodic table, this risk is minimal. The client UI can provide clear feedback when an update conflict occurs and allow users to refresh and retry.

- **Risk 2:** Accumulation of many object versions could increase storage costs
    - **Mitigation:** Implement S3 lifecycle policies to automatically remove versions older than a specified threshold (e.g., 90 days).

- **Risk 3:** Loss of S3 access could prevent updates
    - **Mitigation:** Follow standard AWS infrastructure resilience practices, including proper IAM configuration and monitoring.

---

## Analysis of the Options

### Versioning Options

#### S3 versioning for backup

- **Good, because** it leverages native AWS capabilities without requiring any custom code for backups.
- **Good, because** it automatically tracks all changes with useful metadata (timestamps, AWS principal), enabling strong auditability.
- **Good, because** it provides simple rollback functionality through the AWS console or API, making error recovery easy.
- **Good, because** version management can be automated with S3 lifecycle policies, ensuring old versions are pruned without manual intervention.
- **Bad, because** if there are many updates, storage costs may increase unless lifecycle policies are properly configured.

#### Manually copy existing object before update

- **Good, because** it gives explicit control over when and what is backed up.
- **Good, because** it is portable to other storage providers; not tied to S3-specific features.
- **Bad, because** it requires additional custom logic for both backup and restore operations.
- **Bad, because** there's a higher risk of human error or logic bugs leading to inconsistent backups or failed restores.
- **Bad, because** operational complexity increases, particularly for naming, tracking, and cleaning up backups.

### Race Condition Options

#### ETag-based optimistic locking

- **Good, because** it integrates cleanly with S3's conditional update features (If-Match headers), requiring no additional infrastructure or services.
- **Good, because** it is an established and reliable pattern for handling concurrent modifications.
- **Good, because** it prevents silent data overwrites, instead signaling clients to retry if a conflict occurs.
- **Good, because** implementation is simple and has minimal maintenance overhead.
- **Bad, because** client-side logic must handle and retry failed updates, adding a bit of complexity.
- **Bad, because** users may potentially lose their in-progress changes if someone else updates first, requiring refresh and retry.

#### Distributed process lock

- **Good, because** it enables true queuing and serialization of updates for hard guarantees on order.
- **Good, because** it can prevent update failures due to conflicts by making others wait in line.
- **Bad, because** it introduces notable infrastructure complexity (e.g., requires Redis, DynamoDB, or another distributed store).
- **Bad, because** distributed locks are harder to get right and add new points of failure or contention.
- **Bad, because** it's overkill for a system where updates are expected to be very infrequent.
