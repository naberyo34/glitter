-- Postgres と同じような環境にするため必要
create schema if not exists public;

create table public."user" (
  id text not null primary key,
  username text not null,
  password text not null,
  email text not null unique,
  profile text
);

create table public.post (
  -- serial でないと h2 で動かないため注意
  id serial primary key,
  user_id text not null,
  foreign key (user_id) references "user" (id),
  content text,
  created_at timestamp not null default current_timestamp
);
