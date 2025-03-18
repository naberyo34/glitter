package com.example.demo.domain.Post;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.generated.Post;

@Service
public class PostService {
  @Autowired
  PostRepository postRepository;

  /**
   * ユーザー ID に紐づく投稿を取得する
   * 
   * @param userId
   * @return 投稿のリスト
   */
  public List<PostDto> getPostsByUserId(String userId) {
    Stream<Post> posts = postRepository.findByUserId(userId).stream();
    return posts.map(p -> new PostDto(p.getId(), p.getUserId(), p.getContent(), p.getCreatedAt())).toList();
  }

  /**
   * ユーザー ID と内容を指定して投稿する
   * 投稿に成功した場合、投稿の DTO を返す
   * 
   * @param post
   */
  public Optional<PostDto> addPost(PostParams postParams) throws Exception {
    Post post = new Post();
    post.setUserId(postParams.getUserId());
    post.setContent(postParams.getContent());
    // 現在時刻を設定
    post.setCreatedAt(new Date());

    try {
      Post p = postRepository.insert(post);
      return Optional.of(new PostDto(p.getId(), p.getUserId(), p.getContent(), p.getCreatedAt()));
    } catch (Exception e) {
      throw e;
    }
  }
}
