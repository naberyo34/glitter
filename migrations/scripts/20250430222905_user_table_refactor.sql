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
drop table post;

drop table follow;

drop table "user";

create table "user" (
  uuid text primary key default gen_random_uuid (),
  user_id text not null,
  domain text not null,
  actor_url text not null unique,
  username text not null,
  profile text,
  icon text,
  sub text,
  constraint unique_user_id_domain unique (user_id, domain)
);

create table post (
  uuid text primary key default gen_random_uuid (),
  user_id text not null,
  domain text not null,
  content text,
  created_at timestamp not null default current_timestamp,
  constraint fk_post_user foreign key (user_id, domain) references "user" (user_id, domain) on delete cascade
);

create table follow (
  follower_id text not null,
  follower_domain text not null,
  followee_id text not null,
  followee_domain text not null,
  created_at timestamp not null default current_timestamp,
  primary key (follower_id, follower_domain, followee_id, followee_domain),
  constraint fk_follower_user foreign key (follower_id, follower_domain) references "user" (user_id, domain) on delete cascade,
  constraint fk_followee_user foreign key (followee_id, followee_domain) references "user" (user_id, domain) on delete cascade
);

-- //@UNDO
-- SQL to undo the change goes here.
drop table post;

drop table follow;

drop table "user";
