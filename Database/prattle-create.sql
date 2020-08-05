-- Drop the schema if exists
DROP SCHEMA IF EXISTS PRATTLE;

-- Create the empty schema
CREATE SCHEMA PRATTLE;

-- Switch to prattle db
use PRATTLE;

-- Drop the user table if exists
DROP TABLE IF EXISTS `user`;

-- Create the user table
CREATE TABLE `user` (
    user_id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(50) NULL,
    last_name VARCHAR(50) NULL,
    contact_num VARCHAR(50) NULL,
    hashPassword VARCHAR(500) NOT NULL,
    timezone VARCHAR(500) NULL,
    last_log_out_time DATETIME DEFAULT NULL,
    profile_picture_url VARCHAR(500) DEFAULT NULL,
    PRIMARY KEY (user_id)
);

-- Drop the user_ip_mapping table if exists
DROP TABLE IF EXISTS user_ip_mapping;

-- Create the user_ip_mapping table
CREATE TABLE user_ip_mapping (
    user_id INT NOT NULL AUTO_INCREMENT,
    ip_address VARCHAR(15) NOT NULL,
    PRIMARY KEY (user_id),
    CONSTRAINT user_ip_fk FOREIGN KEY (user_id)
        REFERENCES `user` (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- drop table user_feed_mapping if exists
DROP TABLE IF EXISTS user_feed_mapping;

-- create table user_feed_mapping
CREATE TABLE user_feed_mapping (
    feed_id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    feed_text VARCHAR(500),
    feed_time TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT ufm_user_fk FOREIGN KEY (user_id)
        REFERENCES `user` (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (feed_id)
);

-- Drop the message table if exists
DROP TABLE IF EXISTS message;

-- Create the message table
CREATE TABLE message (
    msg_id INT NOT NULL AUTO_INCREMENT,
    source_msg_id INT NOT NULL,
    content VARCHAR(10000),
    from_user_id INT NOT NULL,
    to_user_id INT NOT NULL,
    message_status ENUM('DELIVERED', 'READ', 'DELETED', 'EXPIRED') NOT NULL,
    msg_subject VARCHAR(50),
    hasAttachment BOOLEAN DEFAULT FALSE,
    generation_time DATETIME,
    PRIMARY KEY (msg_id),
    CONSTRAINT source_fk FOREIGN KEY (source_msg_id)
        REFERENCES message (msg_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT to_fk FOREIGN KEY (to_user_id)
        REFERENCES `user` (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Drop the message_encryption table if exists
DROP TABLE IF EXISTS message_encryption;

-- Create the message_encryption table
CREATE TABLE message_encryption (
    msg_id INT NOT NULL,
    enryption_key VARCHAR(500) NOT NULL,
    CONSTRAINT msg_fk FOREIGN KEY (msg_id)
        REFERENCES message (msg_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (msg_id)
);

-- Drop the msg_attachment table if exists
DROP TABLE IF EXISTS msg_attachment_map;

-- Create the msg_attachment_map table
CREATE TABLE msg_attachment_map (
    file_id INT NOT NULL auto_increment,
    message_id INT NOT NULL,
    web_url VARCHAR(500) NOT NULL,
    PRIMARY KEY (file_id),
    CONSTRAINT msg_att_fk FOREIGN KEY (message_id)
        REFERENCES message (msg_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);


-- Drop the message_type_details table if exists
DROP TABLE IF EXISTS message_type_details;

-- Create the message_type_details table
CREATE TABLE message_type_details (
    msg_id INT NOT NULL,
    isBroadcastMsg BOOLEAN DEFAULT FALSE,
    isPrivateMsg BOOLEAN DEFAULT FALSE,
    isGroupMsg BOOLEAN DEFAULT FALSE,
    isForwardedMsg BOOLEAN DEFAULT FALSE,
    isSelfDestruct BOOLEAN DEFAULT FALSE,
    isEncrypyted BOOLEAN DEFAULT FALSE,
    CONSTRAINT msg_td_id_fk FOREIGN KEY (msg_id)
        REFERENCES message (msg_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (msg_id)
);


-- Drop the hashtag table if exists
DROP TABLE IF EXISTS hashtag;

-- Create the hashtag table
CREATE TABLE hashtag (
    hashtag_id INT NOT NULL,
    hashtag VARCHAR(100) NOT NULL,
    search_hits INT NOT NULL DEFAULT 0,
    PRIMARY KEY (hashtag_id)
);


-- Drop the msg_hashtag_mapping table if exists
DROP TABLE IF EXISTS msg_hashtag_mapping;

-- Create the msg_hashtag_mapping table
CREATE TABLE msg_hashtag_mapping (
    msg_id INT NOT NULL,
    hashtag_id INT NOT NULL,
    PRIMARY KEY (msg_id , hashtag_id),
    CONSTRAINT msg_hash_fk FOREIGN KEY (msg_id)
        REFERENCES message (msg_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT hash_hash_fk FOREIGN KEY (hashtag_id)
        REFERENCES hashtag (hashtag_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- drop table groups if exists
DROP TABLE if exists `groups`;

-- create table groups
CREATE TABLE `groups` (
    group_id INT NOT NULL AUTO_INCREMENT,
    group_email VARCHAR(100),
    hashPassword VARCHAR(500),
    `name` VARCHAR(50) NOT NULL,
    `description` VARCHAR(250),
    PRIMARY KEY (group_id)
);

-- drop table group_user_mapping if exists
DROP TABLE IF EXISTS group_user_mapping;

-- create table group_user_mapping
CREATE TABLE group_user_mapping (
    group_id INT NOT NULL,
    user_id INT NOT NULL,
    isModerator BOOLEAN DEFAULT FALSE,
    isFollower BOOLEAN DEFAULT FALSE,
    isMember BOOLEAN DEFAULT TRUE,
    CONSTRAINT gum_group_fk FOREIGN KEY (group_id)
        REFERENCES `groups` (group_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT gum_user_fk FOREIGN KEY (user_id)
        REFERENCES `user` (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (group_id , user_id)
);

-- drop table group_group_mapping if exists
DROP TABLE IF EXISTS group_group_mapping;

-- create table group_user_mapping
CREATE TABLE group_group_mapping (
                                    parent_group_id INT NOT NULL,
                                    sub_group_id INT NOT NULL,
                                    CONSTRAINT ggm_parent_group_fk FOREIGN KEY (parent_group_id)
                                        REFERENCES `groups` (group_id)
                                        ON DELETE CASCADE ON UPDATE CASCADE,
                                    CONSTRAINT ggm_sub_group_fk FOREIGN KEY (sub_group_id)
                                        REFERENCES `groups` (group_id)
                                        ON DELETE CASCADE ON UPDATE CASCADE,
                                    PRIMARY KEY (parent_group_id , sub_group_id)
);

-- drop table filters if exists
DROP TABLE if exists filters;

-- create table filters
CREATE TABLE filters (
    filter_id INT NOT NULL AUTO_INCREMENT,
    `filter` VARCHAR(100) NOT NULL,
    PRIMARY KEY (filter_id)
);


-- drop table user_filter_map if exists
DROP TABLE IF EXISTS user_filter_map;

-- create table user_filter_map
CREATE TABLE user_filter_map (
    user_id INT NOT NULL,
    filter_id INT NOT NULL,
    PRIMARY KEY (user_id , filter_id),
    CONSTRAINT ufm_fk FOREIGN KEY (user_id)
        REFERENCES `user` (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);


-- drop table government if exists
DROP TABLE IF EXISTS government;

-- create table government
CREATE TABLE government (
    gov_id INT NOT NULL auto_increment,
    gov_username VARCHAR(50) NOT NULL UNIQUE,
    gov_password VARCHAR(500) NOT NULL,
    PRIMARY KEY (gov_id)
);


-- drop table subpoena if exists
DROP TABLE IF EXISTS subpoena;

-- create table subpoena
CREATE TABLE subpoena (
    subpoena_id INT NOT NULL AUTO_INCREMENT,
    gov_user_id INT NOT NULL,
    watched_user_id INT NOT NULL,
    expire_timestamp DATETIME NOT NULL,
    PRIMARY KEY (subpoena_id),
    CONSTRAINT gov_subpoena_fk FOREIGN KEY (gov_user_id)
        REFERENCES government (gov_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT user_subpoena_fk FOREIGN KEY (watched_user_id)
        REFERENCES `user` (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);


-- drop table user_follows
DROP TABLE IF EXISTS user_follows;

-- create table user_follows
CREATE TABLE user_follows (
    followee_id INT NOT NULL,
    follower_id INT NOT NULL,
    PRIMARY KEY (followee_id , follower_id),
    CONSTRAINT user_followee_fk FOREIGN KEY (followee_id)
        REFERENCES `user` (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT user_follower_fk FOREIGN KEY (follower_id)
        REFERENCES `user` (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);
