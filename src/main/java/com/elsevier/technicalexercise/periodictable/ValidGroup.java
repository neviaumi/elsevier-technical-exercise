package com.elsevier.technicalexercise.periodictable;


import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validation annotation for periodic table group numbers.
 * Validates that a string represents a valid group number (1-18 or "n/a").
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidGroup.GroupValidator.class)
public @interface ValidGroup {
  /**
   * Error message to be used when the validation fails.
   *
   * @return the error message
   */
  String message() default "Invalid group number";

  /**
   * Groups for this constraint.
   *
   * @return the groups
   */
  Class<?>[] groups() default {};

  /**
   * Payloads for this constraint.
   *
   * @return the payloads
   */
  Class<? extends Payload>[] payload() default {};

  /**
   * Validator implementation for the ValidGroup annotation.
   * Validates that a string represents a valid group number (1-18 or "n/a").
   */
  static class GroupValidator implements ConstraintValidator<ValidGroup, String> {
    /**
     * Validates if the given string is a valid group number.
     *
     * @param value the group number to validate
     * @return true if the value is a valid group number, false otherwise
     */
    static boolean validateGroup(String value) {
      if ("n/a".equalsIgnoreCase(value)) {
        return true;
      }
      try {
        int groupNumber = Integer.parseInt(value);
        if (groupNumber < 1 || groupNumber > 18) {
          return false;
        }
      } catch (NumberFormatException ex) {
        return false;
      }
      return true;
    }

    /**
     * Validates if the given string is a valid group number.
     *
     * @param value the group number to validate
     * @param context the constraint validator context
     * @return true if the value is a valid group number or null, false otherwise
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
      if (value == null) {
        return true;
      }
      return GroupValidator.validateGroup(value);
    }
  }

}
