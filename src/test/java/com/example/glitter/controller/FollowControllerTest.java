package com.example.glitter.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.glitter.util.WithMockJwt;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class FollowControllerTest {

  @Autowired
  private MockMvc mockMvc;

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

  @Test
  @Transactional
  @WithMockJwt
  void ログイン中にユーザーをフォローできる() throws Exception {
    // TODO: シードデータにはすでに test_user -> test_user_2 のフォロー関係があるので、
    // このテストは重複フォローとなるが、サービス側で冪等性を担保している
    mockMvc.perform(post("/user/test_user_2/follow"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.followerId").value("test_user"))
        .andExpect(jsonPath("$.followeeId").value("test_user_2"));
  }

  @Test
  @Transactional
  void 非ログイン中にユーザーをフォローしようとすると401が返る() throws Exception {
    mockMvc.perform(post("/user/test_user_2/follow"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Transactional
  @WithMockJwt
  void 存在しないユーザーをフォローしようとすると404が返る() throws Exception {
    mockMvc.perform(post("/user/not_exist_user/follow"))
        .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  @WithMockJwt
  void ログイン中にユーザーのフォローを解除できる() throws Exception {
    mockMvc.perform(delete("/user/test_user_2/follow"))
        .andExpect(status().isOk());
  }

  @Test
  @Transactional
  void 非ログイン中にユーザーのフォローを解除しようとすると401が返る() throws Exception {
    mockMvc.perform(delete("/user/test_user_2/follow"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Transactional
  @WithMockJwt
  void フォローしていないユーザーのフォローを解除しようとすると404が返る() throws Exception {
    mockMvc.perform(delete("/user/not_exist_user/follow"))
        .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  @WithMockJwt
  void ユーザーのフォロー一覧を取得できる() throws Exception {
    mockMvc.perform(get("/user/test_user/following"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id").exists())
        .andExpect(jsonPath("$[1].id").exists());
  }

  @Test
  @Transactional
  void 存在しないユーザーのフォロー一覧を取得しようとすると404が返る() throws Exception {
    mockMvc.perform(get("/user/not_exist_user/following"))
        .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void ユーザーのフォロワー一覧を取得できる() throws Exception {
    mockMvc.perform(get("/user/test_user/followers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id").exists())
        .andExpect(jsonPath("$[1].id").exists());
  }

  @Test
  @Transactional
  void 存在しないユーザーのフォロワー一覧を取得しようとすると404が返る() throws Exception {
    mockMvc.perform(get("/user/not_exist_user/followers"))
        .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  @WithMockJwt
  void ログイン中に自分のフォロー一覧を取得できる() throws Exception {
    mockMvc.perform(get("/user/me/following"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id").exists())
        .andExpect(jsonPath("$[1].id").exists());
  }

  @Test
  @Transactional
  void 非ログイン中に自分のフォロー一覧を取得しようとすると401が返る() throws Exception {
    mockMvc.perform(get("/user/me/following"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Transactional
  @WithMockJwt
  void ログイン中に自分のフォロワー一覧を取得できる() throws Exception {
    // test_user としてログイン中なので、シードデータのフォロワー一覧を取得
    mockMvc.perform(get("/user/me/followers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id").exists())
        .andExpect(jsonPath("$[1].id").exists());
  }

  @Test
  @Transactional
  void 非ログイン中に自分のフォロワー一覧を取得しようとすると401が返る() throws Exception {
    mockMvc.perform(get("/user/me/followers"))
        .andExpect(status().isUnauthorized());
  }
}
