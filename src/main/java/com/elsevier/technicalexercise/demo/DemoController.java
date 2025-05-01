package com.elsevier.technicalexercise.demo;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
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
}
