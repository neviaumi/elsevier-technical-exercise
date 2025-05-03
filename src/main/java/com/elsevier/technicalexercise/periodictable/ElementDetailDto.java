package com.elsevier.technicalexercise.periodictable;

/**
 * Data transfer object for detailed element information.
 */
public record ElementDetailDto(String name, int atomicNumber, String alternativeName) {
  static ElementDetailDto fromElement(ElementEntity element) {
    String alternativeName = element.alternativeName().equals("n/a")
        ? "none" : element.alternativeName();
    return new ElementDetailDto(element.name(), element.atomicNumber(), alternativeName);
  }
}
