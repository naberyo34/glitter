# Glitter

## 初回セットアップ (クローンしたら実行すること)

- `.env.example` を参考に `.env` を作成します。
- `docker compose up --build -d` で開発データベースとストレージのコンテナを立ち上げます。
- `/migrations` ディレクトリの README を参考に MyBatis Migrations を導入、実行します。
- `gradle mbgenerator` でエンティティ、マッパーファイルを生成します。
- `/src/main/resources/certs` 以下で下記を実行します。[参考](https://www.danvega.dev/blog/spring-security-jwt)

```sh
# create rsa key pair
openssl genrsa -out keypair.pem 2048

# extract public key
openssl rsa -in keypair.pem -pubout -out public.pem

# create private key in PKCS#8 format
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private.pem
```

- `gradle build` でビルドとテストが通ることを確認します。

### 起動

- `docker compose up -d` 開発データベースとストレージのコンテナを立ち上げる
- `gradle bootRun` サーバーを立ち上げる

フロントエンド開発については `/view` の README を参照してください。
