CREATE TABLE blogs.users (
	id int8 NOT NULL DEFAULT nextval('blogs.users_seq'::regclass),
	display_name varchar(255) NULL,
	status varchar(10) NULL,
	created_at timestamp NULL,
	email varchar(255) NOT NULL,
	encoded_password varchar(255) NULL,
	CONSTRAINT users_email_uq UNIQUE (email),
	CONSTRAINT users_pk PRIMARY KEY (id)
);

CREATE TABLE blogs.user_roles (
	user_id int8 NOT NULL,
	role_name varchar(10) NOT NULL
);

ALTER TABLE blogs.user_roles ADD CONSTRAINT user_roles_fk FOREIGN KEY (user_id) REFERENCES blogs.users(id);

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

-- Spring ACL tables
-- (source: https://docs.spring.io/spring-security/reference/servlet/appendix/database-schema.html#_postgresql)

-- ALTER USER blogadmin SET search_path TO blogs;

create table blogs.acl_sid(
	id bigserial not null primary key,
	principal boolean not null,
	sid varchar(100) not null,
	constraint unique_uk_1 unique(sid,principal)
);

create table blogs.acl_class(
	id bigserial not null primary key,
	class varchar(100) not null,
	constraint unique_uk_2 unique(class)
);

create table blogs.acl_object_identity(
	id bigserial primary key,
	object_id_class bigint not null,
	object_id_identity varchar(36) not null,
	parent_object bigint,
	owner_sid bigint,
	entries_inheriting boolean not null,
	constraint unique_uk_3 unique(object_id_class,object_id_identity),
	constraint foreign_fk_1 foreign key(parent_object)references acl_object_identity(id),
	constraint foreign_fk_2 foreign key(object_id_class)references acl_class(id),
	constraint foreign_fk_3 foreign key(owner_sid)references acl_sid(id)
);

create table blogs.acl_entry(
	id bigserial primary key,
	acl_object_identity bigint not null,
	ace_order int not null,
	sid bigint not null,
	mask integer not null,
	granting boolean not null,
	audit_success boolean not null,
	audit_failure boolean not null,
	constraint unique_uk_4 unique(acl_object_identity,ace_order),
	constraint foreign_fk_4 foreign key(acl_object_identity) references acl_object_identity(id),
	constraint foreign_fk_5 foreign key(sid) references acl_sid(id)
);