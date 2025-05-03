package com.elsevier.technicalexercise.periodictable;

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
    return periodicTableRepository.getElement(atomicNumber);
  }

  public CompletableFuture<PeriodicTableEntity> updatePeriodicTable(
      List<PatchElementDto> patchElements) {
    return periodicTableRepository.getPeriodicTable()
        .thenApply(
            periodicTableEntity -> new PeriodicTableEntity(
                periodicTableEntity.data().stream().map(existingElement -> {
                  PatchElementDto newElementHaveToUpdate =
                      patchElements.stream().filter(newElement -> {
                        return newElement.atomicNumber()
                            ==
                            Integer.parseInt(String.valueOf(existingElement.get("atomic_number")));
                      }).findFirst().orElse(null);
                  if (newElementHaveToUpdate == null) {
                    return existingElement;
                  }
                  Map<String, Object> mergeElement = new HashMap<>(existingElement);
                  if (newElementHaveToUpdate.name() != null) {
                    mergeElement.put("name", newElementHaveToUpdate.name());
                  }
                  if (newElementHaveToUpdate.alternativeName() != null) {
                    mergeElement.put("alternative_name", newElementHaveToUpdate.alternativeName());
                  }
                  if (newElementHaveToUpdate.groupBlock() != null) {
                    mergeElement.put("group_block", newElementHaveToUpdate.groupBlock());
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
