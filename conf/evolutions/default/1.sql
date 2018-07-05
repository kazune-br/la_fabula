# --- !Ups
create table users (
      user_id varchar(255),
      login_info_provider_id varchar(255),
      login_info_provider_key varchar(255),
      first_name VARCHAR(100) NOT NULL,
      last_name VARCHAR(100) NOT NULL,
      full_name VARCHAR(100) NOT NULL,
      email VARCHAR(100) NOT NULL,
      avatar_url VARCHAR(255) NOT NULL,
      activated BIT NOT NULL default 0,
      PRIMARY KEY(user_id, login_info_provider_id, login_info_provider_key)
);
