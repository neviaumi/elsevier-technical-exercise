package com.elsevier.technicalexercise.periodictable;

/**
 * Data transfer object for basic element information.
 */
public record ElementDTO(String name, int atomicNumber) {
  static ElementDTO fromElement(ElementEntity element) {
    String alternativeName = element.alternativeName().equals("n/a") 
        ? "none" : element.alternativeName();
    return new ElementDTO(element.name(), element.atomicNumber());
  }
}
