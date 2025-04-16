package com.example.glitter.domain.WebFinger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.glitter.domain.WebFinger.WebFinger.Link;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebFingerServiceTest {
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
  private WebFingerService webFingerService;

  @Value("${env.api-url}")
  private String apiUrl;

  @Value("${env.domain}")
  private String domain;

  @Test
  void 正しいリソースを渡すと適切なJRDが返る() {
    String resource = "acct:test_user@" + domain;
    Optional<WebFinger> jrdOpt = webFingerService.getJrd(resource);

    assertTrue(jrdOpt.isPresent());
    WebFinger jrd = jrdOpt.get();

    // 検証
    assertEquals(resource, jrd.getSubject());
    List<Link> links = jrd.getLinks();
    assertNotNull(links);
    assertTrue(links.size() > 0);

    Link selfLink = links.get(0);
    assertEquals("self", selfLink.getRel());
    assertEquals("application/activity+json", selfLink.getType());
    assertEquals(apiUrl + "/user/test_user", selfLink.getHref());
  }

  @Test
  void 存在するユーザーだがドメイン名が誤っている場合Emptyが返る() {
    String resource = "acct:test_user@" + "malicious.com";
    Optional<WebFinger> jrdOpt = webFingerService.getJrd(resource);

    assertTrue(jrdOpt.isEmpty());
  }

  @Test
  void 存在しないユーザーのリソースを渡すとEmptyが返る() {
    String resource = "acct:not_exist_user@" + domain;
    Optional<WebFinger> jrdOpt = webFingerService.getJrd(resource);

    assertTrue(jrdOpt.isEmpty());
  }
}
