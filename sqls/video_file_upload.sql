
/*Create video file upload table*/

CREATE TABLE IF NOT EXISTS video_file_upload(
video_id int(11) primary key auto_increment,
event_id varchar(100) not null,
file_name varchar(100) not null,
file_location varchar(100) not null,
upload_date timestamp default now()
);


