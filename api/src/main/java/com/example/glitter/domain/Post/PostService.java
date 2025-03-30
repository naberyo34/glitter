package com.example.glitter.domain.Post;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.generated.Post;
import com.example.glitter.generated.User;

import jakarta.validation.Valid;

@Service
@Validated
public class PostService {
  @Autowired
  PostRepository postRepository;
  @Autowired
  UserRepository userRepository;

  /**
   * ユーザー ID に紐づく投稿を取得する
   * 
   * @param userId
   * @return 投稿のリスト
   */
  public List<PostResponseDto> getPostsByUserId(String userId) {
    Stream<Post> posts = postRepository.findByUserId(userId).stream();
    return posts.map(p -> {
      User user = userRepository.findById(p.getUserId()).orElseThrow();
      return PostResponseDto.fromEntity(p, user);
    }).toList();
  }

  /**
   * ユーザー ID と内容を指定して投稿する
   * 投稿に成功した場合、投稿の DTO を返す
   * 
   * @param post
   */
  public Optional<PostDto> add(@Valid PostParamsDto postParamsDto) throws Exception {
    Post post = new Post();
    post.setUserId(postParamsDto.getUserId());
    post.setContent(postParamsDto.getContent());
    // 現在時刻を設定
    post.setCreatedAt(new Date());

    try {
      Post result = postRepository.insert(post);
      return Optional.of(PostDto.fromEntity(result));
    } catch (Exception e) {
      throw e;
    }
  }
}
