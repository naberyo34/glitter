package com.example.glitter.controller;

import static org.junit.Assert.assertNotNull;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.glitter.domain.Auth.UserIdentity;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthControllerTest {
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

  @Test
  void 正しいユーザー情報を渡すとトークンを生成できる() throws Exception {
    UserIdentity validUser = new UserIdentity("test_user", "password");
    ObjectMapper objectMapper = new ObjectMapper();
    mockMvc.perform(post("/auth/token").content(objectMapper.writeValueAsString(validUser))
        .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andExpect((result) -> {
          String token = result.getResponse().getContentAsString();
          assertNotNull(token);
        });
  }

  @Test
  void 不正なユーザー情報を渡すと401が返る() throws Exception {
    UserIdentity invalidUser = new UserIdentity("invalid_user", "password");
    ObjectMapper objectMapper = new ObjectMapper();
    mockMvc.perform(post("/auth/token").content(objectMapper.writeValueAsString(invalidUser))
        .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)).andExpect(status().isUnauthorized());
  }
}
