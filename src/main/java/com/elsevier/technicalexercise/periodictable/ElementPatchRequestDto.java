package com.elsevier.technicalexercise.periodictable;

import com.elsevier.technicalexercise.utils.Validator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Data Transfer Object for PATCH operations on periodic table elements.
 * Contains the fields that can be updated for an element.
 */
public class ElementPatchRequestDto {

  private String name;

  @NotNull(message = "Atomic number must not be null")
  @Positive(message = "Atomic number must be a positive integer")
  private Integer atomicNumber;

  private String alternativeName;

  @ValidGroupBlock
  private String groupBlock;

  /**
   * No-argument constructor for frameworks.
   */
  public ElementPatchRequestDto() {
  }

  /**
   * All-arguments constructor for convenience (e.g., tests).
   *
   * @param name            The name of the element
   * @param atomicNumber    The atomic number of the element
   * @param alternativeName The alternative name of the element
   * @param groupBlock      The group block of the element
   */
  public ElementPatchRequestDto(String name, Integer atomicNumber, String alternativeName,
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

  @Schema(hidden = true)
  public boolean isEmpty() {
    return Validator.isNotNullOrBlank(this.name)
        && Validator.isNotNullOrBlank(this.alternativeName)
        && Validator.isNotNullOrBlank(this.groupBlock);
  }
}
