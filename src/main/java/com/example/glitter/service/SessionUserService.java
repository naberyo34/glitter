package com.example.glitter.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.glitter.domain.Auth.NotLoginException;
import com.example.glitter.domain.User.UserDto;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.generated.User;

@Service
@Transactional
public class SessionUserService {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private FollowListService followListService;
  @Autowired
  private ImageWriteService imageWriteService;

  /**
   * 自身のユーザー情報を取得する
   * 
   * @return セッションユーザー
   * @throws NotLoginException セッションユーザーが存在しない場合
   */
  public UserDto getMe() {
    User user = userRepository.getSessionUser().orElseThrow(() -> new NotLoginException("セッションユーザーが存在しません"));
    return UserDto.fromEntity(user);
  }

  /**
   * 自身のフォロー一覧を取得する
   * 
   * @param userId ユーザーID
   * @return フォロー一覧
   */
  public List<UserDto> getFollowing() {
    UserDto me = getMe();
    return followListService.getFollowing(me.getUserId());
  }

  /**
   * 自身のフォロワー一覧を取得する
   * 
   * @param userId ユーザーID
   * @return フォロワー一覧
   */
  public List<UserDto> getFollowers() {
    UserDto me = getMe();
    return followListService.getFollowers(me.getUserId());
  }

  /**
   * アイコン画像を更新する
   * TODO: 更新時に ActivityPub に通知する
   * 
   * @param file
   * @return userDto
   * @throws Exception
   */
  public UserDto updateIcon(MultipartFile file) throws Exception {
    User me = userRepository.getSessionUser()
        .orElseThrow(() -> new NotLoginException("セッションユーザーが存在しません"));
    // 既存のアイコン画像を削除
    if (StringUtils.hasLength(me.getIcon())) {
      imageWriteService.delete(me.getIcon());
    }
    // ファイル名、パスを作成
    String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
    String fileName = UUID.randomUUID().toString() + "." + extension;
    String key = me.getUserId() + "/" + fileName;
    // 画像をアップロード
    imageWriteService.upload(file, key);
    // データベース上のアイコン画像のパスを更新
    me.setIcon(key);
    return UserDto.fromEntity(userRepository.update(me));
  }
}
