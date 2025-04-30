package com.example.glitter.domain.Follow;

import java.util.Date;

import com.example.glitter.generated.Follow;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * フォロー DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FollowDto {
  @Schema(description = "フォローする側の ID", example = "test_user", requiredMode = Schema.RequiredMode.REQUIRED)
  private String followerId;
  @Schema(description = "フォローされる側の ID", example = "test_user_2", requiredMode = Schema.RequiredMode.REQUIRED)
  private String followeeId;
  @Schema(description = "フォロー日時", example = "2025-03-10T08:14:12.451+00:00", requiredMode = Schema.RequiredMode.REQUIRED)
  private Date timestamp;

  public FollowDto toEntity() {
    return new FollowDto(followerId, followeeId, timestamp);
  }

  public static FollowDto fromEntity(Follow follow) {
    return new FollowDto(follow.getFollowerId(), follow.getFolloweeId(), follow.getTimestamp());
  }
}
