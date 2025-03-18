insert into
  public."user" (id, username, password, email, profile)
values
  (
    'test_user',
    'テストユーザー',
    -- 「password」をハッシュ化したもの
    '$2a$12$Z3MQA08C1d8S89U7nA0/1eMMxRw061BKTZHl.OlGzZjFMLQs6FC3y',
    'test@example.com',
    'テスト用のアカウントです。'
  );

insert into
  public."user" (id, username, password, email, profile)
values
  (
    'test_user_2',
    'テストユーザー2',
    '$2a$12$Z3MQA08C1d8S89U7nA0/1eMMxRw061BKTZHl.OlGzZjFMLQs6FC3y',
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
