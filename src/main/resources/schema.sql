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

create table if not exists umm.discipline
(
    name varchar(255),
    id   bigint not null
        constraint discipline_pkey
            primary key
);

create table if not exists syllabus.cycle
(
    name varchar(255),
    id   bigint not null
        constraint cycle_pkey
            primary key
);


create table if not exists syllabus.department
(
    id   bigint not null
        constraint department_pkey
            primary key,
    name varchar(255)
);


create table if not exists syllabus.syllabus_category
(
    id   bigint not null
        constraint syllabus_category_pkey
            primary key,
    name varchar(255)
);


create table if not exists syllabus.syllabus
(
    id                              bigint  not null
        constraint syllabus_pkey
            primary key,
    base                            boolean not null,
    conferences                     integer not null,
    course_work_control             numeric(19, 2),
    course_works                    integer not null,
    credit                          integer not null,
    exam_control                    numeric(19, 2),
    fertile_units                   integer not null,
    graded_credit_control           numeric(19, 2),
    group_exercises                 integer not null,
    group_lessons                   integer not null,
    index                           varchar(255),
    laboratory_works                integer not null,
    lectures                        integer not null,
    pass_without_assessment_control numeric(19, 2),
    practical_lessons               integer not null,
    practices                       integer not null,
    seminars                        integer not null,
    special_lessons                 integer not null,
    year1_cycle1_intensity          numeric(19, 2),
    year1_cycle2_intensity          numeric(19, 2),
    year1_cycle1_self_study_hours   integer,
    year1_cycle2_self_study_hours   integer,
    year1_cycle1_training_hours     integer,
    year1_cycle2_training_hours     integer,
    year2_cycle1_intensity          numeric(19, 2),
    year2_cycle2_intensity          numeric(19, 2),
    year2_cycle1_self_study_hours   integer,
    year2_cycle2_self_study_hours   integer,
    year2_cycle1_training_hours     integer,
    year2_cycle2_training_hours     integer,
    year3_cycle1_intensity          numeric(19, 2),
    year3_cycle2_intensity          numeric(19, 2),
    year3_cycle1_self_study_hours   integer,
    year3_cycle2_self_study_hours   integer,
    year3_cycle1_training_hours     integer,
    year3_cycle2_training_hours     integer,
    year4_cycle1_intensity          numeric(19, 2),
    year4_cycle2_intensity          numeric(19, 2),
    year4_cycle1_self_study_hours   integer,
    year4_cycle2_self_study_hours   integer,
    year4_cycle1_training_hours     integer,
    year4_cycle2_training_hours     integer,
    year5_cycle1_intensity          numeric(19, 2),
    year5_cycle2_intensity          numeric(19, 2),
    year5_cycle1_self_study_hours   integer,
    year5_cycle2_self_study_hours   integer,
    year5_cycle1_training_hours     integer,
    year5_cycle2_training_hours     integer,
    tests                           integer not null,
    undefining_parameter            integer not null,
    syllabus_category_id            bigint
        constraint fk33citio791a5rv4uk2mv0gowk
            references syllabus.syllabus_category,
    discipline_id                   bigint
        constraint fkbnif81l15kl9eyho8cgfmiqne
            references umm.discipline,
    speciality_id                   bigint
        constraint fkcv049uxyapjpaaeimar1kpsh8
            references users.speciality
);



create schema if not exists simulator;
