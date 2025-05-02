package com.elsevier.technicalexercise.periodictable;

/**
 * Data transfer object for detailed element information.
 */
public record ElementDetailDTO(String name, int atomicNumber, String alternativeName) {
  static ElementDetailDTO fromElement(ElementEntity element) {
    String alternativeName = element.alternativeName().equals("n/a") 
        ? "none" : element.alternativeName();
    return new ElementDetailDTO(element.name(), element.atomicNumber(), alternativeName);
  }
}
