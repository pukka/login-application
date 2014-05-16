# --- !Ups
create table users (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(128),
  password varchar(128),
  PRIMARY KEY (id)
);

# --- !Downs
drop table users; 
