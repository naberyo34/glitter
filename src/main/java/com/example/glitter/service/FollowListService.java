package com.example.glitter.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.glitter.domain.Follow.FollowRepository;
import com.example.glitter.domain.User.UserNotFoundException;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.domain.User.UserDto;
import com.example.glitter.generated.Follow;
import com.example.glitter.generated.User;

@Service
public class FollowListService {
  @Autowired
  private FollowRepository followRepository;
  @Autowired
  private UserRepository userRepository;

  @Value("${env.domain}")
  private String domain;

  /**
   * ユーザーのフォロー一覧を取得する
   * 
   * @param userId ユーザーID
   * @return フォロー一覧
   */
  public List<UserDto> getFollowing(String userId) {
    // ユーザーの存在判定
    userRepository.findByUserIdAndDomain(userId, domain)
        .orElseThrow(() -> new UserNotFoundException());
    List<Follow> follows = followRepository.findFollowing(userId, domain);

    return follows.stream()
        .map(follow -> {
          User followee = userRepository.findByUserIdAndDomain(follow.getFolloweeId(), follow.getFolloweeDomain())
              .orElseThrow();
          return UserDto.fromEntity(followee);
        })
        .collect(Collectors.toList());
  }

  /**
   * ユーザーのフォロワー一覧を取得する
   * 
   * @param userId ユーザーID
   * @return フォロワー一覧
   */
  public List<UserDto> getFollowers(String userId) {
    // ユーザーの存在判定
    userRepository.findByUserIdAndDomain(userId, domain)
        .orElseThrow(() -> new UserNotFoundException());
    List<Follow> followers = followRepository.findFollowers(userId, domain);

    return followers.stream()
        .map(follow -> {
          User follower = userRepository.findByUserIdAndDomain(follow.getFollowerId(), follow.getFollowerDomain())
              .orElseThrow();
          return UserDto.fromEntity(follower);
        })
        .collect(Collectors.toList());
  }
}
