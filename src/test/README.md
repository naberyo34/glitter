# test

テストを記述します。

## テスト作成のルール

### Repository

Repository のテスト **のみ** 、データベースとの疎通を含むテストを行います。

テスト時は h2 によるテスト用のデータベースが起動されます。
テスト用データベースの初期化は `resources/schema.sql` と `resources/data.sql` によってテスト実行のたびに行われます。

### Service

Service のテストでは、Repository が返すデータはモックする必要があります。
データベースとの疎通は行わず、ビジネスロジックが正しいことを検証します。

### Controller

Controller のテストでも、Service や Repository が返すデータはモックする必要があります。
