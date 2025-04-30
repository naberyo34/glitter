-- Postgres と同じような環境にするため必要
create schema if not exists public;

create table if not exists public."user" (
  id text not null primary key,
  username text not null,
  email text not null unique,
  profile text,
  icon text,
  sub text not null unique
);

create table if not exists public.post (
  -- serial でないと h2 で動かないため注意
  id serial primary key,
  user_id text not null,
  foreign key (user_id) references "user" (id),
  content text,
  created_at timestamp not null default current_timestamp
);

create table follow (
  follower_id text not null,
  followee_id text not null,
  timestamp timestamp not null default current_timestamp,
  primary key (follower_id, followee_id),
  foreign key (follower_id) references "user" (id),
  foreign key (followee_id) references "user" (id)
);
