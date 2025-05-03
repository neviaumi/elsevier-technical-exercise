package com.elsevier.technicalexercise.demo;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for demo endpoints.
 */
@RestController
public class DemoController {

  /**
   * Returns a demo response with sample data.
   *
   * @return Map containing demo data
   */
  @GetMapping("/demo")
  public Map<String, Object> getDemo() {
    Map<String, Object> response = new HashMap<>();
    Map<String, Object> data = new HashMap<>();

    data.put("createdAt", "2025-05-01");
    response.put("data", data);

    return response;
  }

  /**
   * Returns a demo response with the provided path variable.
   *
   * @param number The number provided in the path
   * @return Map containing the number in the response data
   */
  @GetMapping("/demo/{number}")
  public Map<String, Object> getFromPathVariable(@PathVariable int number) {
    Map<String, Object> response = new HashMap<>();
    Map<String, Object> data = new HashMap<>();

    data.put("number", number);
    response.put("data", data);

    return response;
  }

  /**
   * Returns a demo response with data from the request body.
   *
   * @param body The request body containing demo data
   * @return Map containing the body data in the response
   */
  @PostMapping("/demo/body")
  public Map<String, Object> getFromBody(@Valid @RequestBody DemoDto body) {
    Map<String, Object> response = new HashMap<>();
    Map<String, Object> data = new HashMap<>();

    data.put("number", body);
    response.put("data", data);

    return response;
  }


  /**
   * Throws a runtime exception for demonstration purposes.
   *
   * @throws RuntimeException Always throws a demo error
   */
  @GetMapping("/demo/throw-error")
  public void throwError() {
    throw new RuntimeException("Demo error");
  }
}
