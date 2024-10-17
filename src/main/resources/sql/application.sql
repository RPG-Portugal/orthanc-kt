create table main.application
(
    id    integer null on conflict rollback
        constraint application_pk
            primary key,
    token TEXT    not null
);

