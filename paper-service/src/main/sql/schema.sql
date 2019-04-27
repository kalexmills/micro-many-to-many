create database "paper-db"
  with owner postgres;

create table papers
(
  id uuid not null
    constraint papers_pk
      primary key,
  name varchar(512) not null,
  event_id uuid
);

comment on column papers.event_id is 'foreign key from conference svc';

alter table papers owner to postgres;

create table paper_authors
(
  paper_id uuid not null
    constraint paper_authors_papers_id_fk
      references papers,
  author_id uuid not null,
  constraint paper_authors_pk
    primary key (paper_id, author_id)
);

comment on column paper_authors.paper_id is 'foreign key from papers table (local)';

comment on column paper_authors.author_id is 'foreign key from authors table in author-svc';

alter table paper_authors owner to postgres;

