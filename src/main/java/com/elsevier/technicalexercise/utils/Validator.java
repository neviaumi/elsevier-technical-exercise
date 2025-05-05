package com.elsevier.technicalexercise.utils;

/**
 * Utility class for validation operations.
 */
public class Validator {
  /**
   * Checks if a string is not null and not blank (contains non-whitespace characters).
   *
   * @param value The string to check
   * @return true if the string is not null and contains at least one non-whitespace character
   */
  public static boolean isNotNullOrBlank(String value) {
    return value != null && !value.trim().isEmpty();
  }

  /**
   * Checks if a string is null or blank (contains only whitespace characters or is empty).
   *
   * @param value The string to check
   * @return true if the string is null or contains only whitespace characters
   */
  public static boolean isNullOrBlank(String value) {
    return !Validator.isNotNullOrBlank(value);
  }

}
