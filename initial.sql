-- DROP ALL TABLE
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS settings;
DROP TABLE IF EXISTS settings_dynamic_name;

-- CREATE FUNCTION FOR DEFAULT TIMESTAMPTZ
CREATE IF NOT EXISTS function now_utc() returns TIMESTAMP AS $$ SELECT now() AT TIME ZONE 'utc'; $$ LANGUAGE SQL;

-- CREATE EXTENSION FOR DEFAULT UUID
CREATE IF NOT EXISTS EXTENSION "uuid-ossp";


-- CREATE TABLE

CREATE TABLE roles(
	id VARCHAR(20) PRIMARY KEY,
   	name VARCHAR (255) UNIQUE NOT NULL,
   	created_at TIMESTAMPTZ NOT NULL DEFAULT now_utc(),
   	updated_at TIMESTAMPTZ DEFAULT NULL
);

CREATE TABLE users(
	id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
	role_id VARCHAR (20) NOT NULL,
   	username VARCHAR (50) UNIQUE NOT NULL,
   	password_hash VARCHAR (255) NOT NULL,
   	salt VARCHAR (50) NOT NULL,
   	email VARCHAR (255) UNIQUE NOT NULL,
   	created_at TIMESTAMPTZ NOT NULL default now_utc(),
   	updated_at TIMESTAMPTZ DEFAULT NULL,
   	last_login TIMESTAMPTZ DEFAULT NULL,
   	CONSTRAINT users_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE ON UPDATE RESTRICT
);

CREATE TABLE settings(
	id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
   	setting_name VARCHAR (100) UNIQUE NOT NULL,
   	setting_value VARCHAR (100) NOT NULL
);

CREATE TABLE settings_dynamic_name(
	id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
   	table_name VARCHAR (100) NOT NULL,
   	column_name VARCHAR (100) NOT NULL,
   	setting_name VARCHAR (100) NOT NULL,
   	setting_value VARCHAR (100) NOT NULL
);


-- INSERT VALUE INTO TABLE
INSERT INTO roles (id, name) VALUES
('user','User'),
('admin','Admin');

INSERT INTO users (id, role_id, username, password_hash, salt, email) VALUES
('9a057751-3624-4216-a2ce-66b8fb64b2e6', 'user', 'user', '8734692d0dc881870722ed85382b3a34e88bd09dadba41d3f6e5b3b10ee1f51a5ae34fb20393a11a52cdf3d977ad70c55c503a3852cf62f87c01b783618da614', '5b424038663435396135', 'user@mail.com'),
('370223fa-138c-4f40-ac8b-f483b2b129f6', 'admin', 'admin', '476307de62dca311031769ac7532ab969d4190f6d4f903bedaf4fb408466d5d2f445b2ef1590674f891641e927652bb276989f31902abfbe1462938d30435409', '5b42403663366162663138', 'admin@mail.com');

INSERT INTO settings (setting_name, setting_value) VALUES
('login_with','username');


INSERT INTO settings_dynamic_name (table_name, column_name, setting_name, setting_value) VALUES
('users','is_active', '0', 'Active'),
('users','is_active', '1', 'Inactive');
