package com.example.glitter.domain.WebFinger;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.glitter.domain.User.UserService;

@Service
public class WebFingerServiceImpl implements WebFingerService {
  @Autowired
  private UserService userService;

  @Value("${env.api-url}")
  private String apiUrl;

  @Value("${env.domain}")
  private String domain;

  private Pattern ACCT_PATTERN = Pattern.compile("^acct:([^@]+)@(.+)$");

  @Override
  public Optional<Map<String, Object>> getJrd(String resource) {
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
    return userService.findById(userId).map(_ -> {
      // JRD (JSON Resource Descriptor) を構築
      return Map.of(
          "subject", resource,
          "links", List.of(
              Map.of(
                  "rel", "self",
                  "type", "application/activity+json",
                  "href", apiUrl + "/actor/" + userId)
          )
      );
    });
  }
}
