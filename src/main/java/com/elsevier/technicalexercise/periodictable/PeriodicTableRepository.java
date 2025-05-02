package com.elsevier.technicalexercise.periodictable;

import com.elsevier.technicalexercise.cloud.ObjectStorage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Repository for accessing periodic table element data.
 */
@Repository
class PeriodicTableRepository {
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
   * Exception thrown when an element is not found.
   */
  private static class ElementNotFoundException extends RuntimeException {
    public ElementNotFoundException(String message) {
      super(message);
    }
  }

  /**
   * Constructs a new PeriodicTableRepository.
   *
   * @param objectStorage the object storage service
   */
  @Autowired
  public PeriodicTableRepository(ObjectStorage objectStorage) {
    this.objectStorage = objectStorage;
  }

  /**
   * Finds all elements in the periodic table.
   *
   * @return a future that will complete with the list of elements
   */
  public CompletableFuture<List<ElementEntity>> findElements() {
    return this.objectStorage.getObject(
        "elsevier-technical-exercise", "periodic_table.json")
        .thenApply(resp -> {
          ObjectMapper mapper = new ObjectMapper();
          mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
          mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
          try {
            return mapper.readValue(
                resp.content(), new TypeReference<List<ElementEntity>>() {});
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
   * @throws ElementNotFoundException if the element is not found
   */
  public CompletableFuture<ElementEntity> getElement(int atomicNumber) {
    return this.findElements()
        .thenApply(elements -> elements.stream()
            .filter(element -> element.atomicNumber() == atomicNumber)
            .findFirst()
            .orElseThrow(() -> new ElementNotFoundException(
                "Element not found for atomic number: " + atomicNumber)));
  }
}
