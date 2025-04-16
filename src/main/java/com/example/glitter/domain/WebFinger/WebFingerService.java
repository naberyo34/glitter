package com.example.glitter.domain.WebFinger;

import java.util.Optional;

public interface WebFingerService {
  /**
   * WebFinger のリソース URI からユーザー ID を抽出し、
   * JRD (JSON Resource Descriptor) を返却します。
   *
   * @param resource WebFinger のリソース URI (例: acct:user@example.com)
   * @return JRD を表す WebFinger オブジェクト
   */
  Optional<WebFinger> getJrd(String resource);
}

