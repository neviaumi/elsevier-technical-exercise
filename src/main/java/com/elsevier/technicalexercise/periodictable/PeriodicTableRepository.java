package com.elsevier.technicalexercise.periodictable;

import com.elsevier.technicalexercise.cloud.ObjectStorage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Repository for accessing periodic table element data.
 */
@Repository
class PeriodicTableRepository {
  private final String periodicTableBucket;
  private final String periodicTableKeyPath;
  private final ObjectStorage objectStorage;

  /**
   * Exception thrown when JSON mapping fails.
   */
  private static class JsonMappingException extends RuntimeException {
    public JsonMappingException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  /**
   * Exception thrown when group block parsing fails.
   */
  private static class InvalidGroupBlockException extends RuntimeException {
    public InvalidGroupBlockException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  /**
   * Constructs a new PeriodicTableRepository.
   *
   * @param objectStorage the object storage service
   */
  @Autowired
  public PeriodicTableRepository(ObjectStorage objectStorage,
                                 @Value("${periodic-table.bucket}") String bucketName,
                                 @Value("${periodic-table.key}") String objectKeyPath
  ) {
    this.objectStorage = objectStorage;
    this.periodicTableBucket = bucketName;
    this.periodicTableKeyPath = objectKeyPath;
  }

  public CompletableFuture<PeriodicTableEntity> getPeriodicTable() {
    return this.objectStorage.getObject(this.periodicTableBucket, this.periodicTableKeyPath)
        .thenApply(resp -> {
          ObjectMapper mapper = new ObjectMapper();
          try {
            List<Map<String, Object>> list = mapper.readValue(
                resp.content(),
                new TypeReference<>() {
                }
            );
            return new PeriodicTableEntity(list, resp.etag());
          } catch (Exception e) {
            throw new JsonMappingException(
                "Error on mapping the object on ObjectStorage to JSON : " + e.getMessage(), e);
          }
        });
  }

  public CompletableFuture<?> updatePeriodicTable(
      PeriodicTableEntity periodicTableEntity) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      byte[] content = mapper.writeValueAsBytes(periodicTableEntity.data());
      return this.objectStorage.replaceObject(this.periodicTableBucket, this.periodicTableKeyPath,
          content, periodicTableEntity.etag());
    } catch (Exception e) {
      throw new JsonMappingException(
          "Error on mapping the object to JSON : " + e.getMessage(), e);
    }


  }

  /**
   * Finds all elements in the periodic table.
   *
   * @return a future that will complete with the list of elements
   */
  public CompletableFuture<List<ElementEntity>> findElements() {
    return this.objectStorage.getObject(
            this.periodicTableBucket, this.periodicTableKeyPath)
        .thenApply(resp -> {
          ObjectMapper mapper = new ObjectMapper();
          mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
          mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
          try {
            return mapper.readValue(
                resp.content(), new TypeReference<>() {
                });
          } catch (Exception e) {
            throw new JsonMappingException(
                "Error on mapping the object on ObjectStorage to JSON : " + e.getMessage(), e);
          }
        });
  }

  /**
   * Finds elements by group.
   *
   * @param group the group to filter by
   * @return a future that will complete with the filtered list of elements
   */
  public CompletableFuture<List<ElementEntity>> findElements(String group) {
    return this.findElements()
        .thenApply(elements -> elements.stream()
            .filter(element -> {
              try {
                String[] groups = element.groupBlock().split(",");
                String groupBlockNumber = groups[0].strip().split(" ")[1];
                String groupBlock = groups[1].strip();
                return groupBlockNumber.equals(group) || groupBlock.equals(group);
              } catch (Exception e) {
                throw new InvalidGroupBlockException(
                    "Invalid group block : " + e.getMessage(), e);
              }
            })
            .toList());
  }

  /**
   * Gets an element by atomic number.
   *
   * @param atomicNumber the atomic number of the element
   * @return a future that will complete with the element
   */
  public CompletableFuture<Optional<ElementEntity>> getElement(int atomicNumber) {
    return this.findElements()
        .thenApply(elements -> elements.stream()
            .filter(element -> element.atomicNumber() == atomicNumber)
            .findFirst()
        );
  }
}
