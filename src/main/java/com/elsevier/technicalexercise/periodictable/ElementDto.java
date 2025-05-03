package com.elsevier.technicalexercise.periodictable;

/**
 * Data transfer object for basic element information.
 */
public record ElementDto(String name, int atomicNumber) {
  static ElementDto fromElement(ElementEntity element) {
    String alternativeName = element.alternativeName().equals("n/a") 
        ? "none" : element.alternativeName();
    return new ElementDto(element.name(), element.atomicNumber());
  }
}
