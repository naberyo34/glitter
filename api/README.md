# Glitter APi

## Getting Started (WIP)

- 以下、開発マシン上で PostgreSQL が起動されている前提です。（改良予定）

- `.env` を作成し以下を記入します。

```
DB_URL=
DB_USERNAME=
DB_PASSWORD=
CLIENT_URL=
```

- `/migrations` ディレクトリの README を参考に MyBatis Migrations を導入、実行します。
- `./gradlew build`
- `./gradlew mbgenerator`

### 起動

- `./gradlew bootRun`
