/*Create Events table*/

CREATE TABLE events_list(
id int(11) primary key auto_increment,
event_name varchar(100) not null,
latitude varchar(100) not null,
longitude varchar(100) not null,
description text,
created_at timestamp default now(),
updated_at timestamp 
);

/* Inserting dummy data */
INSERT INTO events_list(event_name, latitude, longitude, description ) VALUES('Event1', '43.5432', '87.5371','Event 1 happening');
INSERT INTO events_list(event_name, latitude, longitude, description ) VALUES('Event2', '45.5532', '57.8172','Event 2 happening');
INSERT INTO events_list(event_name, latitude, longitude, description ) VALUES('Event3', '35.5462', '47.4375','Event 3 happening');
INSERT INTO events_list(event_name, latitude, longitude, description ) VALUES('Event4', '48.1432', '77.1372','Event 4 happening');
INSERT INTO events_list(event_name, latitude, longitude, description ) VALUES('Event5', '57.7432', '17.3376','Event 5 happening');
INSERT INTO events_list(event_name, latitude, longitude, description ) VALUES('Event6', '41.1432', '77.8675','Event 6 happening');
INSERT INTO events_list(event_name, latitude, longitude, description ) VALUES('Event7', '49.6987', '71.3258','Event 7 happening');
