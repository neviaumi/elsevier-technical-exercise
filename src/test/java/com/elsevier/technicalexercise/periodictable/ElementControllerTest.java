package com.elsevier.technicalexercise.periodictable;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

@AutoConfigureMockMvc
@SpringBootTest
public class ElementControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void testGetAllElements() throws Exception {

    // When
    MvcResult mvcResult = mockMvc.perform(get("/elements")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(request().asyncStarted())
        .andReturn();

    // Then
    mockMvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.items").isArray())
        .andExpect(jsonPath("$.data.items.length()").value(117));
  }

  @Test
  public void testFilterByGroupNumber() throws Exception {
    // When
    MvcResult mvcResult = mockMvc.perform(get("/elements")
            .param("group", "1")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(request().asyncStarted())
        .andReturn();

    // Then
    mockMvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.items").isArray())
        // Group 1 should have 7 elements (H, Li, Na, K, Rb, Cs, Fr)
        .andExpect(jsonPath("$.data.items.length()").value(7));
  }

  @Test
  public void testFilterByGroupBlock() throws Exception {
    // When
    MvcResult mvcResult = mockMvc.perform(get("/elements")
            .param("group", "s-block")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(request().asyncStarted())
        .andReturn();

    // Then
    mockMvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.items").isArray())
        // s-block should have fewer elements than the total
        .andExpect(jsonPath("$.data.items.length()").value(14));
  }

  @Test
  public void testFilterByNonExistentGroup() throws Exception {
    // When
    MvcResult mvcResult = mockMvc.perform(get("/elements")
            .param("group", "non-existent-group")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(request().asyncStarted())
        .andReturn();

    // Then
    mockMvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.items").isArray())
        // Non-existent group should return empty array
        .andExpect(jsonPath("$.data.items.length()").value(0));
  }

  @Test
  public void testGetElementByAtomicNumber() throws Exception {
    // When
    MvcResult mvcResult = mockMvc.perform(get("/elements/1")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(request().asyncStarted())
        .andReturn();

    // Then
    mockMvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.name").value("Hydrogen"))
        .andExpect(jsonPath("$.data.atomicNumber").value(1))
        .andExpect(jsonPath("$.data.alternativeName").exists());
  }

  @Test
  public void testGetElementByInvalidAtomicNumber() throws Exception {
    // When - Using an atomic number that doesn't exist (e.g., 999)
    MvcResult mvcResult = mockMvc.perform(get("/elements/999")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(request().asyncStarted())
        .andReturn();

    // Then - The test will fail with ElementNotFoundException
    // This is the expected behavior as the exception is thrown during async processing
    try {
      mockMvc.perform(asyncDispatch(mvcResult));
      // If we get here, the test should fail because we expect an exception
      throw new AssertionError("Expected ElementNotFoundException was not thrown");
    } catch (Exception e) {
      // Verify that the exception is related to ElementNotFoundException
      Throwable cause = e;
      boolean foundElementNotFoundException = false;

      // Look through the exception chain for ElementNotFoundException
      while (cause != null) {
        if (cause.toString().contains("ElementNotFoundException")) {
          foundElementNotFoundException = true;
          break;
        }
        cause = cause.getCause();
      }

      if (!foundElementNotFoundException) {
        throw new AssertionError(
            "Expected ElementNotFoundException in exception chain, but found: " + e);
      }
    }
  }
}
