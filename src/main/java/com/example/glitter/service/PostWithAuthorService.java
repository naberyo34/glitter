package com.example.glitter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.glitter.domain.Post.PostRepository;
import com.example.glitter.domain.Post.PostResponse;
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
   * ID からユーザー情報付きの投稿を取得する
   * 
   * @param id
   * @return 投稿の DTO
   */
  public Optional<PostResponse> findById(Long id) {
    Optional<Post> postOpt = postRepository.findById(id);
    if (postOpt.isEmpty()) {
      return Optional.empty();
    }
    Post post = postOpt.get();
    User user = userRepository.findById(post.getUserId()).orElseThrow();
    return Optional.of(PostResponse.fromEntity(post, user));
  }

  /**
   * ユーザー ID からユーザー情報付きの投稿リストを取得する
   * 
   * @param userId
   * @return 投稿のリスト
   */
  public List<PostResponse> findPostsByUserId(String userId) {
    Stream<Post> posts = postRepository.findPostsByUserId(userId).stream();
    return posts.map(p -> {
      User user = userRepository.findById(p.getUserId()).orElseThrow();
      return PostResponse.fromEntity(p, user);
    }).toList();
  }
}
