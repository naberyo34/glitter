package com.example.glitter.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.glitter.domain.Post.PostDto;
import com.example.glitter.domain.Post.PostRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
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

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  @WithMockUser(username = "test_user")
  @Transactional
  void ログイン状態で投稿を追加できる() throws Exception {
    PostRequestDto post = new PostRequestDto("new post");
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
  @Transactional
  void 非ログイン状態の投稿で401が返る() throws Exception {
    PostRequestDto post = new PostRequestDto("new post");
    mockMvc.perform(post("/post")
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        .content(objectMapper.writeValueAsString(post)))
        .andExpect(status().isUnauthorized());
  }
}
