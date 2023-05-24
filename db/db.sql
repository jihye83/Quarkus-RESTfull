--MYSQL
CREATE DATABASE quarkus-social;

CREATE TABLE users (
	id SERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	age INTEGER NOT NULL
);

CREATE TABLE posts (
    id SERIAL PRIMARY KEY,
    post_text varchar(150) not null,
    dateTime timestamp,
    user_id bigint not null references USERS(id)
);

CREATE TABLE fallowers {
    id SERIAL PRIMARY KEY,
    user_id bigint not null references USERS(id),
    fallowers_id bigint not null references USERS(id)
};

--POSTGRES
--CREATE DATABASE quarkus-social;
--
--CREATE TABLE USERS (
--	id bigserial not null primary key,
--	name varchar(100) not null,
--	age integer not null
--);
--
--CREATE TABLE POSTS (
--    id bigserial not null primary key,
--    post_text varchar(150) not null,
--    dateTime timestamp not null,
--    user_id bigint not null references USERS(id)
--);
--
--CREATE TABLE FOLLOWERS (
--	id bigserial not null primary key,
--	user_id bigint not null references USERS(id),
--	follower_id bigint not null references USERS(id)
--);