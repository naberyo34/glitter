package com.example.glitter.domain.Post;

import java.util.Date;

import com.example.glitter.generated.Post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 投稿 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {
  @Schema(description = "投稿 uuid", example = "uuid", requiredMode = Schema.RequiredMode.REQUIRED)
  private String uuid;

  @Schema(description = "ユーザー ID", example = "example", requiredMode = Schema.RequiredMode.REQUIRED)
  private String userId;

  @Schema(description = "ユーザーのドメイン", example = "example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  private String domain;

  @NotBlank
  @Schema(description = "本文", example = "がんばります。", requiredMode = Schema.RequiredMode.REQUIRED)
  private String content;

  @Schema(description = "投稿日時", example = "2025-03-10T08:14:12.451+00:00", requiredMode = Schema.RequiredMode.REQUIRED)
  private Date createdAt;

  public Post toEntity() {
    Post post = new Post();
    post.setUuid(uuid);
    post.setUserId(userId);
    post.setDomain(domain);
    post.setContent(content);
    post.setCreatedAt(createdAt);
    return post;
  }

  public static PostDto fromEntity(Post post) {
    return new PostDto(post.getUuid(), post.getUserId(), post.getDomain(), post.getContent(), post.getCreatedAt());
  }
}
