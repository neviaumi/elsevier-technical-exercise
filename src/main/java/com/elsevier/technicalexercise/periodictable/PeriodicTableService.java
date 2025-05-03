package com.elsevier.technicalexercise.periodictable;

import com.elsevier.technicalexercise.utils.Validator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for accessing periodic table element data.
 */
@Service
class PeriodicTableService {
  private final PeriodicTableRepository periodicTableRepository;

  public static class ElementNotFoundException extends RuntimeException {
    public ElementNotFoundException(String message) {
      super(message);
    }
  }

  /**
   * Constructs a new PeriodicTableService.
   *
   * @param periodicTableRepository the repository for periodic table data
   */
  @Autowired
  public PeriodicTableService(PeriodicTableRepository periodicTableRepository) {
    this.periodicTableRepository = periodicTableRepository;
  }

  /**
   * Finds all elements in the periodic table.
   *
   * @return a future that will complete with the list of elements
   */
  public CompletableFuture<List<ElementEntity>> findElements() {
    return periodicTableRepository.findElements();
  }

  /**
   * Finds elements by group.
   *
   * @param group the group to filter by
   * @return a future that will complete with the filtered list of elements
   */
  public CompletableFuture<List<ElementEntity>> findElements(String group) {
    return periodicTableRepository.findElements(group);
  }

  /**
   * Gets an element by atomic number.
   *
   * @param atomicNumber the atomic number of the element
   * @return a future that will complete with the element
   */
  public CompletableFuture<ElementEntity> getElement(int atomicNumber) {
    return periodicTableRepository.getElement(atomicNumber).thenApply(
        elementEntity -> elementEntity.orElseThrow(
            () -> new ElementNotFoundException(
                "Element not found for atomic number: " + atomicNumber))
    );
  }

  public CompletableFuture<PeriodicTableEntity> updatePeriodicTable(
      List<PatchElementDto> patchElements) {
    return periodicTableRepository.getPeriodicTable()
        .thenApply(
            periodicTableEntity -> new PeriodicTableEntity(
                periodicTableEntity.data().stream().map(existingElement -> {
                  PatchElementDto newElementHaveToUpdate =
                      patchElements.stream().filter(newElement -> {
                        return newElement.getAtomicNumber()
                            ==
                            Integer.parseInt(String.valueOf(existingElement.get("atomic_number")));
                      }).findFirst().orElse(null);
                  if (newElementHaveToUpdate == null) {
                    return existingElement;
                  }
                  Map<String, Object> mergeElement = new HashMap<>(existingElement);
                  if (Validator.isNotNullOrBlank(newElementHaveToUpdate.getName())) {
                    mergeElement.put("name", newElementHaveToUpdate.getName());
                  }
                  if (Validator.isNotNullOrBlank(newElementHaveToUpdate.getAlternativeName())) {
                    mergeElement.put("alternative_name",
                        newElementHaveToUpdate.getAlternativeName());
                  }
                  if (Validator.isNotNullOrBlank(newElementHaveToUpdate.getGroupBlock())) {
                    mergeElement.put("group_block", newElementHaveToUpdate.getGroupBlock());
                  }
                  return mergeElement;
                }).toList(), periodicTableEntity.etag())
        )
        .thenCompose((periodicTableEntity) -> {
          return periodicTableRepository.updatePeriodicTable(periodicTableEntity)
              .thenApply((ignored) -> periodicTableEntity);
        });
  }
}
