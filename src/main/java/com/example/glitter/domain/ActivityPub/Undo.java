package com.example.glitter.domain.ActivityPub;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * ActivityPub の Undo Object
 * 以前実行したアクションを取り消すことを示します。
 * 例えば、フォローを解除する場合などに使用します。
 * @see https://www.w3.org/TR/activitystreams-vocabulary/#dfn-undo
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(Include.NON_NULL)
public class Undo extends ActivityPubObject {
  @Schema(description = "オブジェクトのタイプ", example = "Undo", requiredMode = Schema.RequiredMode.REQUIRED)
  private final String type = "Undo";

  @Schema(description = "取り消しを申請する Actor Endpoint", example = "https://example.com/user/test_user", requiredMode = Schema.RequiredMode.REQUIRED)
  private String actor;

  @Schema(description = "取り消す対象のアクション", requiredMode = Schema.RequiredMode.REQUIRED)
  private ActivityPubObject object;
}
