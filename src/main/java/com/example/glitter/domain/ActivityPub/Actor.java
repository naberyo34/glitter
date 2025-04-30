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
 * ActivityPub のプロトコルに準拠した Actor Object を表す。
 * @see https://www.w3.org/TR/activitypub/#actor-objects
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class Actor {
  @JsonProperty("@context")
  @Schema(description = "コンテキスト", example = "https://www.w3.org/ns/activitystreams", requiredMode = Schema.RequiredMode.REQUIRED)
  private final List<String> context = List.of("https://www.w3.org/ns/activitystreams");
  
  @Schema(description = "アクターエンドポイントの URL", example = "https://example.com/user/test_user", requiredMode = Schema.RequiredMode.REQUIRED)
  private String id;
  @Schema(description = "Person", example = "Person", requiredMode = Schema.RequiredMode.REQUIRED)
  private final String type = "Person";
  @Schema(description = "ユーザー ID", example = "test_user", requiredMode = Schema.RequiredMode.REQUIRED)
  private String preferredUsername;
  @Schema(description = "表示名", example = "テストユーザー", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;
  @Schema(description = "プロフィール", example = "こんにちは。", requiredMode = Schema.RequiredMode.REQUIRED)
  private String summary;
  
  @Schema(description = "Inbox の URL", example = "https://example.com/user/test_user/inbox", requiredMode = Schema.RequiredMode.REQUIRED)
  private String inbox;
  @Schema(description = "outbox の URL", example = "https://example.com/user/test_user/outbox", requiredMode = Schema.RequiredMode.REQUIRED)
  private String outbox;
  
  @Schema(description = "アイコン画像", example = "https://example.com/bucketName/test_user/icon.jpg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String[] icon;

  @Schema(description = "外部からのインデックスを許可するか。これが true でない場合 Misskey が認識しないらしい", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
  private final Boolean discoverable = true;
}
