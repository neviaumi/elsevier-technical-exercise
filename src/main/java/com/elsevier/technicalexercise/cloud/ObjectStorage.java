package com.elsevier.technicalexercise.cloud;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Service for interacting with object storage (S3).
 */
@Component
public class ObjectStorage {
  private S3AsyncClient s3Client;

  /**
   * Initializes the object storage client.
   */
  public ObjectStorage(
      @Value("${application.environment}") String appEnvironment,
      @Value("${aws.s3.region}") String awsRegion,
      @Value("${aws.s3.endpoint-override:#{null}}") String endpointOverride
  ) {
    System.out.println("Initializing ObjectStorage with appEnvironment: " + appEnvironment);
    S3AsyncClientBuilder s3ClientBuilder = S3AsyncClient.builder();
    if (List.of("test", "development").contains(appEnvironment)) {
      s3ClientBuilder = s3ClientBuilder
          .region(Region.of(awsRegion))
          .endpointOverride(URI.create(endpointOverride))
          .credentialsProvider(StaticCredentialsProvider.create(
              AwsBasicCredentials.create("test", "test")));
    }
    s3Client = s3ClientBuilder.build();
  }

  /**
   * Response object for get object operations.
   *
   * @param content the content of the object
   * @param etag    the ETag of the object
   */
  public static record GetObjectResponse(byte[] content, String etag) {
  }

  /**
   * Retrieves an object from the storage.
   *
   * @param bucketName the name of the bucket
   * @param keyName    the key of the object
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

  /**
   * Copies an object within a bucket.
   *
   * @param bucketName         the name of the bucket
   * @param sourceKeyName      the key of the source object
   * @param destinationKeyName the key for the destination object
   * @return a future that will complete when the copy operation is done
   */
  public CompletableFuture<?> copyObject(String bucketName, String sourceKeyName,
                                         String destinationKeyName) {
    CopyObjectRequest copyObjectRequest =
        CopyObjectRequest.builder().sourceBucket(bucketName).sourceKey(sourceKeyName)
            .destinationBucket(bucketName)
            .destinationKey(destinationKeyName).build();
    return s3Client.copyObject(copyObjectRequest);
  }

  /**
   * Replaces an object in the storage with new content.
   *
   * @param bucketName the name of the bucket
   * @param keyName    the key of the object to replace
   * @param content    the new content for the object
   * @param etag       the ETag of the object to ensure consistency
   * @return a future that will complete when the replace operation is done
   */
  public CompletableFuture<?> replaceObject(String bucketName, String keyName, byte[] content,
                                            String etag) {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName).ifMatch(etag)
        .key(keyName)
        .build();

    return s3Client.putObject(putObjectRequest, AsyncRequestBody.fromBytes(content));
  }

  /**
   * Deletes an object from the storage.
   *
   * @param bucketName the name of the bucket
   * @param keyName    the key of the object to delete
   * @return a future that will complete when the delete operation is done
   */
  public CompletableFuture<?> deleteObject(String bucketName, String keyName) {
    return s3Client.deleteObject(
        DeleteObjectRequest.builder().bucket(bucketName).key(keyName).build());
  }
}
