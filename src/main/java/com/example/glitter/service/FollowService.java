package com.example.glitter.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.example.glitter.domain.Follow.FollowDto;
import com.example.glitter.domain.Follow.FollowRepository;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.domain.User.UserResponse;
import com.example.glitter.generated.Follow;
import com.example.glitter.generated.User;

import jakarta.validation.constraints.NotBlank;

@Service
@Validated
@Transactional
public class FollowService {
  @Autowired
  private FollowRepository followRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  /**
   * ユーザーをフォローする
   * 
   * @param followeeId フォロー対象のユーザーID
   * @return フォロー情報
   * @throws IllegalArgumentException ユーザーが見つからない場合
   * @throws AccessDeniedException    セッションユーザーが取得できない場合
   */
  public FollowDto follow(@NotBlank String followeeId) {
    UserResponse sessionUser = userService.getSessionUser()
        .orElseThrow(() -> new AccessDeniedException("セッションユーザーを取得できません"));

    // フォロー対象のユーザーが存在するか確認
    userRepository.findById(followeeId)
        .orElseThrow(() -> new IllegalArgumentException("フォロー対象のユーザーが見つかりません: " + followeeId));

    // 自分自身はフォロー不可
    if (sessionUser.getId().equals(followeeId)) {
      throw new IllegalArgumentException("自分自身をフォローすることはできません");
    }

    // すでにフォローしている場合は既存のフォロー情報を返す
    Optional<Follow> existingFollow = followRepository.findByFollowerIdAndFolloweeId(
        sessionUser.getId(), followeeId);

    if (existingFollow.isPresent()) {
      return FollowDto.fromEntity(existingFollow.get());
    }

    // フォロー情報を作成
    Follow follow = new Follow();
    follow.setFollowerId(sessionUser.getId());
    follow.setFolloweeId(followeeId);
    follow.setTimestamp(new Date());
    Follow savedFollow = followRepository.insert(follow);

    return FollowDto.fromEntity(savedFollow);
  }

  /**
   * ユーザーのフォローを解除する
   * 
   * @param followeeId フォロー解除対象のユーザーID
   * @return 成功した場合はtrue
   * @throws AccessDeniedException セッションユーザーが取得できない場合
   */
  public boolean unfollow(@NotBlank String followeeId) {
    UserResponse sessionUser = userService.getSessionUser()
        .orElseThrow(() -> new AccessDeniedException("セッションユーザーを取得できません"));

    // フォロー情報を削除
    int deletedRows = followRepository.delete(sessionUser.getId(), followeeId);

    return deletedRows > 0;
  }

  /**
   * ユーザーのフォロー一覧を取得する
   * 
   * @param userId ユーザーID
   * @return フォロー一覧
   */
  public List<UserResponse> getFollowing(@NotBlank String userId) {
    userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません: " + userId));

    List<Follow> follows = followRepository.findFollowing(userId);

    return follows.stream()
        .map(follow -> {
          User user = userRepository.findById(follow.getFolloweeId())
              .orElseThrow();
          return UserResponse.fromEntity(user);
        })
        .collect(Collectors.toList());
  }

  /**
   * ユーザーのフォロワー一覧を取得する
   * 
   * @param userId ユーザーID
   * @return フォロワー一覧
   */
  public List<UserResponse> getFollowers(@NotBlank String userId) {
    userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません: " + userId));

    List<Follow> followers = followRepository.findFollowers(userId);

    return followers.stream()
        .map(follow -> {
          User user = userRepository.findById(follow.getFollowerId())
              .orElseThrow();
          return UserResponse.fromEntity(user);
        })
        .collect(Collectors.toList());
  }

  /**
   * セッションユーザーのフォロー一覧を取得する
   * 
   * @return フォロー一覧
   * @throws AccessDeniedException セッションユーザーが取得できない場合
   */
  public List<UserResponse> getMyFollowing() {
    UserResponse sessionUser = userService.getSessionUser()
        .orElseThrow(() -> new AccessDeniedException("セッションユーザーを取得できません"));

    return getFollowing(sessionUser.getId());
  }

  /**
   * セッションユーザーのフォロワー一覧を取得する
   * 
   * @return フォロワー一覧
   * @throws AccessDeniedException セッションユーザーが取得できない場合
   */
  public List<UserResponse> getMyFollowers() {
    UserResponse sessionUser = userService.getSessionUser()
        .orElseThrow(() -> new AccessDeniedException("セッションユーザーを取得できません"));

    return getFollowers(sessionUser.getId());
  }
}
