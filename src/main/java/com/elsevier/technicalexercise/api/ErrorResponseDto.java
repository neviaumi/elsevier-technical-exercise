package com.elsevier.technicalexercise.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

/**
 * Standard error response DTO for API errors.
 */
public record ErrorResponseDto(int code, String message, String reason, List<Error> errors) {
  /**
   * Nested record representing a specific error with reason and message.
   *
   * @param reason  The error reason or type
   * @param message The detailed error message
   */
  public record Error(String reason, String message, String location, String locationType) {
  }

  /**
   * Creates an error response from an exception.
   *
   * @param status  The HTTP status code
   * @param message The error message
   * @param ex      The exception that caused the error
   * @return A new ErrorResponseDto with information from the exception
   */
  public static ErrorResponseDto fromException(HttpStatus status, String message, Exception ex) {
    return new ErrorResponseDto(
        status.value(),
        message,
        ex.getClass().getSimpleName(),
        List.of(new Error(ex.getClass().getSimpleName(), ex.getMessage(), null, null))
    );
  }

  /**
   * Creates an error response from a list of errors, using the first error's reason.
   *
   * @param status  The HTTP status code
   * @param message The error message
   * @param errors  The list of errors
   * @return A new ErrorResponseDto with information from the errors
   */
  public static ErrorResponseDto fromErrors(HttpStatus status, String message, List<Error> errors) {
    return fromErrors(status, errors.getFirst().reason(), message, errors);
  }

  /**
   * Creates an error response from a list of errors with a specified reason.
   *
   * @param status  The HTTP status code
   * @param reason  The error reason
   * @param message The error message
   * @param errors  The list of errors
   * @return A new ErrorResponseDto with the provided information
   */
  public static ErrorResponseDto fromErrors(HttpStatus status, String reason, String message,
                                            List<Error> errors) {
    return new ErrorResponseDto(status.value(),
        message,
        reason, errors);
  }
}
