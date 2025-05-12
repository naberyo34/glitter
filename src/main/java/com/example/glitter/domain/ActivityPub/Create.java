package com.example.glitter.domain.ActivityPub;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * ActivityPub の Create Object
 * 何らかのリソースを作成したことを示します。
 * たとえば、投稿の通知に利用します。
 * @see https://www.w3.org/TR/activitystreams-vocabulary/#dfn-accept
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(Include.NON_NULL)
public class Create extends ActivityPubObject {
  @Schema(description = "オブジェクトのタイプ", example = "Create", requiredMode = Schema.RequiredMode.REQUIRED)
  private final String type = "Create";

  @Schema(description = "送信者の Actor Endpoint", example = "https://example.com/user/test_user", requiredMode = Schema.RequiredMode.REQUIRED)
  private String actor;

  @Schema(description = "通知対象のリソース", requiredMode = Schema.RequiredMode.REQUIRED)
  private ActivityPubObject object;

  @Schema(description = "通知の公開範囲", example = "https://www.w3.org/ns/activitystreams#Public", requiredMode = Schema.RequiredMode.REQUIRED)
  private final String[] to = {"https://www.w3.org/ns/activitystreams#Public"};

  @Schema(description = "通知の cc (フォロワー)", example = "https://example.com/user/test_user/followers", requiredMode = Schema.RequiredMode.REQUIRED)
  private String cc;
}
