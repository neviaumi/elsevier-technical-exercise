package com.elsevier.technicalexercise.periodictable;

import com.elsevier.technicalexercise.api.ErrorResponseDto;
import com.elsevier.technicalexercise.api.SuccessResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for accessing periodic table element data.
 */
@RestController
class ElementController {
  private final PeriodicTableService periodicTableService;

  public static class PatchElementSizeException extends RuntimeException {
    public PatchElementSizeException(String s) {
      super(s);
    }
  }

  /**
   * Constructs a new ElementController.
   *
   * @param periodicTableService the service for periodic table data
   */
  public ElementController(PeriodicTableService periodicTableService) {
    this.periodicTableService = periodicTableService;
  }

  @GetMapping("/elements")
  @ResponseBody
  public CompletableFuture<SuccessResponseDto<SuccessResponseDto.Items<ElementDto>>> findElements(
      @ParameterObject
      @Valid @ModelAttribute
      ElementListingRequestDto elementListingRequestDto) {

    return this.periodicTableService.findElements(elementListingRequestDto)
        .thenApply(resp -> SuccessResponseDto.fromListOfItems(
            resp.stream()
                .map(ElementDto::fromElement)
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
  public CompletableFuture<SuccessResponseDto<ElementDetailDto>> getElement(
      @PathVariable int atomicNumber) {
    return this.periodicTableService.getElement(atomicNumber)
        .thenApply(ElementDetailDto::fromElement)
        .thenApply(SuccessResponseDto::fromSingleItem);
  }

  @PatchMapping("/elements")
  @ResponseBody
  public CompletableFuture<ResponseEntity<?>> updatePeriodicTable(
      @RequestBody @Size(min = 1, message = "At least one element is required")
      @Valid List<ElementPatchRequestDto> patchElements) {
    List<ElementPatchRequestDto> validElements = patchElements.stream().filter(
        (element) -> !element.isEmpty()
    ).toList();
    if (validElements.isEmpty()) {
      throw new PatchElementSizeException(
          "Validation failed, Element must minimum 1 field to update"
      );
    }
    return this.periodicTableService.updatePeriodicTable(validElements).thenApply((resp) -> {
      HttpHeaders headers = new HttpHeaders();
      headers.add("ETag", resp.etag());
      return new ResponseEntity<>(null, headers, HttpStatus.NO_CONTENT);
    });
  }

  @ExceptionHandler(PatchElementSizeException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorResponseDto handleValidationExceptions(PatchElementSizeException ex) {
    return ErrorResponseDto.fromException(HttpStatus.BAD_REQUEST,
        PatchElementSizeException.class.getSimpleName(),
        ex);
  }

  @ExceptionHandler(PeriodicTableService.ElementNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ErrorResponseDto handleElementNotFoundException(
      PeriodicTableService.ElementNotFoundException ex) {
    return ErrorResponseDto.fromException(HttpStatus.NOT_FOUND,
        PeriodicTableService.ElementNotFoundException.class.getSimpleName(),
        ex);
  }
}
