create table if not exists  file_meta(
  name varchar(50) not null,
  path varchar(1000)  not null,
  size bigint  not null,
  last_modified timestamp  not null,
  pinyin varchar (50),
  pinyin_first varchar (50),
  is_directory boolean not null
);