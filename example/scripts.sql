
CREATE TABLE `country` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
insert into country(id, version, name)
			values(1, 0, 'Australia');

insert into country(id, version, name)
			values(2, 0, 'Bangladesh');

insert into country(id, version, name)
			values(3, 0, 'Bahrain');



insert into country(id, version, name)
			values(8, 0, 'Fiji');

insert into country(id, version, name)
			values(9, 0, 'United Kingdom');

insert into country(id, version, name)
			values(10, 0, 'Hong Kong');