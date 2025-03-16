# config

Spring Security の OAuth2 認証機能に関連するファイルを配置します。

OAuth2 を用いた認証の実装は以下のドキュメントを参考にしています。
https://www.danvega.dev/blog/spring-security-jwt

## 注意事項

`UserDetailsImpl.java` と `UserDetailServiceImpl.java` は、Spring Security 関連処理でのみ参照されることを想定しているため、あえてこちらに配置しています。

アプリケーションでユーザーを取り扱う際は、必ず上記ではなく `domain/User` を利用してください。
