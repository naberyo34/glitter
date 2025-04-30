package com.example.glitter.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.glitter.domain.Auth.NotLoginException;
import com.example.glitter.domain.Follow.FollowDto;
import com.example.glitter.domain.Follow.FollowRepository;
import com.example.glitter.domain.Post.PostDto;
import com.example.glitter.domain.Post.PostRepository;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.domain.User.UserResponse;
import com.example.glitter.generated.Follow;
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
  private FollowRepository followRepository;
  @Autowired
  private FollowUserListService followUserListService;
  @Autowired
  private ImageWriteService imageWriteService;

  /**
   * 自身のユーザー情報を取得する
   * 
   * @return セッションユーザー
   * @throws NotLoginException セッションユーザーが存在しない場合
   */
  public UserResponse getMe() {
    User user = userRepository.getSessionUser().orElseThrow(() -> new NotLoginException("セッションユーザーが存在しません"));
    return UserResponse.fromEntity(user);
  }

  /**
   * 自身のフォロー一覧を取得する
   * 
   * @param userId ユーザーID
   * @return フォロー一覧
   */
  public List<UserResponse> getFollowing() {
    UserResponse me = getMe();
    return followUserListService.getFollowing(me.getId());
  }

  /**
   * 自身のフォロワー一覧を取得する
   * 
   * @param userId ユーザーID
   * @return フォロワー一覧
   */
  public List<UserResponse> getFollowers() {
    UserResponse me = getMe();
    return followUserListService.getFollowers(me.getId());
  }

  /**
   * 投稿する
   * 
   * @param content
   * @return
   * @throws Exception
   */
  public Optional<PostDto> addPost(String content) throws Exception {
    UserResponse me = getMe();
    Post post = new Post();
    post.setUserId(me.getId());
    post.setContent(content);
    post.setCreatedAt(new Date());

    Post result = postRepository.insert(post);
    return Optional.of(PostDto.fromEntity(result));
  }

  /**
   * アイコン画像を更新する
   * 
   * @param file
   * @return userResponse
   * @throws Exception
   */
  public UserResponse updateIcon(MultipartFile file) throws Exception {
    User me = userRepository.getSessionUser()
        .orElseThrow(() -> new NotLoginException("セッションユーザーが存在しません"));
    // 既存のアイコン画像を削除
    if (StringUtils.hasLength(me.getIcon())) {
      imageWriteService.delete(me.getIcon());
    }
    // ファイル名、パスを作成
    String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
    String fileName = UUID.randomUUID().toString() + "." + extension;
    String key = me.getId() + "/" + fileName;
    // 画像をアップロード
    imageWriteService.upload(file, key);
    // データベース上のアイコン画像のパスを更新
    me.setIcon(key);
    return UserResponse.fromEntity(userRepository.update(me));
  }

  /**
   * ユーザーをフォローする
   * 
   * @param followeeId フォロー対象のユーザーID
   * @return フォロー情報
   */
  public FollowDto follow(String followeeId) {
    UserResponse me = getMe();

    // フォロー対象のユーザーが存在するか確認
    userRepository.findById(followeeId)
        .orElseThrow(() -> new IllegalArgumentException("フォロー対象のユーザーが見つかりません"));

    // 自分自身はフォロー不可
    if (me.getId().equals(followeeId)) {
      throw new IllegalArgumentException("自分自身をフォローすることはできません");
    }

    // すでにフォローしている場合は既存のフォロー情報を返す
    Optional<Follow> existingFollow = followRepository.findByFollowerIdAndFolloweeId(
        me.getId(), followeeId);
    if (existingFollow.isPresent()) {
      return FollowDto.fromEntity(existingFollow.get());
    }

    // フォロー情報を作成
    Follow follow = new Follow();
    follow.setFollowerId(me.getId());
    follow.setFolloweeId(followeeId);
    follow.setTimestamp(new Date());
    Follow savedFollow = followRepository.insert(follow);

    return FollowDto.fromEntity(savedFollow);
  }

  /**
   * ユーザーのフォローを解除する
   * 
   * @param followeeId フォロー解除対象のユーザー ID
   * @return 成功した場合は true
   */
  public boolean unfollow(String followeeId) {
    UserResponse me = getMe();
    int deletedRows = followRepository.delete(me.getId(), followeeId);
    return deletedRows > 0;
  }
}
