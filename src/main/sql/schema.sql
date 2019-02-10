DROP TABLE IF EXISTS `bus_accesskey`;
CREATE TABLE `bus_accesskey` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `accesskeyid` varchar(32) DEFAULT NULL COMMENT '用户标识',
  `accesskeysecret` varchar(32) DEFAULT NULL COMMENT '密钥',
  PRIMARY KEY (`id`)
);