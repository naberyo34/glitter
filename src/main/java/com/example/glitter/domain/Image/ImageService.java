package com.example.glitter.domain.Image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class ImageService {
  @Autowired
  private S3Client s3Client;

  @Value("${env.storage-bucket-name}")
  private String bucketName;

  private long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

  /**
   * ファイルの基本的なバリデーション
   * 
   * @param file
   * @throws Exception
   */
  private void validation(MultipartFile file) throws Exception {
    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new Exception(contentType + " is not image");
    }
    if (file.getSize() > MAX_FILE_SIZE) {
      throw new Exception("file size is too large");
    }
  }

  /**
   * 画像をアップロードする
   * 
   * @param file
   * @param key
   * @return void
   * @throws Exception
   */
  public void upload(MultipartFile file, String key) throws Exception {
    try {
      validation(file);

      PutObjectRequest request = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(key)
          .contentType(file.getContentType())
          .build();

      // バケットがなければ作る
      if (!s3Client.listBuckets().buckets().stream()
          .anyMatch(b -> b.name().equals(bucketName))) {
        s3Client.createBucket(b -> b.bucket(bucketName));
      }

      s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * 画像を削除する
   * 
   * @param key
   * @return void
   * @throws Exception
   */
  public void delete(String key) throws Exception {
    try {
      DeleteObjectRequest request = DeleteObjectRequest.builder()
          .bucket(bucketName)
          .key(key)
          .build();
      s3Client.deleteObject(request);
    } catch (Exception e) {
      throw e;
    }
  }
}
