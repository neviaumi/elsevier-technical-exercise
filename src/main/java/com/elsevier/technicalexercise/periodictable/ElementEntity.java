package com.elsevier.technicalexercise.periodictable;

/**
 * Entity representing a chemical element in the periodic table.
 */
public record ElementEntity(
    String name, 
    int atomicNumber, 
    String alternativeName, 
    String groupBlock) {
}
