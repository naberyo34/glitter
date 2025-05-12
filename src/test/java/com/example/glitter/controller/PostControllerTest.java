package com.example.glitter.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.glitter.domain.ActivityPub.Note;
import com.example.glitter.domain.Post.PostDto;
import com.example.glitter.domain.Post.PostRequest;
import com.example.glitter.domain.Post.PostWithAuthor;
import com.example.glitter.util.WithMockJwt;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
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
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
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

  @Value("${env.api-url}")
  private String apiUrl;

  @Test
  void IDから投稿を取得できる() throws Exception {
    mockMvc.perform(get("/post/uuid_1"))
        .andExpect(status().isOk()).andExpect(result -> {
          String content = result.getResponse().getContentAsString();
          PostWithAuthor resultPostResponse = objectMapper.readValue(content, PostWithAuthor.class);
          assertEquals("uuid_1", resultPostResponse.getUuid());
        });
  }

  @Test
  void ActivityPubとして投稿を取得したとき正しいNoteJSONが返る() throws Exception {
    mockMvc.perform(get("/post/uuid_1")
        .accept(MediaType.parseMediaType("application/activity+json")))
        .andExpect(status().isOk()).andExpect(result -> {
          String content = result.getResponse().getContentAsString();
          Note resultNote = objectMapper.readValue(content, Note.class);
          assertEquals(apiUrl + "/post/uuid_1", resultNote.getId());
          assertEquals("Note", resultNote.getType());
        });
  }

  @Test
  void IDから投稿が見つからないとき404が返る() throws Exception {
    mockMvc.perform(get("/post/uuid_999"))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockJwt
  void ログイン状態で投稿を追加できる() throws Exception {
    mockServer.expect(requestTo("http://localhost:8080/user/test_user_2/inbox"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess());
    mockServer.expect(requestTo("http://localhost:8080/user/test_user_3/inbox"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess());

    PostRequest post = new PostRequest("new post");
    mockMvc.perform(post("/post")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(post)))
        .andExpect(status().isOk()).andExpect(result -> {
          String content = result.getResponse().getContentAsString();
          PostDto resultPostDto = objectMapper.readValue(content, PostDto.class);
          assertEquals("new post", resultPostDto.getContent());
        });
    mockServer.verify();
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
