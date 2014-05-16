# --- !Ups

create table tasks (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  user_id bigint(20),
  date varchar(128),
  time varchar(128),
  work varchar(512),
  PRIMARY KEY (id)
);

# --- !Downs
drop table tasks;
