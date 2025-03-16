# MyBatis Migrations

## Getting Started

- [手順](https://github.com/mybatis/migrations/blob/master/README.md)を参考にインストールし `migrate` コマンドが実行できる状態にしてください。
- `/drivers` に PostgreSQL の JDBC ドライバー（`.jar`）を配置してください。
- `/environments` 内の `development.sample.properties` を参考にデータベースへの接続設定を記載し、ファイル名を `development.properties` を作成してください。
- `migrate status` -> `migrate up`

## DBを操作するとき

- `migrate new "description"` で新規のマイグレーションファイルを作成
- `migrate up`

