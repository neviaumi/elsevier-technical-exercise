package com.elsevier.technicalexercise.periodictable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import com.elsevier.technicalexercise.cloud.ObjectStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureMockMvc
@SpringBootTest
public class ElementControllerMutationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectStorage objectStorage;

  @Value("${periodic-table.bucket}")
  private String bucket;

  static String testObjectKey;
  private ObjectMapper mapper;

  @DynamicPropertySource
  static void dynamicProperties(DynamicPropertyRegistry registry) {
    // Generate a unique key with UUID, e.g., "my-test-key-<UUID>.json"
    testObjectKey = "tests/periodic-table-" + UUID.randomUUID() + ".json";
    registry.add("periodic-table.key", () -> testObjectKey);
  }

  @BeforeEach
  public void setUp() {
    objectStorage.copyObject(bucket, "tests/periodic_table.json", testObjectKey).join();
    mapper = new ObjectMapper();
  }

  @AfterEach
  public void tearDown() {
    objectStorage.deleteObject(bucket, testObjectKey).join();
  }

  @Test
  public void testPatchMultipleElements() throws Exception {
    // When
    MvcResult mvcPatchResult =
        mockMvc.perform(patch("/elements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(List.of(
                    new PatchElementDto("Updated Hydrogen", 1, "H", "updated group 1, s-block"),
                    new PatchElementDto("Updated Helium", 2, "He",
                        "updated group 18 (noble gases), s-block")
                ))))
            .andExpect(request().asyncStarted())
            .andReturn();

    // Then
    mockMvc.perform(asyncDispatch(mvcPatchResult))
        .andExpect(status().isNoContent())
        .andExpect(header().exists("ETag"));

    // Verify first element was updated
    MvcResult mvcGetResult1 = mockMvc.perform(get("/elements/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(request().asyncStarted())
        .andReturn();

    mockMvc.perform(asyncDispatch(mvcGetResult1))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.name").value("Updated Hydrogen"))
        .andExpect(jsonPath("$.data.alternativeName").value("H"));

    // Verify second element was updated
    MvcResult mvcGetResult2 = mockMvc.perform(get("/elements/2").accept(MediaType.APPLICATION_JSON))
        .andExpect(request().asyncStarted())
        .andReturn();

    mockMvc.perform(asyncDispatch(mvcGetResult2))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.name").value("Updated Helium"))
        .andExpect(jsonPath("$.data.alternativeName").value("He"));
  }

  @Test
  public void testPatchSingleField() throws Exception {
    // When - update only the name field
    MvcResult mvcPatchResult =
        mockMvc.perform(patch("/elements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(List.of(
                    new PatchElementDto("Only Name Updated", 3, null, null)
                ))))
            .andExpect(request().asyncStarted())
            .andReturn();

    // Then
    mockMvc.perform(asyncDispatch(mvcPatchResult))
        .andExpect(status().isNoContent())
        .andExpect(header().exists("ETag"));

    // Verify only name was updated, other fields remain unchanged
    MvcResult mvcGetResult = mockMvc.perform(get("/elements/3").accept(MediaType.APPLICATION_JSON))
        .andExpect(request().asyncStarted())
        .andReturn();

    mockMvc.perform(asyncDispatch(mvcGetResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.name").value("Only Name Updated"));
  }

  @Test
  public void testPatchAlternativeNameOnly() throws Exception {
    // When - update only the alternative_name field
    MvcResult mvcPatchResult =
        mockMvc.perform(patch("/elements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(List.of(
                    new PatchElementDto(null, 4, "Updated Be", null)
                ))))
            .andExpect(request().asyncStarted())
            .andReturn();

    // Then
    mockMvc.perform(asyncDispatch(mvcPatchResult))
        .andExpect(status().isNoContent())
        .andExpect(header().exists("ETag"));

    // Verify only alternative_name was updated
    MvcResult mvcGetResult = mockMvc.perform(get("/elements/4").accept(MediaType.APPLICATION_JSON))
        .andExpect(request().asyncStarted())
        .andReturn();

    mockMvc.perform(asyncDispatch(mvcGetResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.name").value("Beryllium"))
        .andExpect(jsonPath("$.data.alternativeName").value("Updated Be"));
  }

  @Test
  public void testPatchGroupBlockOnly() throws Exception {
    // When - update only the group_block field
    MvcResult mvcPatchResult =
        mockMvc.perform(patch("/elements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(List.of(
                    new PatchElementDto(null, 5, null, "Updated group block")
                ))))
            .andExpect(request().asyncStarted())
            .andReturn();

    // Then
    mockMvc.perform(asyncDispatch(mvcPatchResult))
        .andExpect(status().isNoContent())
        .andExpect(header().exists("ETag"));

    // Verify only group_block was updated
    MvcResult mvcGetResult = mockMvc.perform(get("/elements/5").accept(MediaType.APPLICATION_JSON))
        .andExpect(request().asyncStarted())
        .andReturn();

    mockMvc.perform(asyncDispatch(mvcGetResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.name").value("Boron"));
  }

  @Test
  public void testPatchNonExistentElement() throws Exception {
    // When - try to update an element with atomic number that doesn't exist
    MvcResult mvcPatchResult =
        mockMvc.perform(patch("/elements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(List.of(
                    new PatchElementDto("Non-existent Element", 999, "XX", "non-existent group")
                ))))
            .andExpect(request().asyncStarted())
            .andReturn();

    // Then - should still return 204 No Content as the operation is idempotent
    mockMvc.perform(asyncDispatch(mvcPatchResult))
        .andExpect(status().isNoContent())
        .andExpect(header().exists("ETag"));
  }

  @Test
  public void testPatchEmptyList() throws Exception {
    // When - send an empty list of elements to update
    MvcResult mvcPatchResult =
        mockMvc.perform(patch("/elements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(Collections.emptyList())))
            .andExpect(request().asyncStarted())
            .andReturn();

    // Then - should still return 204 No Content
    mockMvc.perform(asyncDispatch(mvcPatchResult))
        .andExpect(status().isNoContent())
        .andExpect(header().exists("ETag"));
  }


}
