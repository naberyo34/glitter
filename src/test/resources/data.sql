insert into
  "user" (
    user_id,
    domain,
    actor_url,
    username,
    profile,
    sub
  )
values
  (
    'test_user',
    'example.com',
    'https://example.com/user/test_user',
    'テストユーザー',
    'テスト用のアカウントです。',
    'test_user_sub'
  ),
  (
    'test_user_2',
    'example.com',
    'https://example.com/user/test_user_2',
    'テストユーザー2',
    'テスト用のアカウントです。2',
    'test_user_2_sub'
  ),
  (
    'test_user_3',
    'example.com',
    'https://example.com/user/test_user_3',
    'テストユーザー3',
    'テスト用のアカウントです。3',
    'test_user_3_sub'
  );

-- 本来自動割当だが、 テスト向けに UUID と createdAt を固定する
insert into
  post (uuid, user_id, domain, content, created_at)
values
  (
    'uuid_1',
    'test_user',
    'example.com',
    'テスト投稿',
    '2023-10-01 00:00:00'
  ),
  (
    'uuid_2',
    'test_user',
    'example.com',
    'テスト投稿2',
    '2023-10-02 00:00:00'
  );

insert into
  follow (
    follower_id,
    follower_domain,
    followee_id,
    followee_domain
  )
values
  (
    'test_user',
    'example.com',
    'test_user_2',
    'example.com'
  ),
  (
    'test_user',
    'example.com',
    'test_user_3',
    'example.com'
  ),
  (
    'test_user_2',
    'example.com',
    'test_user',
    'example.com'
  ),
  (
    'test_user_3',
    'example.com',
    'test_user',
    'example.com'
  );
