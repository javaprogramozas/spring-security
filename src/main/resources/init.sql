CREATE TABLE blogs.users (
	id int8 NOT NULL DEFAULT nextval('blogs.users_seq'::regclass),
	display_name varchar(255) NULL,
	status varchar(10) NULL,
	created_at timestamp NULL,
	email varchar(255) NOT NULL,
	CONSTRAINT users_email_uq UNIQUE (email),
	CONSTRAINT users_pk PRIMARY KEY (id)
);

CREATE TABLE blogs.posts (
	id int8 NOT NULL DEFAULT nextval('blogs.posts_seq'::regclass),
	title varchar(255) NOT NULL,
	description text NOT NULL,
	created_at timestamp NOT NULL,
	likes int4 NULL DEFAULT 0,
	slug varchar(255) NOT NULL,
	author_id int8 NULL,
	topic varchar(50) NULL,
	CONSTRAINT likes_not_negative CHECK ((likes >= 0)),
	CONSTRAINT posts_pk PRIMARY KEY (id)
);

ALTER TABLE blogs.posts ADD CONSTRAINT posts_author_fk FOREIGN KEY (author_id) REFERENCES blogs.users(id);