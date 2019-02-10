# filemanager
 文件服务

cors配置和用户鉴权的相关内容已经按照接口文档中的说明完成；
在需要鉴权的接口对应方法上加NeedCheckSignature注解即可；
AccessKey从数据库中查询，目前已经引入数据源druid；
