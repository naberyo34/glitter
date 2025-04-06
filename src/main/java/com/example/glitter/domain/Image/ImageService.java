package com.example.glitter.domain.Image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.glitter.domain.User.UserSummaryDto;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class ImageService {
  @Autowired
  private S3Client s3Client;

  @Value("${env.storage-url}")
  private String url;

  @Value("${env.storage-bucket-name}")
  private String bucketName;

  private long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB
  private int ICON_WIDTH = 400;

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
 * 画像をアイコン向けにリサイズ
 * @param inputStream
 * @return InputStream
 * @throws Exception
 */
private InputStream resizeToIconSize(InputStream inputStream) throws Exception {
    try {
      BufferedImage image = ImageIO.read((inputStream));
      if (image.getWidth() > ICON_WIDTH) {
        image = Scalr.resize(image, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, ICON_WIDTH);
      }
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      ImageIO.write(image, "jpg", outputStream);
      return new ByteArrayInputStream(outputStream.toByteArray());
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * 画像をアップロードする
   * アップロードはユーザー情報が必須です。
   * 成功時はファイル名を返します。
   * 
   * @param file
   * @param user
   * @return ファイル名
   * @throws Exception
   */
  public Optional<String> upload(MultipartFile file, UserSummaryDto user) throws Exception {
    try {
      // 検証
      validation(file);

      // リサイズ
      InputStream resizedImage = resizeToIconSize(file.getInputStream());

      // S3 にアップロード
      String fileName = UUID.randomUUID().toString() + ".jpg";
      PutObjectRequest request = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(fileName)
          .contentType(file.getContentType())
          .build();
      s3Client.putObject(request, RequestBody.fromInputStream(resizedImage, resizedImage.available()));

      return Optional.of(fileName);
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * 画像を削除する
   */
  public void delete(String fileName) throws Exception {
    try {
      DeleteObjectRequest request = DeleteObjectRequest.builder()
          .bucket(bucketName)
          .key(fileName)
          .build();
      s3Client.deleteObject(request);
    } catch (Exception e) {
      throw e;
    }
  }
}
