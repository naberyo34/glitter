package com.example.glitter.domain.ActivityPub;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ActivityPub の OrderedCollection Object を表す。
 * Outbox Endpoint からの応答としてこれを返します。
 * @see https://www.w3.org/TR/activitystreams-vocabulary/#dfn-orderedcollection
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class OrderedCollection {
  @JsonProperty("@context")
  @Schema(description = "コンテキスト", example = "https://www.w3.org/ns/activitystreams", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<String> context;
  
  @Schema(description = "コレクションエンドポイントの URL", example = "https://example.com/user/test_user/outbox", requiredMode = Schema.RequiredMode.REQUIRED)
  private String id;
  
  @Schema(description = "OrderedCollection", example = "OrderedCollection", requiredMode = Schema.RequiredMode.REQUIRED)
  private String type;
  
  @Schema(description = "コレクションに含まれるアイテムの総数", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
  private int totalItems;
  
  @Schema(description = "コレクションの最初のページへのURL", example = "https://example.com/user/test_user/outbox?page=0", requiredMode = Schema.RequiredMode.REQUIRED)
  private String first;
  
  @Schema(description = "コレクションの最後のページへのURL", example = "https://example.com/user/test_user/outbox?page=1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String last;
  
  @Schema(description = "このコレクションに含まれるアイテムの配列", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private List<Object> orderedItems;
}
