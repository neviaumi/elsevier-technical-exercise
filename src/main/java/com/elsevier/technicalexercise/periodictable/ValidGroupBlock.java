package com.elsevier.technicalexercise.periodictable;


import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validation annotation for periodic table group block strings.
 * Validates that a string represents a valid group block in the format "group X, Y-block"
 * where X is a valid group number (1-18 or "n/a") and Y is one of "s", "p", "d", "f", or "g".
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidGroupBlock.GroupBlockValidator.class)
public @interface ValidGroupBlock {
  /**
   * Error message to be used when the validation fails.
   *
   * @return the error message
   */
  String message() default "Invalid group block";

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
   * Validator implementation for the ValidGroupBlock annotation.
   * Validates that a string represents a valid group block in the format "group X, Y-block".
   */
  static class GroupBlockValidator implements ConstraintValidator<ValidGroupBlock, String> {
    /**
     * Validates if the given string is a valid group block.
     *
     * @param value the group block to validate
     * @param context the constraint validator context
     * @return true if the value is a valid group block or null, false otherwise
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
      if (value == null) {
        return true;
      }
      String[] parts = value.split(",");
      if (parts.length != 2) {
        return false;
      }

      String groupPart = parts[0].trim();
      String blockPart = parts[1].trim();
      Pattern pattern =
          Pattern.compile("^group (n/a|[0-9]{1,2})( \\([a-zA-Z ]+\\))?$", Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(groupPart);

      if (!matcher.matches()) {
        return false;
      }
      String groupNum = matcher.group(1);
      if (!ValidGroup.GroupValidator.validateGroup(groupNum)) {
        return false;
      }

      if (!blockPart.toLowerCase().matches("^[spdfg]-block$")) {
        return false;
      }
      return true;
    }
  }

}
