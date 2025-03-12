package com.example.demo.domain.Post;


import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 投稿 DTO
 */
public class PostDto {
  @Schema(description = "投稿 ID", example = "1", required = true)
  private Integer id;
  @Schema(description = "ユーザー ID", example = "example", required = true)
  private String userId;
  @Schema(description = "本文", example = "がんばります。", required = true)
  private String content;
  @Schema(description = "投稿日時", example = "2025-03-10T08:14:12.451+00:00", required = true)
  private Date createdAt;

  public PostDto(Integer id, String userId, String content, Date createdAt) {
    this.id = id;
    this.userId = userId;
    this.content = content;
    this.createdAt = createdAt;
  }

  public Integer getId() {
    return id;
  }

  public String getUserId() {
    return userId;
  }

  public String getContent() {
    return content;
  }

  public Date getCreatedAt() {
    return createdAt;
  }
}
