--测试用数据
insert into bus_businessSystem(businessSystemCode,businessSystemName,valid)
values('OA','OA系统','Y');
insert into bus_menu(id,menuName,parentId,businessSystemCode)
values(1,'OA系统',999,'OA');
insert into prv_user(id,accessKeyId,accessKeySecret,userName,businessSystemCode,valid)
values(1,'010101','123','乔乔','OA','Y');
commit;