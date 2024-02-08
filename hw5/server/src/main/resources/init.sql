create table if not exists user
(
    username varchar(50) not null
        primary key,
    password varchar(50) null,
    email    varchar(50) null,
    constraint table_name_pk_2
        unique (email)
);

create table if not exists room
(
    name   varchar(50) not null,
    member varchar(50) not null,
    primary key (name, member),
    constraint table_name_user_username_fk
        foreign key (member) references user (username)
            on update cascade on delete cascade
);

create table if not exists save_messages
(
    username  text                          not null,
    `from`    text                          not null,
    `to`      text                          not null,
    content   text collate utf32_unicode_ci not null,
    isRoom    tinyint(1)                    not null,
    timestamp mediumtext                    not null
);
