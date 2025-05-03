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
        CompletableFuture<ObjectStorage.GetObjectResponse> futureResponse = objectStorage.getObject(BUCKET_NAME,
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
        CompletableFuture<ObjectStorage.GetObjectResponse> futureResponse = objectStorage.getObject("invalid-bucket",
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
        CompletableFuture<ObjectStorage.GetObjectResponse> futureResponse = objectStorage.getObject(BUCKET_NAME,
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
}
