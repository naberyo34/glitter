package com.example.glitter.domain.User;

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
   * 対象ユーザーのアイコン画像を更新する
   * 
   * @param file
   * @param user
   * @return
   * @throws Exception
   */
  public UserSummaryDto updateIcon(MultipartFile file, UserSummaryDto user) throws Exception {
    // 既存のアイコン画像を削除
    if (StringUtils.hasLength(user.getIcon())) {
      imageService.delete(user.getIcon());
    }
    // 画像をアップロード
    String iconPath = imageService.upload(file, user).orElseThrow();
    user.setIcon(iconPath);
    return userService.updateFromSummary(user);
  }
}
