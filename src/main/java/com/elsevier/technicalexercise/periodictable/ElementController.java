package com.elsevier.technicalexercise.periodictable;

import com.elsevier.technicalexercise.api.SuccessResponseDTO;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for accessing periodic table element data.
 */
@RestController
class ElementController {
  private final PeriodicTableService periodicTableService;

  /**
   * Constructs a new ElementController.
   *
   * @param periodicTableService the service for periodic table data
   */
  public ElementController(PeriodicTableService periodicTableService) {
    this.periodicTableService = periodicTableService;
  }

  /**
   * Finds elements, optionally filtered by group.
   *
   * @param group the group to filter by (optional)
   * @return a future that will complete with the response containing the elements
   */
  @GetMapping("/elements")
  @ResponseBody
  public CompletableFuture<SuccessResponseDTO> findElements(
      @RequestParam(required = false) String group) {
    CompletableFuture<java.util.List<ElementEntity>> elementsFuture = group != null 
        ? this.periodicTableService.findElements(group) 
        : this.periodicTableService.findElements();

    return elementsFuture.thenApply(resp -> SuccessResponseDTO.fromListOfItems(
        resp.stream()
            .map(ElementDTO::fromElement)
            .toList()));
  }

  /**
   * Gets an element by atomic number.
   *
   * @param atomicNumber the atomic number of the element
   * @return a future that will complete with the response containing the element
   */
  @GetMapping("/elements/{atomicNumber}")
  @ResponseBody
  public CompletableFuture<SuccessResponseDTO> getElement(@PathVariable int atomicNumber) {
    return this.periodicTableService.getElement(atomicNumber)
        .thenApply(ElementDetailDTO::fromElement)
        .thenApply(SuccessResponseDTO::fromSingleItem);
  }
}
