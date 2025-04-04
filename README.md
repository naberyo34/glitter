# Glitter

## Getting Started

- `docker compose up --build -d` でデータベースとストレージのコンテナを立ち上げます。
- `.env.example` を参考に `.env` を作成します。
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

- `gradle bootRun`
