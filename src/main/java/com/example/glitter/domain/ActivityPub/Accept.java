package com.example.glitter.domain.ActivityPub;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * ActivityPub の Accept Object
 * 相手から受けた申請を承認することを示します。
 * たとえば、フォロー依頼に対する応答で利用します。
 * @see https://www.w3.org/TR/activitystreams-vocabulary/#dfn-accept
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(Include.NON_NULL)
public class Accept extends ActivityPubObject {
  @Schema(description = "オブジェクトのタイプ", example = "Accept", requiredMode = Schema.RequiredMode.REQUIRED)
  private final String type = "Accept";

  @Schema(description = "承認する Actor Endpoint", example = "https://example.com/user/test_user", requiredMode = Schema.RequiredMode.REQUIRED)
  private String actor;

  @Schema(description = "承認する対象のアクション", requiredMode = Schema.RequiredMode.REQUIRED)
  private ActivityPubObject object;
}
