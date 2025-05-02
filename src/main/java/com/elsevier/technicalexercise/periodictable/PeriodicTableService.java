package com.elsevier.technicalexercise.periodictable;

import java.util.List;
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
}
