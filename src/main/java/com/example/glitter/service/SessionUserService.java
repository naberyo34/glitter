package com.example.glitter.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.glitter.domain.Auth.NotLoginException;
import com.example.glitter.domain.Post.PostDto;
import com.example.glitter.domain.Post.PostRepository;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.domain.User.UserDto;
import com.example.glitter.generated.Post;
import com.example.glitter.generated.User;

@Service
@Transactional
public class SessionUserService {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PostRepository postRepository;
  @Autowired
  private FollowUserListService followUserListService;
  @Autowired
  private ImageWriteService imageWriteService;

  @Value("${env.domain}")
  private String domain;

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
    return followUserListService.getFollowing(me.getUserId());
  }

  /**
   * 自身のフォロワー一覧を取得する
   * 
   * @param userId ユーザーID
   * @return フォロワー一覧
   */
  public List<UserDto> getFollowers() {
    UserDto me = getMe();
    return followUserListService.getFollowers(me.getUserId());
  }

  /**
   * 投稿する
   * 
   * @param content
   * @return
   * @throws Exception
   */
  public Optional<PostDto> addPost(String content) throws Exception {
    UserDto me = getMe();
    Post post = new Post();
    post.setUserId(me.getUserId());
    post.setDomain(me.getDomain());
    post.setContent(content);

    Post result = postRepository.insert(post);
    return Optional.of(PostDto.fromEntity(result));
  }

  /**
   * アイコン画像を更新する
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
