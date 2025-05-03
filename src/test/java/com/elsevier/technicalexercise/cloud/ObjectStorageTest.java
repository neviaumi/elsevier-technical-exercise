package com.elsevier.technicalexercise.cloud;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ObjectStorageTest {

  private final ObjectStorage objectStorage = new ObjectStorage();

  private static final String BUCKET_NAME = "elsevier-technical-exercise";
  private static final String KEY_NAME = "periodic_table.json";

  @Test
  void testGetObject() throws ExecutionException, InterruptedException {
    // When
    CompletableFuture<ObjectStorage.GetObjectResponse> futureResponse =
        objectStorage.getObject(BUCKET_NAME,
            KEY_NAME);

    // Then
    assertNotNull(futureResponse,
        "Future response should not be null");

    ObjectStorage.GetObjectResponse response = futureResponse.get();
    assertNotNull(response,
        "Response should not be null");
    assertNotNull(response.content(),
        "Content should not be null");
    assertTrue(response.content().length > 0,
        "Content should not be empty");
    assertNotNull(response.etag(),
        "ETag should not be null");

    // Convert content to string and verify it contains expected data
    String content = new String(response.content(),
        StandardCharsets.UTF_8);
    assertTrue(content.contains("\"name\": \"Hydrogen\""),
        "Content should contain Hydrogen element");
    assertTrue(content.contains("\"symbol\": \"H\""),
        "Content should contain H symbol");
  }

  @Test
  void testGetObjectWithInvalidBucket() {
    // When
    CompletableFuture<ObjectStorage.GetObjectResponse> futureResponse =
        objectStorage.getObject("invalid-bucket",
            KEY_NAME);

    // Then
    assertNotNull(futureResponse,
        "Future response should not be null");

    // The future should complete exceptionally
    Exception exception = assertThrows(ExecutionException.class,
        () -> {
          futureResponse.get();
        });

    // Verify the exception is related to NoSuchBucket
    assertTrue(exception.getCause()
            .getMessage()
            .contains("NoSuchBucket")
            || exception.getCause()
            .getMessage()
            .contains("The specified bucket does not exist"),
        "Exception should be related to NoSuchBucket");
  }

  @Test
  void testGetObjectWithInvalidKey() {
    // When
    CompletableFuture<ObjectStorage.GetObjectResponse> futureResponse =
        objectStorage.getObject(BUCKET_NAME,
            "invalid-key.json");

    // Then
    assertNotNull(futureResponse,
        "Future response should not be null");

    // The future should complete exceptionally
    Exception exception = assertThrows(ExecutionException.class,
        () -> {
          futureResponse.get();
        });

    // Verify the exception is related to NoSuchKey
    assertTrue(exception.getCause()
            .getMessage()
            .contains("NoSuchKey")
            || exception.getCause()
            .getMessage()
            .contains("The specified key does not exist"),
        "Exception should be related to NoSuchKey");
  }

  @Test
  void testReplaceObject() throws ExecutionException, InterruptedException {
    String TEST_KEY_NAME = "tests/test-replace-object.json";
    objectStorage.copyObject(BUCKET_NAME, KEY_NAME, TEST_KEY_NAME).join();
    // First, get the object to obtain its etag
    CompletableFuture<ObjectStorage.GetObjectResponse> getResponse =
        objectStorage.getObject(BUCKET_NAME, TEST_KEY_NAME);


    ObjectStorage.GetObjectResponse originalObject = getResponse.get();
    String etag = originalObject.etag();

    // Modify the content
    String originalContent = new String(originalObject.content(), StandardCharsets.UTF_8);
    String modifiedContent = originalContent.replace("Hydrogen", "Modified Hydrogen");
    byte[] modifiedBytes = modifiedContent.getBytes(StandardCharsets.UTF_8);


    // When: Replace the object
    CompletableFuture<?> replaceResponse =
        objectStorage.replaceObject(BUCKET_NAME, TEST_KEY_NAME, modifiedBytes, etag);

    // Then: Verify the replace operation completed successfully
    assertNotNull(replaceResponse, "Replace response should not be null");
    replaceResponse.get(); // This will throw an exception if the operation failed

    // Verify the object was actually replaced by getting it again
    CompletableFuture<ObjectStorage.GetObjectResponse> verifyResponse =
        objectStorage.getObject(BUCKET_NAME, TEST_KEY_NAME);
    ObjectStorage.GetObjectResponse updatedObject = verifyResponse.get();

    String updatedContent = new String(updatedObject.content(), StandardCharsets.UTF_8);
    assertTrue(updatedContent.contains("Modified Hydrogen"),
        "Updated content should contain the modified text");
    assertNotEquals(etag, updatedObject.etag(),
        "ETag should be different after replacement");
    objectStorage.deleteObject(BUCKET_NAME, TEST_KEY_NAME).join();
  }

  @Test
  void testReplaceObjectWithInvalidEtag() {
    // When: Try to replace an object with an invalid etag
    String invalidEtag = "\"invalid-etag\"";
    byte[] content = "Test content".getBytes(StandardCharsets.UTF_8);
    CompletableFuture<?> replaceResponse =
        objectStorage.replaceObject(BUCKET_NAME, KEY_NAME, content, invalidEtag);

    // Then: The operation should fail with a precondition failed exception
    assertNotNull(replaceResponse, "Replace response should not be null");
    Exception exception = assertThrows(ExecutionException.class, () -> {
      replaceResponse.get();
    });

    // Print the actual exception message for debugging
    System.out.println("[DEBUG_LOG] Exception message: " + exception.getCause().getMessage());

    // Check for various possible error messages related to ETag mismatch
    assertTrue(exception.getCause().getMessage().contains("PreconditionFailed")
            || exception.getCause().getMessage().contains("precondition failed")
            || exception.getCause().getMessage().contains("Precondition Failed")
            || exception.getCause().getMessage().contains("condition not met")
            || exception.getCause().getMessage().contains("Condition not met")
            || exception.getCause().getMessage().contains("ETag")
            || exception.getCause().getMessage().contains("etag")
            || exception.getCause().getMessage().contains("pre-conditions")
            || exception.getCause().getMessage().contains("Status Code: 412"),
        "Exception should be related to precondition failure due to ETag mismatch");
  }

  @Test
  void testReplaceObjectWithInvalidBucket() {
    // When: Try to replace an object in a non-existent bucket
    byte[] content = "Test content".getBytes(StandardCharsets.UTF_8);
    CompletableFuture<?> replaceResponse =
        objectStorage.replaceObject("invalid-bucket", KEY_NAME, content, "\"some-etag\"");

    // Then: The operation should fail with a no such bucket exception
    assertNotNull(replaceResponse, "Replace response should not be null");
    Exception exception = assertThrows(ExecutionException.class, () -> {
      replaceResponse.get();
    });

    assertTrue(exception.getCause().getMessage().contains("NoSuchBucket")
            || exception.getCause().getMessage().contains("The specified bucket does not exist"),
        "Exception should be related to NoSuchBucket");
  }

  @Test
  void testReplaceObjectWithNonExistentKey() {
    // When: Try to replace a non-existent object
    byte[] content = "Test content".getBytes(StandardCharsets.UTF_8);
    CompletableFuture<?> replaceResponse =
        objectStorage.replaceObject(BUCKET_NAME, "non-existent-key.json", content, "\"some-etag\"");

    // Then: The operation should fail with a not found exception
    assertNotNull(replaceResponse, "Replace response should not be null");
    Exception exception = assertThrows(ExecutionException.class, () -> {
      replaceResponse.get();
    });

    // The error could be either NoSuchKey or PreconditionFailed depending on the S3 implementation
    assertTrue(exception.getCause().getMessage().contains("NoSuchKey")
            || exception.getCause().getMessage().contains("The specified key does not exist")
            || exception.getCause().getMessage().contains("PreconditionFailed")
            || exception.getCause().getMessage().contains("precondition failed"),
        "Exception should be related to the key not existing or precondition failure");
  }
}
