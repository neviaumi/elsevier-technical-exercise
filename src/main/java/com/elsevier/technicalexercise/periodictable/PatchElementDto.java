package com.elsevier.technicalexercise.periodictable;

/**
 * Data Transfer Object for PATCH operations on periodic table elements.
 * Contains the fields that can be updated for an element.
 */
public record PatchElementDto(
    String name,
    int atomicNumber,
    String alternativeName,
    String groupBlock) {
}
