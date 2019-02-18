DROP TABLE IF EXISTS bus_menu;
create table bus_menu(
	id bigint not null primary key  auto_increment,
	menuName varchar(50),
	parentId bigint,
	businessSystemCode varchar(20)
);

DROP TABLE IF EXISTS prv_user;
create table prv_user(
	id bigint not null primary key  auto_increment,
	accessKeyId varchar(20),
	accessKeySecret varchar(32),
	userName varchar(50),
	businessSystemCode varchar(20),
	valid varchar(1)
);

DROP TABLE IF EXISTS bus_file;
create table bus_file(
	id varchar(32) not null primary key,
	menuId bigint,
	fileCode varchar(50),
	fileName varchar(255),
	fileSuffix varchar(10),
	fileSize bigint,
	createTime datetime,
	lastModifyTime datetime,
	lastModifyId bigint,
	lastModifyName varchar(50),
	valid varchar(1)
);

DROP TABLE IF EXISTS bus_fileOperateRecord;
create table bus_fileOperateRecord(
	id bigint not null primary key  auto_increment,
	fileId varchar(50),
	fileName varchar(255),
	operaterType varchar(2),
	operaterTime datetime,
	operaterId bigint,
	operaterName varchar(50)
);

DROP TABLE IF EXISTS bus_businessSystem;
create table bus_businessSystem(
	businessSystemCode varchar(20) not null primary key,
	businessSystemName varchar(50),
	valid varchar(1)
);