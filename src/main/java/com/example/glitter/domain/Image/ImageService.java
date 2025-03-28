package com.example.glitter.domain.Image;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.glitter.domain.User.UserSummaryDto;

@Service
public class ImageService {
  // TODO: 画像の保存先はとりあえずローカル
  private Path PATH = Path.of(System.getProperty("user.dir"), "src/main/resources/static/images/");
  private long MAX_FILE_SIZE = 2 * 1024 * 1024; // 4MB
  private int MAX_WIDTH = 800;
  private int MAX_HEIGHT = 800;

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
   * 画像が指定サイズを超えている場合、リサイズしてから保存する
   * 
   * @param inputStream
   * @param outputPath
   * @throws Exception
   */
  private void resizeAndSave(InputStream inputStream, Path outputPath) throws Exception {
    try {
      BufferedImage originalImage = ImageIO.read((inputStream));
      if (originalImage.getWidth() > MAX_WIDTH) {
        originalImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, MAX_WIDTH);
      }
      if (originalImage.getHeight() > MAX_HEIGHT) {
        originalImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_HEIGHT, MAX_HEIGHT);
      }
      ImageIO.write(originalImage, "jpg", outputPath.toFile());
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * 画像をアップロードする
   * アップロードはユーザー情報が必須です。
   * 成功時は保存先のパスを返します。
   * 
   * @param file
   * @param user
   * @return
   * @throws Exception
   */
  public Optional<String> upload(MultipartFile file, UserSummaryDto user) throws Exception {
    try {
      String originalFileName = file.getOriginalFilename();
      if (originalFileName == null) {
        throw new Exception("file name is null");
      }
      validation(file);
      // /static/images/{username}/{fileName} に保存
      Path destination = PATH.resolve(user.getId());
      String fileName = UUID.randomUUID().toString() + ".jpg";
      Path outputPath = destination.resolve(fileName);

      Files.createDirectories(destination);
      resizeAndSave(file.getInputStream(), outputPath);

      return Optional.of(outputPath.toString());
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * 画像を削除する
   */
  public void delete(String path) throws Exception {
    try {
      Path target = PATH.resolve(path);
      Files.deleteIfExists(target);
    } catch (Exception e) {
      throw e;
    }
  }
}
