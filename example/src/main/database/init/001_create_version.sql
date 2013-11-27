DROP TABLE IF EXISTS version_info;

create table version_info(
  id bigint(10) auto_increment primary key,
	version bigint(10) not null,
	filename varchar(100),
	app varchar(100),
	starttime timestamp,
	endtime timestamp,
	constraint v_uq unique (id, app, starttime)
);



