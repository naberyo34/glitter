package com.example.glitter.domain.Post;

import com.example.glitter.domain.User.UserDto;
import com.example.glitter.generated.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 投稿にユーザー情報を結合したレスポンス DTO
 */
@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PostWithAuthor extends PostDto {
  @Schema(description = "ユーザー情報", requiredMode = Schema.RequiredMode.REQUIRED)
  private UserDto user;

  public static PostWithAuthor fromEntity(PostDto post, User user) {
    PostWithAuthor postResponseDto = PostWithAuthor.builder()  
      .uuid(post.getUuid())
      .domain(post.getDomain())
      .userId(post.getUserId())
      .content(post.getContent())
      .createdAt(post.getCreatedAt())
      .user(UserDto.fromEntity(user))
      .build();
    return postResponseDto;
  }
}
