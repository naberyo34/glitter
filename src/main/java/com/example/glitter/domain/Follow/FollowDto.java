package com.example.glitter.domain.Follow;

import java.util.Date;

import com.example.glitter.generated.Follow;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * フォロー DTO
 */
@Data
@Builder
public class FollowDto {
  @Schema(description = "フォローする側の ID", example = "test_user", requiredMode = Schema.RequiredMode.REQUIRED)
  private String followerId;
  @Schema(description = "フォローする側のドメイン", example = "example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  private String followerDomain;
  @Schema(description = "フォローされる側の ID", example = "test_user_2", requiredMode = Schema.RequiredMode.REQUIRED)
  private String followeeId;
  @Schema(description = "フォローされる側のドメイン", example = "example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  private String followeeDomain;
  @Schema(description = "フォロー日時", example = "2025-03-10T08:14:12.451+00:00", requiredMode = Schema.RequiredMode.REQUIRED)
  private Date createdAt;

  public FollowDto toEntity() {
    return new FollowDto(followerId, followeeDomain, followeeId, followerDomain, createdAt);
  }

  public static FollowDto fromEntity(Follow follow) {
    return new FollowDto(follow.getFollowerId(), follow.getFollowerDomain(), follow.getFolloweeId(),
        follow.getFolloweeDomain(), follow.getCreatedAt());
  }
}
