/*
Navicat MySQL Data Transfer

Source Server         : 127.0.0.1_3306
Source Server Version : 50723
Source Host           : 127.0.0.1:3306
Source Database       : test2

Target Server Type    : MYSQL
Target Server Version : 50723
File Encoding         : 65001

Date: 2018-09-20 11:43:02
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for db2test
-- ----------------------------
DROP TABLE IF EXISTS `db2test`;
CREATE TABLE `db2test` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `sex` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of db2test
-- ----------------------------
INSERT INTO `db2test` VALUES ('1', '2', '1');

-- ----------------------------
-- Table structure for testcreateuser
-- ----------------------------
DROP TABLE IF EXISTS `testcreateuser`;
CREATE TABLE `testcreateuser` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `createUser` int(11) DEFAULT NULL,
  `createTime` int(11) DEFAULT NULL,
  `userCreateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of testcreateuser
-- ----------------------------
INSERT INTO `testcreateuser` VALUES ('1', '测试：54', '0', null);
INSERT INTO `testcreateuser` VALUES ('2', '测试：49', '0', null);
INSERT INTO `testcreateuser` VALUES ('3', '测试：35', '0', null);
INSERT INTO `testcreateuser` VALUES ('4', '测试：15', '1', null);
INSERT INTO `testcreateuser` VALUES ('5', '测试：62', '1', '1537411925');
INSERT INTO `testcreateuser` VALUES ('6', '测试：16', '1', '1537413805');
INSERT INTO `testcreateuser` VALUES ('7', '测试：90', '1', '1537413898');
INSERT INTO `testcreateuser` VALUES ('8', '测试：37', '1', '1537413926');
INSERT INTO `testcreateuser` VALUES ('9', '测试：12', '1', '1537413998');
INSERT INTO `testcreateuser` VALUES ('10', '测试：59', '1', '1537414040');
INSERT INTO `testcreateuser` VALUES ('11', '测试：84', '1', '1537414352');
INSERT INTO `testcreateuser` VALUES ('12', '测试：68', '1', '1537414655');
INSERT INTO `testcreateuser` VALUES ('13', '测试：77', '1', '1537414843');

-- ----------------------------
-- Table structure for testremove
-- ----------------------------
DROP TABLE IF EXISTS `testremove`;
CREATE TABLE `testremove` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `isDelete` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of testremove
-- ----------------------------

-- ----------------------------
-- Table structure for testupdatetime
-- ----------------------------
DROP TABLE IF EXISTS `testupdatetime`;
CREATE TABLE `testupdatetime` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `modifyTime` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of testupdatetime
-- ----------------------------
INSERT INTO `testupdatetime` VALUES ('1', '修改后', '1537414856');
INSERT INTO `testupdatetime` VALUES ('2', '测试修改', '0');
INSERT INTO `testupdatetime` VALUES ('3', '测试修改', '0');

-- ----------------------------
-- Table structure for testupdateuser
-- ----------------------------
DROP TABLE IF EXISTS `testupdateuser`;
CREATE TABLE `testupdateuser` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `modifyTime` int(11) DEFAULT NULL,
  `lastModifyUser` int(11) DEFAULT NULL,
  `lastModifyTime` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of testupdateuser
-- ----------------------------
INSERT INTO `testupdateuser` VALUES ('1', '修改后', '1537414857', '1', '1537414857');
INSERT INTO `testupdateuser` VALUES ('2', '测试修改', '0', null, null);
INSERT INTO `testupdateuser` VALUES ('3', '测试修改', '0', null, null);
