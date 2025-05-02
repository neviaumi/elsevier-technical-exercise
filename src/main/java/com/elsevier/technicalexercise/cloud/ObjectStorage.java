package com.elsevier.technicalexercise.cloud;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

/**
 * Service for interacting with object storage (S3).
 */
@Component
public class ObjectStorage {
  private final String accessKey = "test";
  private final String secretKey = "test";
  private S3AsyncClient s3Client;

  /**
   * Initializes the object storage client.
   */
  public ObjectStorage() {
    s3Client = S3AsyncClient.builder()
        .region(Region.EU_WEST_2)
        .endpointOverride(URI.create("http://127.0.0.1:4566"))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }

  /**
   * Response object for get object operations.
   *
   * @param content the content of the object
   * @param etag the ETag of the object
   */
  public static record GetObjectResponse(byte[] content, String etag) {
  }

  /**
   * Retrieves an object from the storage.
   *
   * @param bucketName the name of the bucket
   * @param keyName the key of the object
   * @return a future that will complete with the object response
   */
  public CompletableFuture<GetObjectResponse> getObject(String bucketName, String keyName) {
    GetObjectRequest objectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(keyName)
        .build();

    return s3Client.getObject(objectRequest, AsyncResponseTransformer.toBytes())
        .thenApply(objectResponse -> {
          byte[] content = objectResponse.asByteArray();
          String etag = objectResponse.response().eTag();
          return new GetObjectResponse(content, etag);
        });
  }
}
