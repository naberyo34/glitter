package com.example.glitter.domain.ActivityPub;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * ActivityPub の Follow Object
 * あるユーザーが別のユーザーにフォロー申請することを示します。
 * @see https://www.w3.org/TR/activitystreams-vocabulary/#dfn-follow
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(Include.NON_NULL)
public class Follow extends ActivityPubObject {
  @Schema(description = "オブジェクトのタイプ", example = "Follow", requiredMode = Schema.RequiredMode.REQUIRED)
  private final String type = "Follow";

  @Schema(description = "フォローを申請する Actor Endpoint", example = "https://example.com/user/test_user", requiredMode = Schema.RequiredMode.REQUIRED)
  private String actor;
  
  @Schema(description = "フォロー対象のActor（ユーザー）のID", example = "https://example.com/user/target_user", requiredMode = Schema.RequiredMode.REQUIRED)
  private String object;
}
