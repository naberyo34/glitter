package com.example.glitter.service;

import java.util.Collections;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.glitter.domain.User.UserRepository;
import com.example.glitter.domain.WebFinger.WebFingerResponse;
import com.example.glitter.domain.WebFinger.WebFingerResponse.Link;

@Service
public class WebFingerService {
  @Autowired
  private UserRepository userRepository;

  @Value("${env.api-url}")
  private String apiUrl;

  @Value("${env.domain}")
  private String domain;

  private Pattern ACCT_PATTERN = Pattern.compile("^acct:([^@]+)@(.+)$");

  /**
   * WebFinger のリソース URI からユーザー ID を抽出し、
   * JRD (JSON Resource Descriptor) を返却します。
   *
   * @param resource WebFinger のリソース URI (例: acct:user@example.com)
   * @return JRD を表す WebFinger オブジェクト
   */
  public Optional<WebFingerResponse> getJrd(String resource) {
    if (resource == null || resource.isEmpty()) {
      return Optional.empty();
    }

    // acct:username@domain 形式からユーザー名を抽出
    Matcher matcher = ACCT_PATTERN.matcher(resource);
    if (!matcher.matches()) {
      return Optional.empty();
    }

    String userId = matcher.group(1);
    String resourceDomain = matcher.group(2);

    // ドメインが一致するか確認
    if (!resourceDomain.equals(domain)) {
      return Optional.empty();
    }

    // ユーザーの存在確認
    return userRepository.findByUserIdAndDomain(userId, domain).map(_ -> {
      // JRD (JSON Resource Descriptor) を構築
      Link link = Link.builder()
          .rel("self")
          .type("application/activity+json")
          .href(apiUrl + "/user/" + userId)
          .build();

      return WebFingerResponse.builder()
          .subject(resource)
          .links(Collections.singletonList(link))
          .build();
    });
  }
}
