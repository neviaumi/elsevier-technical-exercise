package com.elsevier.technicalexercise.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

/**
 * Standard error response DTO for API errors.
 */
public record ErrorResponseDto(Error error) {
  /**
   * Represents the error details in the API response.
   *
   * @param code HTTP status code
   * @param message Error message
   * @param reason Error reason
   * @param errors List of detailed error information
   */
  public record Error(int code, String message, String reason, List<ErrorDetail> errors) {
  }

  /**
   * Represents detailed information about a specific error.
   *
   * @param reason The reason for the error
   * @param message The error message
   * @param location The location where the error occurred (can be null)
   * @param locationType The type of location (can be null)
   */
  public record ErrorDetail(String reason, String message, String location, String locationType) {
    static ErrorDetail fromException(Exception ex) {
      return fromException(ex, ex.getMessage());
    }

    static ErrorDetail fromException(String reason, String message) {
      return new ErrorDetail(reason, message, null, null);
    }

    static ErrorDetail fromException(Exception ex, String message) {
      return fromException(
          ex.getClass().getSimpleName(),
          message
      );
    }
  }


  /**
   * Creates an error response from an exception.
   *
   * @param status The HTTP status code to use in the response
   * @param ex The exception that caused the error
   * @return A new ErrorResponseDto containing the exception details
   */
  public static ErrorResponseDto fromException(HttpStatus status, Exception ex) {
    return new ErrorResponseDto(
        new Error(status.value(),
            ex.getMessage(),
            ex.getClass().getSimpleName(),
            List.of(new ErrorDetail(ex.getClass().getSimpleName(), ex.getMessage(), null, null))
        )
    );
  }

  /**
   * Creates an error response from a list of error details.
   *
   * @param status The HTTP status code to use in the response
   * @param errors The list of error details
   * @return A new ErrorResponseDto containing the error details
   */
  public static ErrorResponseDto fromErrors(HttpStatus status, List<ErrorDetail> errors) {
    ErrorDetail firstError = errors.getFirst();
    return fromErrors(status, firstError.reason(), firstError.message(), errors);
  }


  /**
   * Creates an error response with a custom message from a list of error details.
   *
   * @param status The HTTP status code to use in the response
   * @param message The custom error message
   * @param errors The list of error details
   * @return A new ErrorResponseDto containing the error details with the custom message
   */
  public static ErrorResponseDto fromErrors(HttpStatus status, String message,
                                            List<ErrorDetail> errors) {
    return fromErrors(status, errors.getFirst().reason(), message, errors);
  }

  /**
   * Creates an error response with custom reason and message from a list of error details.
   *
   * @param status The HTTP status code to use in the response
   * @param reason The custom error reason
   * @param message The custom error message
   * @param errors The list of error details
   * @return A new ErrorResponseDto containing the error details with custom reason and message
   */
  public static ErrorResponseDto fromErrors(HttpStatus status, String reason, String message,
                                            List<ErrorDetail> errors) {
    return new ErrorResponseDto(new Error(status.value(),
        message,
        reason, errors));
  }
}
