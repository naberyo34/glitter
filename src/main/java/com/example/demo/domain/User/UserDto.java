package com.example.demo.domain.User;

/**
 * ユーザー DTO
 * パスワードや認証情報を含まないユーザー情報です。
 */
public class UserDto {
  private String id;
  private String username;
  private String profile;
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
