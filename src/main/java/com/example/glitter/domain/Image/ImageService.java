package com.example.glitter.domain.Image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
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
  private int MAX_ICON_SIZE = 400;

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
   * 画像をアイコン向けにトリミング、リサイズする
   * 
   * @param InputStream
   * @throws Exception
   */
  public InputStream cropAndResizeIcon(InputStream inputStream) throws Exception {
    BufferedImage originalImage = ImageIO.read(inputStream);

    // 幅と高さのうち、小さい方を基準に正方形にトリミング
    int width = originalImage.getWidth();
    int height = originalImage.getHeight();
    int size = Math.min(width, height);
    int x = (width - size) / 2;
    int y = (height - size) / 2;
    BufferedImage squareImage = originalImage.getSubimage(x, y, size, size);

    // リサイズ
    BufferedImage resizedImage = Scalr.resize(squareImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_EXACT,
        Math.min(size, MAX_ICON_SIZE),
        Math.min(size, MAX_ICON_SIZE));

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ImageIO.write(resizedImage, "jpg", outputStream);

    return new ByteArrayInputStream(outputStream.toByteArray());
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

    InputStream icon = cropAndResizeIcon(file.getInputStream());

    s3Client.putObject(request, RequestBody.fromInputStream(icon, icon.available()));
  }

  /**
   * 画像を削除する
   * 
   * @param key
   * @return void
   * @throws Exception
   */
  public void delete(String key) throws Exception {
    DeleteObjectRequest request = DeleteObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();
    s3Client.deleteObject(request);
  }
}
