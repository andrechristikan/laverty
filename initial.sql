
-- CREATE FUNCTION FOR DEFAULT TIMESTAMPTZ
create function now_utc() returns timestamp as $$ select now() at time zone 'utc'; $$ language sql;

-- CREATE EXTENSION FOR DEFAULT UUID
CREATE EXTENSION "uuid-ossp";


-- CREATE TABLE
CREATE TABLE roles(
	id VARCHAR(20) primary key,
   	name VARCHAR (255) UNIQUE NOT null,
   	created_at TIMESTAMPTZ NOT null default now_utc(),
   	updated_at TIMESTAMPTZ default NULL
);

CREATE TABLE users(
	id UUID primary key default uuid_generate_v4(),
	role_id VARCHAR (20) not null,
   	username VARCHAR (50) UNIQUE NOT NULL,
   	password VARCHAR (50) NOT NULL,
   	email VARCHAR (355) UNIQUE NOT NULL,
   	created_at TIMESTAMPTZ NOT null default now_utc(),
   	updated_at TIMESTAMPTZ default NULL,
   	last_login TIMESTAMPTZ default NULL,
   	CONSTRAINT users_role FOREIGN KEY (role_id) REFERENCES roles (id) on delete cascade on update cascade
);

CREATE TABLE settings(
	id UUID primary key default uuid_generate_v4(),
   	setting_name VARCHAR (100) UNIQUE NOT null,
   	setting_value VARCHAR (100) NOT NULL
);



-- INSERT VALUE INTO TABLE
INSERT INTO roles (id, name) values
('user','User'),
('admin','Admin');

INSERT INTO users (id, role_id, username, password, email) values
('9a057751-3624-4216-a2ce-66b8fb64b2e6', 'user', 'user', '123', 'user@mail.com'),
('370223fa-138c-4f40-ac8b-f483b2b129f6', 'admin', 'admin', '123', 'admin@mail.com');

INSERT INTO settings (setting_name, setting_value) values
('login_with','username');
