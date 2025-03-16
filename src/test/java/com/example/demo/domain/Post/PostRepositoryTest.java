package com.example.demo.domain.Post;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.generated.Post;

@SpringBootTest
public class PostRepositoryTest {
  @Autowired
  private PostRepository postRepository;

  @Test
  void ユーザーIDに紐づく投稿を取得したときその投稿のリストが返る() throws Exception {
    List<Post> posts = postRepository.findByUserId("test_user");
    assertThat(posts).isNotEmpty();
  }

  @Test
  void ユーザーIDに紐づく投稿が存在しないとき空のリストが返る() throws Exception {
    List<Post> posts = postRepository.findByUserId("test_user_2");
    assertThat(posts).isEmpty();
  }

  @Test
  void 存在しないユーザーの投稿を取得したとき空のリストが返る() throws Exception {
    List<Post> posts = postRepository.findByUserId("not_exist_user");
    assertThat(posts).isEmpty();
  }
}
