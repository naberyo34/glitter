package com.example.glitter.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.glitter.domain.Follow.FollowRepository;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.domain.User.UserResponse;
import com.example.glitter.generated.Follow;
import com.example.glitter.generated.User;

@Service
public class FollowUserListService {
  @Autowired
  private FollowRepository followRepository;
  @Autowired
  private UserRepository userRepository;

  /**
   * ユーザーのフォロー一覧を取得する
   * 
   * @param userId ユーザーID
   * @return フォロー一覧
   */
  public List<UserResponse> getFollowing(String userId) {
    List<Follow> follows = followRepository.findFollowing(userId);
    // ユーザーが存在しない場合例外を投げる
    userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

    return follows.stream()
        .map(follow -> {
          User followee = userRepository.findById(follow.getFolloweeId())
              .orElseThrow();
          return UserResponse.fromEntity(followee);
        })
        .collect(Collectors.toList());
  }

  /**
   * ユーザーのフォロワー一覧を取得する
   * 
   * @param userId ユーザーID
   * @return フォロワー一覧
   */
  public List<UserResponse> getFollowers(String userId) {
    List<Follow> followers = followRepository.findFollowers(userId);
    // ユーザーが存在しない場合例外を投げる
    userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

    return followers.stream()
        .map(follow -> {
          User follower = userRepository.findById(follow.getFollowerId())
              .orElseThrow();
          return UserResponse.fromEntity(follower);
        })
        .collect(Collectors.toList());
  }
}
