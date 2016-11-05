drop table users;
--;;
alter table chart alter column id drop default;
--;;
alter sequence chart_id_seq owned by none;
--;;
drop sequence chart_id_seq;
--;;
drop table chart;
--;;

create table people
(id serial primary key,
user_name varchar(30),
email varchar(30),
pass varchar(300),
unique (user_name));
--;;

create table households
(id serial primary key,
house_name varchar(30),
unique (house_name));
--;;

create table living_situations
(id serial primary key,
person_id integer references people (id),
household_id integer references households (id));
--;;

create table chores
(id serial primary key,
chore_name varchar(30),
description text,
household_id integer references households (id));
--;;

create table chore_chart
(id serial primary key,
person_id integer references people (id),
chore_id integer references chores (id),
moment timestamp);



