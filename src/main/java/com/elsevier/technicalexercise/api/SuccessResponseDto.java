package com.elsevier.technicalexercise.api;

import java.util.List;

/**
 * Data transfer object for successful API responses.
 */
public record SuccessResponseDto<T>(T data) {

  /**
   * Wrapper for a list of items in a success response.
   *
   * @param <T> the type of items in the list
   * @param items the list of items
   */
  public record Items<T>(List<T> items) {
  }

  /**
   * Creates a success response from a single item.
   *
   * @param item the item to include in the response
   * @param <T>  the type of the item
   * @return a new SuccessResponseDTO containing the item
   */
  public static <T> SuccessResponseDto<T> fromSingleItem(T item) {
    return new SuccessResponseDto<>(item);
  }

  /**
   * Creates a success response from a list of items.
   *
   * @param items the list of items to include in the response
   * @param <T>   the type of the items in the list
   * @return a new SuccessResponseDTO containing the list of items
   */
  public static <T> SuccessResponseDto<SuccessResponseDto.Items<T>> fromListOfItems(List<T> items) {
    return new SuccessResponseDto<>(new SuccessResponseDto.Items<>(items));
  }
}
