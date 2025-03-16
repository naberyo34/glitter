package com.example.demo.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.Post.PostDto;
import com.example.demo.domain.Post.PostRepository;
import com.example.demo.generated.Post;
import com.example.demo.generated.User;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PostRepository postRepository;

  /**
   * ID からユーザー DTO を取得する
   * 
   * @param userId
   * @return 合致するユーザー DTO  (存在しない場合は null)
   */
  @Transactional
  public Optional<UserDto> findById(String userId) {
    Optional<User> user = userRepository.findById(userId);
    return user.map(u -> new UserDto(u.getId(), u.getUsername(), u.getProfile(), u.getEmail()));
  }

  /**
   * セッションユーザー DTO を取得する
   * @return セッションユーザー DTO  (存在しない場合は null)
   */
  @Transactional
  public Optional<UserDto> getSessionUser() {
    Optional<User> user = userRepository.getSessionUser();
    return user.map(u -> new UserDto(u.getId(), u.getUsername(), u.getProfile(), u.getEmail()));
  }

  /**
   * ユーザー ID に紐づく投稿を取得する
   * 
   * @param userId
   * @return 投稿のリスト
   */
  @Transactional
  public List<PostDto> getUserPosts(String userId) {
    Stream<Post> posts = postRepository.findByUserId(userId).stream();
    return posts.map(p -> new PostDto(p.getId(), p.getUserId(), p.getContent(), p.getCreatedAt())).toList();
  }
}
