create schema if not exists users;
create schema if not exists umm;
create schema if not exists interactive_material;

create table if not exists users."user"
(
    id        bigint       not null
        constraint user_pkey
            primary key,
    username  varchar(255) not null,
    password  varchar(255) not null,
    authority varchar(255) not null,
    enabled   boolean      not null
);
create table if not exists users."speciality"
(
    id   bigint       not null
        constraint speciality_pkey
            primary key,
    name varchar(255) not null
);

create table if not exists umm."lesson_type"
(
    id   bigint       not null
        constraint lesson_type_pkey
            primary key,
    name varchar(255) not null,
    code varchar(255)
);

create sequence if not exists hibernate_sequence start with 1000000;

create schema if not exists syllabus;
create table if not exists syllabus.cycle
(
    name varchar(255),
    id   bigint not null
        constraint cycle_pkey primary key
);
