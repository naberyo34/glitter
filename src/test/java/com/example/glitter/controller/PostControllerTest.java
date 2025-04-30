package com.example.glitter.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.glitter.domain.ActivityPub.Note;
import com.example.glitter.domain.Post.PostDto;
import com.example.glitter.domain.Post.PostRequest;
import com.example.glitter.domain.Post.PostResponseDto;
import com.example.glitter.util.WithMockJwt;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class PostControllerTest {
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

  @Value("${env.api-url}")
  private String apiUrl;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void IDから投稿を取得できる() throws Exception {
    mockMvc.perform(get("/post/1"))
        .andExpect(status().isOk()).andExpect(result -> {
          String content = result.getResponse().getContentAsString();
          PostResponseDto resultPostResponseDto = objectMapper.readValue(content, PostResponseDto.class);
          assertEquals(resultPostResponseDto.getId(), 1L);
        });
  }

  @Test
  void ActivityPubとして投稿を取得したとき正しいNoteJSONが返る() throws Exception {
    mockMvc.perform(get("/post/1")
        .accept(MediaType.parseMediaType("application/activity+json")))
        .andExpect(status().isOk()).andExpect(result -> {
          String content = result.getResponse().getContentAsString();
          Note resultPostDto = objectMapper.readValue(content, Note.class);
          assertEquals(resultPostDto.getId(), apiUrl + "/post/1");
          assertEquals(resultPostDto.getType(), "Note");
        });
  }

  @Test
  void IDから投稿が見つからないとき404が返る() throws Exception {
    mockMvc.perform(get("/post/9999"))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockJwt
  void ログイン状態で投稿を追加できる() throws Exception {
    PostRequest post = new PostRequest("new post");
    mockMvc.perform(post("/post")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(post)))
        .andExpect(status().isOk()).andExpect(result -> {
          String content = result.getResponse().getContentAsString();
          PostDto resultPostDto = objectMapper.readValue(content, PostDto.class);
          assertEquals(resultPostDto.getContent(), "new post");
        });
  }

  @Test
  void 非ログイン状態の投稿で401が返る() throws Exception {
    PostRequest post = new PostRequest("new post");
    mockMvc.perform(post("/post")
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        .content(objectMapper.writeValueAsString(post)))
        .andExpect(status().isUnauthorized());
  }
}
