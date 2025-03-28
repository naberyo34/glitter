package com.example.glitter.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.glitter.domain.Post.PostDto;
import com.example.glitter.domain.User.UserSummaryDto;
import com.example.glitter.generated.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTest {
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
  private MockMvc mockMvc;

  private ObjectMapper objectMapper = new ObjectMapper();
  private String EXAMPLE_IMAGE_FILE_PATH = "src/test/resources/static/images/example.jpg";

  @Test
  void IDからユーザーを取得したときユーザーのDTOが返る() throws Exception {
    mockMvc.perform(get("/user/test_user")).andExpect(status().isOk());
  }

  @Test
  void 存在しないユーザーを取得したとき404が返る() throws Exception {
    mockMvc.perform(get("/user/not_exist_user")).andExpect(status().isNotFound());
  }

  @Test
  void 投稿しているユーザーの投稿を取得したとき投稿のリストが返る() throws Exception {
    mockMvc.perform(get("/user/test_user/post")).andExpect(status().isOk()).andExpect((result) -> {
      String content = result.getResponse().getContentAsString();
      List<PostDto> posts = Arrays.asList(objectMapper.readValue(content, PostDto[].class));
      PostDto post = posts.get(0);
      // 投稿日降順のため、「テスト投稿2」が期待される
      assertEquals(post.getContent(), "テスト投稿2");
    });
  }

  @Test
  void 投稿していないユーザーの投稿を取得したとき空のリストが返る() throws Exception {
    mockMvc.perform(get("/user/test_user_2/post")).andExpect(status().isOk()).andExpect((result) -> {
      String content = result.getResponse().getContentAsString();
      assertEquals(content, "[]");
    });
  }

  @Test
  void 存在しないユーザーの投稿を取得したとき404が返る() throws Exception {
    mockMvc.perform(get("/user/not_exist_user/post")).andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "test_user")
  void ログイン中にセッションユーザーを取得したときユーザーのDTOが返る() throws Exception {
    mockMvc.perform(get("/user/me")).andExpect(status().isOk());
  }

  void 非ログイン中にセッションユーザーを取得したとき401が返る() throws Exception {
    mockMvc.perform(get("/user/me")).andExpect(status().isUnauthorized());
  }

  @Test
  @Transactional
  void 正しいパラメーターでユーザーを作成できる() throws Exception {
    User user = new User();
    user.setId("new_user");
    user.setPassword("password");
    user.setUsername("新しいユーザー");
    user.setEmail("new@example.com");

    mockMvc.perform(
        post("/user").content(objectMapper.writeValueAsString(user))
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk()).andExpect((result) -> {
          String content = result.getResponse().getContentAsString();
          User newUser = objectMapper.readValue(content, User.class);
          assertEquals(newUser.getUsername(), "新しいユーザー");
        });
  }

  @Test
  @Transactional
  void 無効なパラメーターでユーザーを作成しようとしたとき400が返る() throws Exception {
    User invalidUser = new User();
    invalidUser.setId("new_user");
    invalidUser.setPassword("password");
    invalidUser.setUsername("新しいユーザー");
    // メールアドレスが無効
    invalidUser.setEmail("invalid_email");

    mockMvc.perform(
        post("/user").content(objectMapper.writeValueAsString(invalidUser))
            .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Transactional
  void 存在するIDでユーザーを作成しようとしたとき409が返る() throws Exception {
    User user = new User();
    user.setId("test_user");
    user.setPassword("password");
    user.setUsername("新しいユーザー");
    user.setEmail("new@example.com");

    mockMvc.perform(
        post("/user").content(objectMapper.writeValueAsString(user))
            .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
        .andExpect(status().isConflict());
  }

  @Test
  @Transactional
  void 存在するメールアドレスでユーザーを作成しようとしたとき409が返る() throws Exception {
    User user = new User();
    user.setId("new_user");
    user.setPassword("password");
    user.setUsername("新しいユーザー");
    user.setEmail("test@example.com");

    mockMvc.perform(
        post("/user").content(objectMapper.writeValueAsString(user))
            .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
        .andExpect(status().isConflict());
  }

  @Test
  @Transactional
  @WithMockUser(username = "test_user")
  void ログイン中のユーザーのアイコン画像を変更できる() throws Exception {
    MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "example.jpg", "image/jpeg",
        Files.readAllBytes(Path.of(EXAMPLE_IMAGE_FILE_PATH)));
    MvcResult result = mockMvc.perform(
        MockMvcRequestBuilders.multipart("/user/me/icon")
            .file(mockMultipartFile)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk()).andReturn();

    // ファイルが追加されていることも確認する
    UserSummaryDto resultUser = objectMapper.readValue(result.getResponse().getContentAsString(), UserSummaryDto.class);
    Path iconPath = Path.of(resultUser.getIcon());
    assertTrue(Files.exists(iconPath));

    // 後始末
    Files.deleteIfExists(iconPath);
    assertTrue(Files.notExists(iconPath));
  }

  @Test
  @Transactional
  void 非ログイン中にユーザーのアイコン画像を変更しようとしたとき401が返る() throws Exception {
    MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "example.jpg", "image/jpeg",
        Files.readAllBytes(Path.of(EXAMPLE_IMAGE_FILE_PATH)));
    mockMvc.perform(
        MockMvcRequestBuilders.multipart("/user/me/icon")
            .file(mockMultipartFile)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isUnauthorized());
  }
}
