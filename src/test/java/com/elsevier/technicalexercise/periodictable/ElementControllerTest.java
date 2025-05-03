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

    // Then
    mockMvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error.code").value(404));

    // The test passes if it gets here without an exception
    // The actual exception handling is tested in other tests
  }

  @Test
  public void testInvalidRequestParameter() throws Exception {
    // When - Using an invalid parameter type (string instead of integer)
    mockMvc.perform(get("/elements/invalid")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error.code").value(400))
        .andExpect(jsonPath("$.error.reason").value("MethodArgumentTypeMismatchException"))
        .andExpect(jsonPath("$.error.message").exists());
  }

  @Test
  public void testNonExistentEndpoint() throws Exception {
    // When - Accessing a non-existent endpoint
    mockMvc.perform(get("/non-existent-endpoint")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error.code").value(404))
        .andExpect(jsonPath("$.error.reason").value("NoResourceFoundException"))
        .andExpect(jsonPath("$.error.message").exists());
  }
}
