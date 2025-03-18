package com.example.demo.domain.Post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Optional;

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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostServiceTest {
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
  private PostService postService;

  @Test
  void ユーザーに紐づく投稿を取得できる() throws Exception {
    List<PostDto> posts = postService.getPostsByUserId("test_user");
    assertThat(posts).isNotEmpty();
  }

  @Test
  void ユーザーの投稿がない場合空のリストが返る() throws Exception {
    List<PostDto> posts = postService.getPostsByUserId("test_user_2");
    assertThat(posts).isEmpty();
  }

  @Test
  void 存在しないユーザーの投稿を取得したとき空のリストが返る() throws Exception {
    List<PostDto> posts = postService.getPostsByUserId("not_exist_user");
    assertThat(posts).isEmpty();
  }

  @Test
  @Transactional
  void 投稿を追加できる() throws Exception {
    try {
      Optional<PostDto> newPost = postService.addPost(new PostParams("test_user", "new post"));
      assertThat(newPost.get()).isNotNull();
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  @Transactional
  void 不正なユーザーの投稿が失敗する() throws Exception {
    try {
      postService.addPost(new PostParams("invalid_user", "new post"));
      fail();
    } catch (Exception e) {
      assertNotNull(e);
    }
  }
}
