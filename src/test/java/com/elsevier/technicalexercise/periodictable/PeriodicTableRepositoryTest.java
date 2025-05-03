package com.elsevier.technicalexercise.periodictable;

import com.elsevier.technicalexercise.cloud.ObjectStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PeriodicTableRepositoryTest {

  private String testBucketName = "elsevier-technical-exercise";
  private String testObjectKeyPath = "periodic_table.json";

  @Mock
  private ObjectStorage objectStorage;

  private PeriodicTableRepository periodicTableRepository;

  @BeforeEach
  void setUp() {
    periodicTableRepository =
        new PeriodicTableRepository(objectStorage, testBucketName, testObjectKeyPath);
  }

  @Test
  void testFindElements() throws ExecutionException, InterruptedException {
    // Given
    String jsonContent = """
        [
            {
                "name": "Hydrogen",
                "atomic_number": 1,
                "alternative_name": "n/a",
                "group_block": "group 1, s-block"
            },
            {
                "name": "Helium",
                "atomic_number": 2,
                "alternative_name": "n/a",
                "group_block": "group 18 (noble gases), s-block"
            }
        ]
        """;

    ObjectStorage.GetObjectResponse mockResponse = new ObjectStorage.GetObjectResponse(
        jsonContent.getBytes(StandardCharsets.UTF_8),
        "mockETag"
    );

    when(objectStorage.getObject(eq(testBucketName),
        eq(testObjectKeyPath)))
        .thenReturn(CompletableFuture.completedFuture(mockResponse));

    // When
    CompletableFuture<List<ElementEntity>> futureElements = periodicTableRepository.findElements();

    // Then
    assertNotNull(futureElements,
        "Future elements should not be null");

    List<ElementEntity> elements = futureElements.get();
    assertNotNull(elements,
        "Elements list should not be null");
    assertTrue(elements.size() > 0,
        "Should have elements");

    // Check for Hydrogen (element 1)
    ElementEntity hydrogen = elements.stream()
        .filter(e -> e.atomicNumber() == 1)
        .findFirst()
        .orElseThrow(() -> new AssertionError("Hydrogen not found"));

    assertEquals("Hydrogen",
        hydrogen.name(),
        "First element should be Hydrogen");

    // Check for Helium (element 2)
    ElementEntity helium = elements.stream()
        .filter(e -> e.atomicNumber() == 2)
        .findFirst()
        .orElseThrow(() -> new AssertionError("Helium not found"));

    assertEquals("Helium",
        helium.name(),
        "Second element should be Helium");

    System.out.println("[DEBUG_LOG] Successfully tested with mock data");
  }

  @Test
  void testFindElementsWithEmptyResponse() {
    // Given
    String jsonContent = "[]";

    ObjectStorage.GetObjectResponse mockResponse = new ObjectStorage.GetObjectResponse(
        jsonContent.getBytes(StandardCharsets.UTF_8),
        "mockETag"
    );

    when(objectStorage.getObject(eq(testBucketName),
        eq(testObjectKeyPath)))
        .thenReturn(CompletableFuture.completedFuture(mockResponse));

    // When
    CompletableFuture<List<ElementEntity>> futureElements = periodicTableRepository.findElements();

    // Then
    assertNotNull(futureElements,
        "Future elements should not be null");

    try {
      List<ElementEntity> elements = futureElements.get();
      assertNotNull(elements,
          "Elements list should not be null");
      assertTrue(elements.isEmpty(),
          "Elements list should be empty");
    } catch (Exception e) {
      fail("Should not throw an exception for empty list: " + e.getMessage());
    }
  }

  @Test
  void testFindElementsWithInvalidJson() {
    // Given
    String invalidJson = "{ invalid json }";

    ObjectStorage.GetObjectResponse mockResponse = new ObjectStorage.GetObjectResponse(
        invalidJson.getBytes(StandardCharsets.UTF_8),
        "mockETag"
    );

    when(objectStorage.getObject(eq(testBucketName),
        eq(testObjectKeyPath)))
        .thenReturn(CompletableFuture.completedFuture(mockResponse));

    // When
    CompletableFuture<List<ElementEntity>> futureElements = periodicTableRepository.findElements();

    // Then
    assertNotNull(futureElements,
        "Future elements should not be null");

    Exception exception = assertThrows(ExecutionException.class,
        () -> {
          futureElements.get();
        });

    assertTrue(exception.getCause()
            .getMessage()
            .contains("Error on mapping the object"),
        "Should throw an exception related to JSON mapping");
  }

  @Test
  void testFindElementsWithObjectStorageFailure() {
    // Given
    CompletableFuture<ObjectStorage.GetObjectResponse> failedFuture = new CompletableFuture<>();
    failedFuture.completeExceptionally(new RuntimeException("Storage error"));

    when(objectStorage.getObject(anyString(),
        anyString()))
        .thenReturn(failedFuture);

    // When
    CompletableFuture<List<ElementEntity>> futureElements = periodicTableRepository.findElements();

    // Then
    assertNotNull(futureElements,
        "Future elements should not be null");

    Exception exception = assertThrows(ExecutionException.class,
        () -> {
          futureElements.get();
        });

    assertEquals("Storage error",
        exception.getCause()
            .getMessage(),
        "Should propagate the original error message");
  }

  @Test
  void testFindElementsByGroup() throws ExecutionException, InterruptedException {
    // Given
    String jsonContent = """
        [
            {
                "name": "Hydrogen",
                "atomic_number": 1,
                "alternative_name": "n/a",
                "group_block": "group 1, s-block"
            },
            {
                "name": "Helium",
                "atomic_number": 2,
                "alternative_name": "n/a",
                "group_block": "group 18 (noble gases), s-block"
            },
            {
                "name": "Lithium",
                "atomic_number": 3,
                "alternative_name": "n/a",
                "group_block": "group 1, s-block"
            }
        ]
        """;

    ObjectStorage.GetObjectResponse mockResponse = new ObjectStorage.GetObjectResponse(
        jsonContent.getBytes(StandardCharsets.UTF_8),
        "mockETag"
    );

    when(objectStorage.getObject(eq(testBucketName),
        eq(testObjectKeyPath)))
        .thenReturn(CompletableFuture.completedFuture(mockResponse));

    // When
    CompletableFuture<List<ElementEntity>> futureElements =
        periodicTableRepository.findElements("1");

    // Then
    assertNotNull(futureElements,
        "Future elements should not be null");

    List<ElementEntity> elements = futureElements.get();
    assertNotNull(elements,
        "Elements list should not be null");
    assertEquals(2, elements.size(),
        "Should have 2 elements in group 1");

    // Verify that only elements from group 1 are returned
    for (ElementEntity element : elements) {
      assertTrue(element.groupBlock().contains("group 1"),
          "Element should be in group 1");
    }

    // Verify specific elements
    assertTrue(elements.stream().anyMatch(e -> e.name().equals("Hydrogen")),
        "Hydrogen should be in the result");
    assertTrue(elements.stream().anyMatch(e -> e.name().equals("Lithium")),
        "Lithium should be in the result");
    assertFalse(elements.stream().anyMatch(e -> e.name().equals("Helium")),
        "Helium should not be in the result");
  }

  @Test
  void testFindElementsByBlockType() throws ExecutionException, InterruptedException {
    // Given
    String jsonContent = """
        [
            {
                "name": "Hydrogen",
                "atomic_number": 1,
                "alternative_name": "n/a",
                "group_block": "group 1, s-block"
            },
            {
                "name": "Helium",
                "atomic_number": 2,
                "alternative_name": "n/a",
                "group_block": "group 18 (noble gases), s-block"
            },
            {
                "name": "Boron",
                "atomic_number": 5,
                "alternative_name": "n/a",
                "group_block": "group 13, p-block"
            }
        ]
        """;

    ObjectStorage.GetObjectResponse mockResponse = new ObjectStorage.GetObjectResponse(
        jsonContent.getBytes(StandardCharsets.UTF_8),
        "mockETag"
    );

    when(objectStorage.getObject(eq(testBucketName),
        eq(testObjectKeyPath)))
        .thenReturn(CompletableFuture.completedFuture(mockResponse));

    // When
    CompletableFuture<List<ElementEntity>> futureElements =
        periodicTableRepository.findElements("s-block");

    // Then
    assertNotNull(futureElements,
        "Future elements should not be null");

    List<ElementEntity> elements = futureElements.get();
    assertNotNull(elements,
        "Elements list should not be null");
    assertEquals(2, elements.size(),
        "Should have 2 elements in s-block");

    // Verify that only elements from s-block are returned
    for (ElementEntity element : elements) {
      assertTrue(element.groupBlock().contains("s-block"),
          "Element should be in s-block");
    }

    // Verify specific elements
    assertTrue(elements.stream().anyMatch(e -> e.name().equals("Hydrogen")),
        "Hydrogen should be in the result");
    assertTrue(elements.stream().anyMatch(e -> e.name().equals("Helium")),
        "Helium should be in the result");
    assertFalse(elements.stream().anyMatch(e -> e.name().equals("Boron")),
        "Boron should not be in the result");
  }

  @Test
  void testFindElementsByGroupWithInvalidGroupBlock() {
    // Given
    String jsonContent = """
        [
            {
                "name": "InvalidElement",
                "atomic_number": 999,
                "alternative_name": "n/a",
                "group_block": "invalid format"
            }
        ]
        """;

    ObjectStorage.GetObjectResponse mockResponse = new ObjectStorage.GetObjectResponse(
        jsonContent.getBytes(StandardCharsets.UTF_8),
        "mockETag"
    );

    when(objectStorage.getObject(eq(testBucketName),
        eq(testObjectKeyPath)))
        .thenReturn(CompletableFuture.completedFuture(mockResponse));

    // When
    CompletableFuture<List<ElementEntity>> futureElements =
        periodicTableRepository.findElements("1");

    // Then
    assertNotNull(futureElements,
        "Future elements should not be null");

    Exception exception = assertThrows(ExecutionException.class,
        () -> {
          futureElements.get();
        });

    assertTrue(exception.getCause() instanceof RuntimeException,
        "Should throw a RuntimeException");
    assertTrue(exception.getCause().getMessage().contains("Invalid group block"),
        "Should contain error message about invalid group block");
  }

  @Test
  void testGetElementByAtomicNumber() throws ExecutionException, InterruptedException {
    // Given
    String jsonContent = """
        [
            {
                "name": "Hydrogen",
                "atomic_number": 1,
                "alternative_name": "n/a",
                "group_block": "group 1, s-block"
            },
            {
                "name": "Helium",
                "atomic_number": 2,
                "alternative_name": "n/a",
                "group_block": "group 18 (noble gases), s-block"
            }
        ]
        """;

    ObjectStorage.GetObjectResponse mockResponse = new ObjectStorage.GetObjectResponse(
        jsonContent.getBytes(StandardCharsets.UTF_8),
        "mockETag"
    );

    when(objectStorage.getObject(eq(testBucketName),
        eq(testObjectKeyPath)))
        .thenReturn(CompletableFuture.completedFuture(mockResponse));

    // When
    CompletableFuture<ElementEntity> futureElement = periodicTableRepository.getElement(1);

    // Then
    assertNotNull(futureElement,
        "Future element should not be null");

    ElementEntity element = futureElement.get();
    assertNotNull(element,
        "Element should not be null");
    assertEquals(1, element.atomicNumber(),
        "Atomic number should be 1");
    assertEquals("Hydrogen", element.name(),
        "Element name should be Hydrogen");
  }

  @Test
  void testGetElementByAtomicNumberNotFound() {
    // Given
    String jsonContent = """
        [
            {
                "name": "Hydrogen",
                "atomic_number": 1,
                "alternative_name": "n/a",
                "group_block": "group 1, s-block"
            },
            {
                "name": "Helium",
                "atomic_number": 2,
                "alternative_name": "n/a",
                "group_block": "group 18 (noble gases), s-block"
            }
        ]
        """;

    ObjectStorage.GetObjectResponse mockResponse = new ObjectStorage.GetObjectResponse(
        jsonContent.getBytes(StandardCharsets.UTF_8),
        "mockETag"
    );

    when(objectStorage.getObject(eq(testBucketName),
        eq(testObjectKeyPath)))
        .thenReturn(CompletableFuture.completedFuture(mockResponse));

    // When
    CompletableFuture<ElementEntity> futureElement = periodicTableRepository.getElement(999);

    // Then
    assertNotNull(futureElement,
        "Future element should not be null");

    Exception exception = assertThrows(ExecutionException.class,
        () -> {
          futureElement.get();
        });

    assertTrue(exception.getCause().getMessage().contains("Element not found"),
        "Should throw ElementNotFoundException with appropriate message");
  }

  @Test
  void testGetPeriodicTable() throws ExecutionException, InterruptedException {
    // Given
    String jsonContent = """
        [
            {
                "name": "Hydrogen",
                "atomic_number": 1,
                "alternative_name": "n/a",
                "group_block": "group 1, s-block"
            },
            {
                "name": "Helium",
                "atomic_number": 2,
                "alternative_name": "n/a",
                "group_block": "group 18 (noble gases), s-block"
            }
        ]
        """;

    ObjectStorage.GetObjectResponse mockResponse = new ObjectStorage.GetObjectResponse(
        jsonContent.getBytes(StandardCharsets.UTF_8),
        "mockETag"
    );

    when(objectStorage.getObject(eq(testBucketName),
        eq(testObjectKeyPath)))
        .thenReturn(CompletableFuture.completedFuture(mockResponse));

    // When
    CompletableFuture<PeriodicTableEntity> futurePeriodicTable =
        periodicTableRepository.getPeriodicTable();

    // Then
    assertNotNull(futurePeriodicTable,
        "Future periodic table should not be null");

    PeriodicTableEntity periodicTable = futurePeriodicTable.get();
    assertNotNull(periodicTable,
        "Periodic table should not be null");
    assertNotNull(periodicTable.data(),
        "Periodic table data should not be null");
    assertEquals("mockETag", periodicTable.etag(),
        "Etag should match the mock response");
    assertEquals(2, periodicTable.data().size(),
        "Periodic table should contain 2 elements");

    // Verify the data contains the expected elements
    assertTrue(periodicTable.data().stream()
            .anyMatch(element -> "Hydrogen".equals(element.get("name"))),
        "Periodic table should contain Hydrogen");
    assertTrue(periodicTable.data().stream()
            .anyMatch(element -> "Helium".equals(element.get("name"))),
        "Periodic table should contain Helium");
  }

  @Test
  void testGetPeriodicTableWithEmptyResponse() throws ExecutionException, InterruptedException {
    // Given
    String jsonContent = "[]";

    ObjectStorage.GetObjectResponse mockResponse = new ObjectStorage.GetObjectResponse(
        jsonContent.getBytes(StandardCharsets.UTF_8),
        "mockETag"
    );

    when(objectStorage.getObject(eq("elsevier-technical-exercise"),
        eq("periodic_table.json")))
        .thenReturn(CompletableFuture.completedFuture(mockResponse));

    // When
    CompletableFuture<PeriodicTableEntity> futurePeriodicTable =
        periodicTableRepository.getPeriodicTable();

    // Then
    assertNotNull(futurePeriodicTable,
        "Future periodic table should not be null");

    PeriodicTableEntity periodicTable = futurePeriodicTable.get();
    assertNotNull(periodicTable,
        "Periodic table should not be null");
    assertNotNull(periodicTable.data(),
        "Periodic table data should not be null");
    assertEquals("mockETag", periodicTable.etag(),
        "Etag should match the mock response");
    assertTrue(periodicTable.data().isEmpty(),
        "Periodic table data should be empty");
  }

  @Test
  void testGetPeriodicTableWithInvalidJson() {
    // Given
    String invalidJson = "{ invalid json }";

    ObjectStorage.GetObjectResponse mockResponse = new ObjectStorage.GetObjectResponse(
        invalidJson.getBytes(StandardCharsets.UTF_8),
        "mockETag"
    );

    when(objectStorage.getObject(eq(testBucketName),
        eq(testObjectKeyPath)))
        .thenReturn(CompletableFuture.completedFuture(mockResponse));

    // When
    CompletableFuture<PeriodicTableEntity> futurePeriodicTable =
        periodicTableRepository.getPeriodicTable();

    // Then
    assertNotNull(futurePeriodicTable,
        "Future periodic table should not be null");

    Exception exception = assertThrows(ExecutionException.class,
        () -> {
          futurePeriodicTable.get();
        });

    assertTrue(exception.getCause()
            .getMessage()
            .contains("Error on mapping the object"),
        "Should throw an exception related to JSON mapping");
  }

  @Test
  void testGetPeriodicTableWithObjectStorageFailure() {
    // Given
    CompletableFuture<ObjectStorage.GetObjectResponse> failedFuture = new CompletableFuture<>();
    failedFuture.completeExceptionally(new RuntimeException("Storage error"));

    when(objectStorage.getObject(anyString(),
        anyString()))
        .thenReturn(failedFuture);

    // When
    CompletableFuture<PeriodicTableEntity> futurePeriodicTable =
        periodicTableRepository.getPeriodicTable();

    // Then
    assertNotNull(futurePeriodicTable,
        "Future periodic table should not be null");

    Exception exception = assertThrows(ExecutionException.class,
        () -> {
          futurePeriodicTable.get();
        });

    assertEquals("Storage error",
        exception.getCause()
            .getMessage(),
        "Should propagate the original error message");
  }
}
