insert into
  public."user" (id, username, password, email, profile)
values
  (
    'test_user',
    'テストユーザー',
    '$2a$12$LWXJeqQzJBzqZWU8PpF31usZPPUxD1i8Mp0bQcov0ygENoLZ2oYlG',
    'test@example.com',
    'テスト用のアカウントです。'
  );

insert into
  public."user" (id, username, password, email, profile)
values
  (
    'test_user_2',
    'テストユーザー2',
    '$2a$12$LWXJeqQzJBzqZWU8PpF31usZPPUxD1i8Mp0bQcov0ygENoLZ2oYlG',
    'test2@example.com',
    'テスト用のアカウントです。2'
  );

insert into
  public.post (user_id, content)
values
  ('test_user', 'テスト投稿');

insert into
  public.post (user_id, content)
values
  ('test_user', 'テスト投稿2');
