package com.example.glitter.domain.Post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.glitter.generated.Post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class PostRepositoryTest {
  @LocalServerPort
  private int port;

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      "postgres:16-alpine");

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  private PostRepository postRepository;

  @Test
  void IDに紐づく投稿を取得したときその投稿が返る() throws Exception {
    assertThat(postRepository.findByUuid("uuid_1")).isNotNull();
  }

  @Test
  void 存在しないIDの投稿を取得したときemptyが返る() throws Exception {
    assertThat(postRepository.findByUuid("uuid_999")).isEmpty();
  }

  @Test
  void ユーザーIDに紐づく投稿を取得したときその投稿のリストが返る() throws Exception {
    List<Post> posts = postRepository.findPostsByUserIdAndDomain("test_user", "example.com");
    assertThat(posts).isNotEmpty();
  }

  @Test
  void ユーザーIDに紐づく投稿が存在しないとき空のリストが返る() throws Exception {
    List<Post> posts = postRepository.findPostsByUserIdAndDomain("test_user_2", "example.com");
    assertThat(posts).isEmpty();
  }

  @Test
  void 存在しないユーザーの投稿を取得したとき空のリストが返る() throws Exception {
    List<Post> posts = postRepository.findPostsByUserIdAndDomain("not_exist_user", "example.com");
    assertThat(posts).isEmpty();
  }

  @Test
  void 正しいユーザーで投稿できる() throws Exception {
    Post post = new Post();
    post.setUserId("test_user");
    post.setDomain("example.com");
    post.setContent("test content");

    Post result = postRepository.insert(post);
    assertNotNull(result);
    assertThat(result.getUserId()).isEqualTo("test_user");
  }

  @Test
  void 存在しないユーザーで投稿に失敗する() throws Exception {
    Post post = new Post();
    post.setUserId("not_exist_user");
    post.setDomain("example.com");
    post.setContent("test content");
    try {
      postRepository.insert(post);
      fail();
    } catch (Exception e) {
      assertNotNull(e);
    }
  }
}
