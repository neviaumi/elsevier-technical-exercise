package com.elsevier.technicalexercise.api;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Global exception handler for the application.
 * Provides centralized exception handling across all controllers.
 */
@ControllerAdvice
public class GlobalExceptionControllerHandler {

  /**
   * Handles Spring's ErrorResponseException.
   *
   * @param ex The ErrorResponseException to handle
   * @return ResponseEntity containing the error response
   */
  @ExceptionHandler(ErrorResponseException.class)
  @ResponseBody
  public ResponseEntity<ErrorResponseDto> handleErrorResponseException(ErrorResponseException ex) {
    return new ResponseEntity<>(ErrorResponseDto.fromException(
        HttpStatus.valueOf(ex.getStatusCode().value()),
        ex.getMessage(),
        ex
    ), ex.getStatusCode());
  }

  /**
   * Handles validation exceptions for handler methods.
   *
   * @param ex The HandlerMethodValidationException to handle
   * @return ErrorResponseDto containing validation error details
   */
  @ExceptionHandler(HandlerMethodValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorResponseDto handleHandlerMethodValidationException(HandlerMethodValidationException
                                                                     ex) {
    return ErrorResponseDto.fromErrors(HttpStatus.BAD_REQUEST,
        HandlerMethodValidationException.class.getSimpleName(),
        "Validation failed for the input fields. Please check the data format and try again.",
        ex.getAllErrors().stream()
            .map(error -> {
              if (error instanceof FieldError fieldError) {
                return new ErrorResponseDto.Error(
                    fieldError.getClass().getSimpleName(),
                    fieldError.getDefaultMessage(),
                    fieldError.getField(),
                    "field"
                );
              }
              return new ErrorResponseDto.Error(
                  error.getClass().getSimpleName(),
                  error.getDefaultMessage(),
                  null,
                  null
              );
            })
            .toList());
  }


  /**
   * Handles validation exceptions for method arguments.
   *
   * @param ex The MethodArgumentNotValidException to handle
   * @return ErrorResponseDto containing validation error details
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorResponseDto handleValidationExceptions(MethodArgumentNotValidException ex) {
    return ErrorResponseDto.fromErrors(HttpStatus.BAD_REQUEST,
        MethodArgumentNotValidException.class.getSimpleName(),
        "Validation failed for the input fields. Please check the data format and try again.",
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new ErrorResponseDto.Error(
                    error.getClass().getSimpleName(),
                    error.getDefaultMessage(),
                    error.getField(),
                    "field"
                )
            )
            .toList());
  }


  /**
   * Handles exceptions for malformed HTTP message bodies.
   *
   * @param ex      The HttpMessageNotReadableException to handle
   * @param request The current web request
   * @return ErrorResponseDto with information about the malformed request
   */
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseBody
  public ErrorResponseDto handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex, WebRequest request) {
    String message =
        "Malformed request. Please check the data format (e.g. JSON structure) and try again.";

    return ErrorResponseDto.fromErrors(
        HttpStatus.BAD_REQUEST,
        message,
        List.of(new ErrorResponseDto.Error(ex.getClass().getSimpleName(), message, null, null))
    );
  }

  /**
   * Handles exceptions for method argument type mismatches.
   *
   * @param ex      The MethodArgumentTypeMismatchException to handle
   * @param request The current web request
   * @return ErrorResponseDto with information about the type mismatch
   */
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseBody
  public ErrorResponseDto handleArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex, WebRequest request) {
    String message = String.format(
        "Invalid value '%s' for parameter '%s'. Expected type: %s.",
        ex.getValue(),
        ex.getName(),
        ex.getRequiredType() != null
            ? ex.getRequiredType().getSimpleName().toLowerCase()
            : "unknown"
    );

    return ErrorResponseDto.fromErrors(
        HttpStatus.BAD_REQUEST,
        message,
        List.of(new ErrorResponseDto.Error(ex.getClass().getSimpleName(), message, null, null))
    );
  }

  /**
   * Handle all other exceptions. This is a catch-all handler.
   *
   * @param ex      the exception
   * @param request the current request
   * @return the ErrorResponseDto
   */
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ErrorResponseDto handleAllExceptions(Exception ex, WebRequest request) {
    // In a production environment, you might want to log the exception here
    // but not include the details in the response for security reasons

    return ErrorResponseDto.fromException(
        HttpStatus.INTERNAL_SERVER_ERROR,
        ex.getMessage(),
        ex
    );
  }

  /**
   * Handles exceptions for resources not found.
   *
   * @param ex      The NoResourceFoundException to handle
   * @param request The current web request
   * @return ErrorResponseDto with information about the not found resource
   */
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ExceptionHandler(NoResourceFoundException.class)
  @ResponseBody
  public ErrorResponseDto handleNoResourceFoundException(NoResourceFoundException ex,
                                                         WebRequest request) {
    return ErrorResponseDto.fromException(
        HttpStatus.NOT_FOUND,
        ex.getMessage(),
        ex
    );
  }
}
