create database "author-db"
  with owner postgres;

create table authors
(
  id uuid not null
    constraint authors_pk
      primary key,
  full_name varchar(1024)
);

alter table authors owner to postgres;

create table attendance
(
  event_id uuid not null,
  author_id uuid not null
    constraint attendance_authors_id_fk
      references authors,
  constraint attendance_pk
    primary key (event_id, author_id)
);

alter table attendance owner to postgres;

