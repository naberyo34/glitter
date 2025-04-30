package com.example.glitter.domain.WebFinger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.glitter.domain.User.UserService;
import com.example.glitter.domain.User.UserSummaryDto;
import com.example.glitter.domain.WebFinger.WebFinger.Link;

@ExtendWith(MockitoExtension.class)
public class WebFingerServiceTest {
  @Mock
  private UserService userService;

  @InjectMocks
  private WebFingerServiceImpl webFingerService;

  private final String TEST_API_URL = "https://api.example.com";
  private final String TEST_DOMAIN = "example.com";
  private final String TEST_USER_ID = "test_user";

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(webFingerService, "apiUrl", TEST_API_URL);
    ReflectionTestUtils.setField(webFingerService, "domain", TEST_DOMAIN);
  }

  @Test
  void 正しいリソースを渡すと適切なJRDが返る() {
    String resource = "acct:" + TEST_USER_ID + "@" + TEST_DOMAIN;
    UserSummaryDto mockUser = new UserSummaryDto();
    mockUser.setId(TEST_USER_ID);
    when(userService.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));

    Optional<WebFinger> jrdOpt = webFingerService.getJrd(resource);
    assertTrue(jrdOpt.isPresent());

    WebFinger jrd = jrdOpt.get();
    assertEquals(resource, jrd.getSubject());

    List<Link> links = jrd.getLinks();
    assertNotNull(links);
    assertTrue(links.size() > 0);

    Link selfLink = links.get(0);
    assertEquals("self", selfLink.getRel());
    assertEquals("application/activity+json", selfLink.getType());
    assertEquals(TEST_API_URL + "/user/" + TEST_USER_ID, selfLink.getHref());
  }

  @Test
  void 存在するユーザーだがドメイン名が誤っている場合Emptyが返る() {
    String wrongDomain = "malicious.com";
    String resource = "acct:" + TEST_USER_ID + "@" + wrongDomain;

    Optional<WebFinger> jrdOpt = webFingerService.getJrd(resource);
    assertTrue(jrdOpt.isEmpty());
  }

  @Test
  void 存在しないユーザーのリソースを渡すとEmptyが返る() {
    String resource = "acct:not_exist_user@" + TEST_DOMAIN;
    when(userService.findById("not_exist_user")).thenReturn(Optional.empty());

    Optional<WebFinger> jrdOpt = webFingerService.getJrd(resource);
    assertTrue(jrdOpt.isEmpty());
  }

  @Test
  void nullリソースを渡すとEmptyが返る() {
    Optional<WebFinger> jrdOpt = webFingerService.getJrd(null);
    assertTrue(jrdOpt.isEmpty());
  }

  @Test
  void 空文字リソースを渡すとEmptyが返る() {
    Optional<WebFinger> jrdOpt = webFingerService.getJrd("");
    assertTrue(jrdOpt.isEmpty());
  }

  @Test
  void 不正な形式のリソースを渡すとEmptyが返る() {
    String resource = TEST_USER_ID + "@" + TEST_DOMAIN;

    Optional<WebFinger> jrdOpt = webFingerService.getJrd(resource);
    assertTrue(jrdOpt.isEmpty());
  }
}
