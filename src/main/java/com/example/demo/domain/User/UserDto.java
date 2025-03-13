package com.example.demo.domain.User;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ユーザー DTO
 * パスワードや認証情報を含まないユーザー情報です。
 */
public class UserDto {
  @Schema(description = "ユーザー ID", example = "example", requiredMode = Schema.RequiredMode.REQUIRED)
  private String id;
  @Schema(description = "ユーザー名", example = "太郎", requiredMode = Schema.RequiredMode.REQUIRED)
  private String username;
  @Schema(description = "プロフィール", example = "こんにちは。", requiredMode = Schema.RequiredMode.REQUIRED)
  private String profile;
  @Schema(description = "メールアドレス", example = "example@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  private String email;

  public UserDto(String id, String username, String profile, String email) {
    this.id = id;
    this.username = username;
    this.profile = profile;
    this.email = email;
  }

  public String getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getProfile() {
    return profile;
  }

  public String getEmail() {
    return email;
  }
}
