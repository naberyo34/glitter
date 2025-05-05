package com.example.glitter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.glitter.domain.Post.PostRepository;
import com.example.glitter.domain.Post.PostWithAuthor;
import com.example.glitter.domain.User.UserNotFoundException;
import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.generated.Post;
import com.example.glitter.generated.User;

@Service
public class PostWithAuthorService {
  @Autowired
  PostRepository postRepository;
  @Autowired
  UserRepository userRepository;

  /**
   * 投稿の ID からユーザー情報付きの投稿を取得する
   * 
   * @param postId
   * @return 投稿の DTO
   */
  public Optional<PostWithAuthor> findByPostId(String postId) {
    Optional<Post> postOpt = postRepository.findByUuid(postId);
    if (postOpt.isEmpty()) {
      return Optional.empty();
    }
    Post post = postOpt.get();
    User user = userRepository.findByUserIdAndDomain(post.getUserId(), post.getDomain()).orElseThrow();
    return Optional.of(PostWithAuthor.fromEntity(post, user));
  }

  /**
   * ユーザー ID とドメインからユーザー情報付きの投稿リストを取得する
   * 
   * @param userId
   * @return 投稿のリスト
   */
  public List<PostWithAuthor> findPostsByUserIdAndDomain(String userId, String userDomain) {
    // ユーザーの存在判定
    userRepository.findByUserIdAndDomain(userId, userDomain)
        .orElseThrow(() -> new UserNotFoundException());
    Stream<Post> posts = postRepository.findPostsByUserIdAndDomain(userId, userDomain).stream();
    return posts.map(post -> {
      User user = userRepository.findByUserIdAndDomain(post.getUserId(), post.getDomain()).orElseThrow();
      return PostWithAuthor.fromEntity(post, user);
    }).toList();
  }
}
