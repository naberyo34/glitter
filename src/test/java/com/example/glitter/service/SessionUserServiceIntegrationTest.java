package com.example.glitter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.glitter.domain.Post.PostDto;
import com.example.glitter.util.WithMockJwt;

/**
 * 通信を伴う処理のため 結合テストとして実施
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
public class SessionUserServiceIntegrationTest {
  @LocalServerPort
  private int port;

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      "postgres:16-alpine");

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @BeforeEach
  void setUp() {
    mockServer = MockRestServiceServer.createServer(restTemplate);
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
  private SessionUserService sessionUserService;
  @Autowired
  private RestTemplate restTemplate;
  private MockRestServiceServer mockServer;

  @TestConfiguration
  static class TestConfig {
    @Bean
    RestTemplate restTemplate() {
      return new RestTemplate();
    }
  }

  @Test
  @WithMockJwt
  void 投稿を作成できる() throws Exception {
    mockServer.expect(requestTo("http://localhost:8080/user/test_user_2/inbox"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess());
    mockServer.expect(requestTo("http://localhost:8080/user/test_user_3/inbox"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess());
    // テスト実行
    Optional<PostDto> result = sessionUserService.addPost("テスト投稿");

    // 検証
    mockServer.verify();
    assertTrue(result.isPresent());
    assertEquals("test_user", result.get().getUserId());
    assertEquals("テスト投稿", result.get().getContent());
  }
}
