# Glitter

## この混沌とした令和のインターネットを照らす一筋の光
- 1人用の ActivityPub 実装です。Mastodon や Misskey のユーザーからフォローされ、ポストを見てもらうことができます。

## 技術
### フロントエンド
- React Router v7
- Tanstack Query
- OpenAPI-fetch
- Ark UI
- Panda CSS

### BFF (というほどのものではない)
- Hono
- Zod

### サーバーサイド
- Spring Boot
- MyBatis
- MyBatis Generation
- MyBatis Migration

### インフラ
#### フロントエンド
- Cloudflare Workers
- Cloudflare Register

#### サーバーサイド
- AWS EC2
- S3
- RDS (PostgreSQL)
- ELB
- Cognito

### 開発環境
- Docker
- MinIO
- Testcontainers
