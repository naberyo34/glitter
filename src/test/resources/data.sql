insert into
  public."user" (id, username, email, profile, icon, sub)
values
  (
    'test_user',
    'テストユーザー',
    'test@example.com',
    'テスト用のアカウントです。',
    '',
    'test_user_sub'
  );

insert into
  public."user" (id, username, email, profile, icon, sub)
values
  (
    'test_user_2',
    'テストユーザー2',
    'test2@example.com',
    'テスト用のアカウントです。2',
    '',
    'test_user_2_sub'
  );

insert into
  public.post (user_id, content)
values
  ('test_user', 'テスト投稿');

insert into
  public.post (user_id, content)
values
  ('test_user', 'テスト投稿2');
