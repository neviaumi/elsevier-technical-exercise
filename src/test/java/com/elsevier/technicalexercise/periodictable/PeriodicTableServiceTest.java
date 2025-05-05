package com.elsevier.technicalexercise.periodictable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PeriodicTableServiceTest {

  @Mock
  private PeriodicTableRepository periodicTableRepository;

  private PeriodicTableService periodicTableService;

  @BeforeEach
  void setUp() {
    periodicTableService = new PeriodicTableService(periodicTableRepository);
  }

  @Test
  void testUpdatePeriodicTable_SuccessfulUpdate() throws ExecutionException, InterruptedException {
    // Given
    List<Map<String, Object>> originalData = new ArrayList<>();
    Map<String, Object> hydrogen = new HashMap<>();
    hydrogen.put("atomic_number", 1);
    hydrogen.put("name", "Hydrogen");
    hydrogen.put("alternative_name", "n/a");
    hydrogen.put("group_block", "group 1, s-block");
    originalData.add(hydrogen);

    Map<String, Object> helium = new HashMap<>();
    helium.put("atomic_number", 2);
    helium.put("name", "Helium");
    helium.put("alternative_name", "n/a");
    helium.put("group_block", "group 18 (noble gases), s-block");
    originalData.add(helium);

    PeriodicTableEntity originalEntity = new PeriodicTableEntity(originalData, "mockETag");

    // Mock repository behavior
    when(periodicTableRepository.getPeriodicTable())
        .thenReturn(CompletableFuture.completedFuture(originalEntity));
    when(periodicTableRepository.updatePeriodicTable(any(PeriodicTableEntity.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    // Create patch elements
    List<ElementPatchRequestDto> patchElements = new ArrayList<>();
    patchElements.add(new ElementPatchRequestDto("Updated Hydrogen", 1, "H", "updated group 1, s-block"));

    // When
    CompletableFuture<PeriodicTableEntity> result =
        periodicTableService.updatePeriodicTable(patchElements);

    // Then
    assertNotNull(result, "Result should not be null");
    PeriodicTableEntity updatedEntity = result.get(); // Wait for completion

    // Verify repository methods were called
    verify(periodicTableRepository).getPeriodicTable();
    verify(periodicTableRepository).updatePeriodicTable(any(PeriodicTableEntity.class));

    // Capture the argument passed to updatePeriodicTable
    var argumentCaptor = org.mockito.ArgumentCaptor.forClass(PeriodicTableEntity.class);
    verify(periodicTableRepository).updatePeriodicTable(argumentCaptor.capture());


    // Verify the updated entity contains the expected changes
    assertEquals(2, updatedEntity.data().size(), "Should still have 2 elements");

    Map<String, Object> updatedHydrogen = updatedEntity.data().getFirst();
    assertEquals("Updated Hydrogen", updatedHydrogen.get("name"), "Name should be updated");
    assertEquals("H", updatedHydrogen.get("alternative_name"),
        "Alternative name should be updated");
    assertEquals("updated group 1, s-block", updatedHydrogen.get("group_block"),
        "Group block should be updated");

    Map<String, Object> unchangedHelium = updatedEntity.data().get(1);
    assertEquals("Helium", unchangedHelium.get("name"), "Helium should remain unchanged");
  }

  @Test
  void testUpdatePeriodicTable_GetPeriodicTableFails() {
    // Given
    RuntimeException expectedException = new RuntimeException("Failed to get periodic table");

    // Mock repository behavior to fail when getting the periodic table
    when(periodicTableRepository.getPeriodicTable())
        .thenReturn(CompletableFuture.failedFuture(expectedException));

    // Create patch elements
    List<ElementPatchRequestDto> patchElements = new ArrayList<>();
    patchElements.add(new ElementPatchRequestDto("Updated Hydrogen", 1, "H", "updated group 1, s-block"));

    // When
    CompletableFuture<PeriodicTableEntity> result = 
        periodicTableService.updatePeriodicTable(patchElements);

    // Then
    // Verify the future completes exceptionally with the expected exception
    ExecutionException executionException = assertThrows(
        ExecutionException.class, 
        () -> result.get(),
        "Should throw ExecutionException when getPeriodicTable fails"
    );

    assertEquals(expectedException, executionException.getCause(), 
        "Exception cause should match the expected exception");

    // Verify repository methods were called
    verify(periodicTableRepository).getPeriodicTable();
    verify(periodicTableRepository, never()).updatePeriodicTable(any(PeriodicTableEntity.class));
  }

  @Test
  void testUpdatePeriodicTable_UpdatePeriodicTableFails() {
    // Given
    List<Map<String, Object>> originalData = new ArrayList<>();
    Map<String, Object> hydrogen = new HashMap<>();
    hydrogen.put("atomic_number", 1);
    hydrogen.put("name", "Hydrogen");
    hydrogen.put("alternative_name", "n/a");
    hydrogen.put("group_block", "group 1, s-block");
    originalData.add(hydrogen);

    PeriodicTableEntity originalEntity = new PeriodicTableEntity(originalData, "mockETag");
    RuntimeException expectedException = new RuntimeException("Failed to update periodic table");

    // Mock repository behavior
    when(periodicTableRepository.getPeriodicTable())
        .thenReturn(CompletableFuture.completedFuture(originalEntity));
    when(periodicTableRepository.updatePeriodicTable(any(PeriodicTableEntity.class)))
        .thenReturn(CompletableFuture.failedFuture(expectedException));

    // Create patch elements
    List<ElementPatchRequestDto> patchElements = new ArrayList<>();
    patchElements.add(new ElementPatchRequestDto("Updated Hydrogen", 1, "H", "updated group 1, s-block"));

    // When
    CompletableFuture<PeriodicTableEntity> result = 
        periodicTableService.updatePeriodicTable(patchElements);

    // Then
    // Verify the future completes exceptionally with the expected exception
    ExecutionException executionException = assertThrows(
        ExecutionException.class, 
        () -> result.get(),
        "Should throw ExecutionException when updatePeriodicTable fails"
    );

    assertEquals(expectedException, executionException.getCause(), 
        "Exception cause should match the expected exception");

    // Verify repository methods were called
    verify(periodicTableRepository).getPeriodicTable();
    verify(periodicTableRepository).updatePeriodicTable(any(PeriodicTableEntity.class));
  }
}
