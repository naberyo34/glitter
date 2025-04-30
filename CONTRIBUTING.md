# Glitter 開発向けセットアップガイド

## 初回セットアップ (クローンしたら実行すること)

- `.env.example` を参考に `.env` を作成します。
- `docker compose up --build -d` で開発データベースとストレージのコンテナを立ち上げます。
- `/migrations` ディレクトリの README を参考に MyBatis Migrations を導入、実行します。
- `gradle mbgenerator` でエンティティ、マッパーファイルを生成します。

- `gradle build` でビルドとテストが通ることを確認します。

### 起動

- `docker compose up -d` 開発データベースとストレージのコンテナを立ち上げる
- `gradle bootRun` サーバーを立ち上げる

フロントエンド開発については `/view` の README を参照してください。
