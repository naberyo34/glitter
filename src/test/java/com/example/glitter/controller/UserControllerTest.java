package com.example.glitter.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.glitter.domain.Post.PostResponseDto;
import com.example.glitter.domain.User.UserResponse;
import com.example.glitter.util.WithMockJwt;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {
  @LocalServerPort
  private int port;

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      "postgres:16-alpine");
  static MinIOContainer minio = new MinIOContainer("minio/minio");

  @BeforeAll
  static void beforeAll() {
    postgres.start();
    minio.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
    minio.stop();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("env.storage-url", minio::getS3URL);
    registry.add("env.minio-username", minio::getUserName);
    registry.add("env.minio-password", minio::getPassword);
    registry.add("env.storage-bucket-name", () -> "test");
  }

  @Autowired
  private MockMvc mockMvc;

  @Value("${env.api-url}")
  private String apiUrl;

  private ObjectMapper objectMapper = new ObjectMapper();
  private String EXAMPLE_IMAGE_FILE_PATH = "src/test/resources/static/images/example.jpg";

  @Test
  void IDからユーザーを取得したときユーザーのDTOが返る() throws Exception {
    mockMvc.perform(get("/user/test_user")).andExpect(status().isOk());
  }

  @Test
  void ActivityPubとしてユーザーを取得したとき正しいActorJSONが返る() throws Exception {
    mockMvc.perform(get("/user/test_user")
        .accept("application/activity+json"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/activity+json"))
        .andExpect(jsonPath("$.id").value(apiUrl + "/user/test_user"))
        .andExpect(jsonPath("$.type").value("Person"))
        .andExpect(jsonPath("$.preferredUsername").value("test_user"))
        .andExpect(jsonPath("$.name").value("テストユーザー"))
        .andExpect(jsonPath("$.summary").value("テスト用のアカウントです。"))
        .andExpect(jsonPath("$.inbox").value(apiUrl + "/user/test_user/inbox"))
        .andExpect(jsonPath("$.outbox").value(apiUrl + "/user/test_user/outbox"));
  }

  @Test
  void ActivityPubとしてOutboxを取得したとき正しいOrderedCollectionJSONが返る() throws Exception {
    mockMvc.perform(get("/user/test_user/outbox"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/activity+json"))
        .andExpect(jsonPath("$.id").value(apiUrl + "/user/test_user/outbox"))
        .andExpect(jsonPath("$.type").value("OrderedCollection"))
        .andExpect(jsonPath("$.totalItems").isNumber())
        .andExpect(jsonPath("$.orderedItems").isArray());
  }

  @Test
  void 存在しないユーザーのOutboxを取得したとき404が返る() throws Exception {
    mockMvc.perform(get("/user/not_exist_user/outbox"))
        .andExpect(status().isNotFound());
  }

  @Test
  void 存在しないユーザーを取得したとき404が返る() throws Exception {
    mockMvc.perform(get("/user/not_exist_user")).andExpect(status().isNotFound());
  }

  @Test
  void 投稿しているユーザーの投稿を取得したとき投稿のリストが返る() throws Exception {
    mockMvc.perform(get("/user/test_user/post")).andExpect(status().isOk()).andExpect((result) -> {
      String content = result.getResponse().getContentAsString();
      List<PostResponseDto> posts = Arrays.asList(objectMapper.readValue(content, PostResponseDto[].class));
      PostResponseDto post = posts.get(0);
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
  @WithMockJwt
  void ログイン中にセッションユーザーを取得したときユーザーのDTOが返る() throws Exception {
    mockMvc.perform(get("/user/me")).andExpect(status().isOk());
  }

  @Test
  void 非ログイン中にセッションユーザーを取得したとき401が返る() throws Exception {
    mockMvc.perform(get("/user/me")).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockJwt
  void ログイン中のユーザーのアイコン画像を変更できる() throws Exception {
    MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "example.jpg", "image/jpeg",
        Files.readAllBytes(Path.of(EXAMPLE_IMAGE_FILE_PATH)));
    MvcResult result = mockMvc.perform(
        MockMvcRequestBuilders.multipart("/user/me/icon")
            .file(mockMultipartFile)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk()).andReturn();

    UserResponse resultUser = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);
    assertTrue(resultUser.getIcon().endsWith(".jpg"));
  }

  @Test
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
