package com.example.glitter.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
@Transactional
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
  @WithMockJwt
  void ユーザーのフォロー一覧を取得できる() throws Exception {
    mockMvc.perform(get("/user/test_user/following"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].userId").exists())
        .andExpect(jsonPath("$[1].userId").exists());
  }

  @Test
  void 存在しないユーザーのフォロー一覧を取得しようとすると404が返る() throws Exception {
    mockMvc.perform(get("/user/not_exist_user/following"))
        .andExpect(status().isNotFound());
  }

  @Test
  void ユーザーのフォロワー一覧を取得できる() throws Exception {
    mockMvc.perform(get("/user/test_user/followers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].userId").exists())
        .andExpect(jsonPath("$[1].userId").exists());
  }

  @Test
  void 存在しないユーザーのフォロワー一覧を取得しようとすると404が返る() throws Exception {
    mockMvc.perform(get("/user/not_exist_user/followers"))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockJwt
  void ログイン中に自分のフォロー一覧を取得できる() throws Exception {
    mockMvc.perform(get("/user/me/following"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].userId").exists())
        .andExpect(jsonPath("$[1].userId").exists());
  }

  @Test
  void 非ログイン中に自分のフォロー一覧を取得しようとすると401が返る() throws Exception {
    mockMvc.perform(get("/user/me/following"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockJwt
  void ログイン中に自分のフォロワー一覧を取得できる() throws Exception {
    // test_user としてログイン中なので、シードデータのフォロワー一覧を取得
    mockMvc.perform(get("/user/me/followers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].userId").exists())
        .andExpect(jsonPath("$[1].userId").exists());
  }

  @Test
  void 非ログイン中に自分のフォロワー一覧を取得しようとすると401が返る() throws Exception {
    mockMvc.perform(get("/user/me/followers"))
        .andExpect(status().isUnauthorized());
  }
}
