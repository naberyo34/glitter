package com.example.glitter.domain.User;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.glitter.domain.Image.ImageService;

@Service
public class UserIconService {
  @Autowired
  private UserService userService;
  @Autowired
  private ImageService imageService;

  /**
   * セッションユーザーのアイコン画像を更新する
   * 
   * @param file
   * @return userResponse
   * @throws Exception
   */
  public UserResponse updateIcon(MultipartFile file) throws Exception {
    // セッションユーザーの取得
    UserResponse sessionUser = userService.getSessionUser().orElseThrow();
    // 既存のアイコン画像を削除
    if (StringUtils.hasLength(sessionUser.getIcon())) {
      imageService.delete(sessionUser.getIcon());
    }
    // ファイル名、パスを作成
    String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
    String fileName = UUID.randomUUID().toString() +  "." + extension;
    String key = sessionUser.getId() + "/" + fileName;
    // 画像をアップロード
    imageService.upload(file, key);
    // データベース上のアイコン画像のパスを更新
    sessionUser.setIcon(key);
    return userService.updateFromSummary(sessionUser);
  }
}
