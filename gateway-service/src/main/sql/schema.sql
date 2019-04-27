create database "conference-db"
  with owner postgres;

create table conferences
(
  id uuid not null
    constraint conferences_pk
      primary key,
  acronym varchar(255)
);

alter table conferences owner to postgres;

create table events
(
  id uuid not null
    constraint events_pk
      primary key,
  conference_id uuid
    constraint events_conferences_id_fk
      references conferences,
  seq integer
);

comment on column events.seq is 'sequence number in order events were held.';

alter table events owner to postgres;

create table attendance
(
  event_id uuid not null
    constraint attendance_events_id_fk
      references events,
  author_id uuid not null,
  constraint attendance_pk
    primary key (event_id, author_id)
);

alter table attendance owner to postgres;

