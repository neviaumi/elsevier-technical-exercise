package com.elsevier.technicalexercise.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data transfer object for successful API responses.
 */
public record SuccessResponseDTO(Map<String, Object> data) {

  /**
   * Creates a success response from a single item.
   *
   * @param item the item to include in the response
   * @param <T> the type of the item
   * @return a new SuccessResponseDTO containing the item
   */
  public static <T> SuccessResponseDTO fromSingleItem(T item) {
    Map<String, Object> wrapper = new HashMap<>();
    wrapper.put("data", item);
    return new SuccessResponseDTO(wrapper);
  }

  /**
   * Creates a success response from a list of items.
   *
   * @param items the list of items to include in the response
   * @param <T> the type of the items in the list
   * @return a new SuccessResponseDTO containing the list of items
   */
  public static <T> SuccessResponseDTO fromListOfItems(List<T> items) {
    Map<String, Object> wrapper = new HashMap<>();
    wrapper.put("items", items);
    return new SuccessResponseDTO(wrapper);
  }
}
