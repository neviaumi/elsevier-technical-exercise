package com.elsevier.technicalexercise.periodictable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Data Transfer Object for PATCH operations on periodic table elements.
 * Contains the fields that can be updated for an element.
 */
public class PatchElementDto {

  private String name;

  @NotNull(message = "Atomic number must not be null")
  @Positive(message = "Atomic number must be a positive integer")
  private Integer atomicNumber;

  private String alternativeName;

  private String groupBlock;

  /**
   * No-argument constructor for frameworks.
   */
  public PatchElementDto() {
  }

  /**
   * All-arguments constructor for convenience (e.g., tests).
   *
   * @param name The name of the element
   * @param atomicNumber The atomic number of the element
   * @param alternativeName The alternative name of the element
   * @param groupBlock The group block of the element
   */
  public PatchElementDto(String name, Integer atomicNumber, String alternativeName,
                         String groupBlock) {
    this.name = name;
    this.atomicNumber = atomicNumber;
    this.alternativeName = alternativeName;
    this.groupBlock = groupBlock;
  }

  // Getters and setters

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getAtomicNumber() {
    return atomicNumber;
  }

  public void setAtomicNumber(Integer atomicNumber) {
    this.atomicNumber = atomicNumber;
  }

  public String getAlternativeName() {
    return alternativeName;
  }

  public void setAlternativeName(String alternativeName) {
    this.alternativeName = alternativeName;
  }

  public String getGroupBlock() {
    return groupBlock;
  }

  public void setGroupBlock(String groupBlock) {
    this.groupBlock = groupBlock;
  }
}
