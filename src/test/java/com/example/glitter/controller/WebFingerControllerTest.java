package com.example.glitter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class WebFingerControllerTest {
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

  @Test
  void クエリパラメータなしでアクセスしたとき400が返る() throws Exception {
    mockMvc.perform(get("/.well-known/webfinger"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void 存在するユーザーのリソースを指定したとき正しいJRDが返る() throws Exception {
    mockMvc.perform(get("/.well-known/webfinger?resource=acct:test_user@example.com"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/jrd+json"))
        .andExpect(jsonPath("$.subject").value("acct:test_user@example.com"))
        .andExpect(jsonPath("$.links[0].rel").value("self"))
        .andExpect(jsonPath("$.links[0].type").value("application/activity+json"))
        .andExpect(jsonPath("$.links[0].href").value(apiUrl + "/user/test_user"));
  }

  @Test
  void 存在しないユーザーのリソースを指定したとき404が返る() throws Exception {
    mockMvc.perform(get("/.well-known/webfinger?resource=acct:not_exist_user@example.com"))
        .andExpect(status().isNotFound());
  }

  @Test
  void 存在するユーザーでもドメインが一致しないとき404が返る() throws Exception {
    mockMvc.perform(get("/.well-known/webfinger?resource=acct:test_user@not_exist_domain.com"))
        .andExpect(status().isNotFound());
  }
}
