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
   * @return UserSummaryDto
   * @throws Exception
   */
  public UserSummaryDto updateIcon(MultipartFile file) throws Exception {
    // セッションユーザーの取得
    UserSummaryDto sessionUser = userService.getSessionUser().orElseThrow();
    // 既存のアイコン画像を削除
    if (StringUtils.hasLength(sessionUser.getIcon())) {
      imageService.delete(sessionUser.getIcon());
    }
    // ファイル名、パスを作成
    // upload は 画像を jpg 形式で保存するため、拡張子は必ず jpg
    String fileName = UUID.randomUUID().toString() + ".jpg";
    String key = sessionUser.getId() + "/" + fileName;
    // 画像をアップロード
    imageService.upload(file, key);
    // データベース上のアイコン画像のパスを更新
    sessionUser.setIcon(key);
    return userService.updateFromSummary(sessionUser);
  }
}
