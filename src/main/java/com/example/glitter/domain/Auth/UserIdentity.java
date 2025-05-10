package com.example.glitter.domain.Auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * クライアントから受け取る認証情報の型定義
 */
@Data
public class UserIdentity {
  @Schema(description = "ユーザー ID", example = "example", requiredMode = Schema.RequiredMode.REQUIRED)
  private String id;
  @Schema(description = "パスワード", example = "$2a$12$Z3MQA08C1d8S89U7nA0/1eMMxRw061BKTZHl.OlGzZjFMLQs6FC3y", requiredMode = Schema.RequiredMode.REQUIRED)
  private String password;
}
