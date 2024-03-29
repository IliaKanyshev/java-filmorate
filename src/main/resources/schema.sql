create table if not exists users
(
    user_id  integer generated by default as identity primary key,
    email    varchar not null,
    login    varchar not null,
    name     varchar not null,
    birthday date
);
create table if not exists friends
(
    user_id   integer not null,
    friend_id integer not null,
    foreign key (user_id) references users (user_id) on delete cascade,
    foreign key (friend_id) references users (user_id) on delete cascade
);
create table if not exists genre_type
(
    genre_id integer generated by default as identity primary key,
    name     varchar not null unique
);
create table if not exists mpa
(
    mpa_rating_id integer generated by default as identity primary key,
    name          varchar not null unique
);
create table if not exists films
(
    film_id       integer generated by default as identity primary key,
    name          varchar not null,
    description   varchar,
    release_date  date,
    duration      integer not null,
    mpa_rating_id integer not null,
    foreign key (mpa_rating_id) references mpa (mpa_rating_id)
);
create table if not exists genre
(
    film_id  integer not null,
    genre_id integer not null,
    foreign key (film_id) references films (film_id) on delete cascade,
    foreign key (genre_id) references genre_type (genre_id) on delete cascade
);
create table if not exists likes
(
    film_id integer not null,
    user_id integer not null,
    foreign key (film_id) references films (film_id) on delete cascade,
    foreign key (user_id) references users (user_id) on delete cascade
);