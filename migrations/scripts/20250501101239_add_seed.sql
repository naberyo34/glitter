--
--    Copyright 2010-2023 the original author or authors.
--
--    Licensed under the Apache License, Version 2.0 (the "License");
--    you may not use this file except in compliance with the License.
--    You may obtain a copy of the License at
--
--       https://www.apache.org/licenses/LICENSE-2.0
--
--    Unless required by applicable law or agreed to in writing, software
--    distributed under the License is distributed on an "AS IS" BASIS,
--    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--    See the License for the specific language governing permissions and
--    limitations under the License.
--
-- // The MyBatis parent POM.
-- Migration SQL that makes the change goes here.
insert into
  "user" (user_id, domain, actor_url, username, profile)
values
  (
    'test_user',
    'example.com',
    'https://example.com/user/test_user',
    'テストユーザー',
    'テスト用のアカウントです。'
  );

insert into
  "user" (user_id, domain, actor_url, username, profile)
values
  (
    'test_user_2',
    'example.com',
    'https://example.com/user/test_user_2',
    'テストユーザー2',
    'テスト用のアカウントです。2'
  );

insert into
  post (user_id, domain, content)
values
  ('test_user', 'example.com', 'テスト投稿');

insert into
  post (user_id, domain, content)
values
  ('test_user', 'example.com', 'テスト投稿2');

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
  );

-- //@UNDO
-- SQL to undo the change goes here.
delete from "user"
where
  user_id = 'test_user';

delete from "user"
where
  user_id = 'test_user_2';

delete from post
where
  post_id = 1;

delete from post
where
  post_id = 2;

delete from follow
where
  follower_id = 'test_user'
  and follower_domain = 'example.com'
  and followee_id = 'test_user_2'
  and followee_domain = 'example.com';
