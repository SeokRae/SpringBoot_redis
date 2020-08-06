use user_db;
drop table if exists account;
create table account(
  id         bigint not null auto_increment,
  created_at timestamp,
  updated_at timestamp,
  role       varchar(255),
  user_name  varchar(255),
  user_pw    varchar(255),
primary key(id)
);

drop table if exists history_access_token;
create table history_access_token(
  id           bigint not null auto_increment,
  created_at   timestamp,
  updated_at   timestamp,
  access_token varchar(255),
  signature    varchar(255),
  user_name    varchar(255),
primary key(id)
);

drop table if exists refresh_token;
create table refresh_token(
  id            bigint not null auto_increment,
  created_at    timestamp,
  updated_at    timestamp,
  access_token  varchar(255),
  refresh_token varchar(255),
  user_name     varchar(255),
primary key(id)
);
