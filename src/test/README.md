# test

テストを記述します。

## テストの書き方

### 単体テスト

- 単体テストでは、データベースとの疎通を行わなず、依存するコンポーネントはモックすることでテスト対象の挙動を確認します。

```java
@ExtendWith(MockitoExtension.class)
public class HogeServiceTest {
  @Mock
  private HogeRepository hogeRepository;

  @InjectMocks
  private HogeService hogeService;

  @Test
  // ...
}
```

### 結合テスト

- 結合テストでは、`testcontainers` を用いてテスト用のデータベースと疎通し、依存するコンポーネントも原則モックせずにテスト対象の挙動を確認します。

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FugaServiceTest {
  @LocalServerPort
  private int port;

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      "postgres:16-alpine");

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Test
  // ...
}
```
