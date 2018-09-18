/*
Navicat MySQL Data Transfer

Source Server         : 127.0.0.1_3306
Source Server Version : 50723
Source Host           : 127.0.0.1:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50723
File Encoding         : 65001

Date: 2018-09-18 10:13:46
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for idtest
-- ----------------------------
DROP TABLE IF EXISTS `idtest`;
CREATE TABLE `idtest` (
  `keyId` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`keyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of idtest
-- ----------------------------
INSERT INTO `idtest` VALUES ('754866782698823680', '测试：13');
INSERT INTO `idtest` VALUES ('754867824094142464', '测试：54');
INSERT INTO `idtest` VALUES ('754868202991054848', '测试：10');
INSERT INTO `idtest` VALUES ('754870205250883584', '测试：61');
INSERT INTO `idtest` VALUES ('754873952945684480', '测试：82');
INSERT INTO `idtest` VALUES ('754874011229872128', '测试：42');
INSERT INTO `idtest` VALUES ('754874441229824000', '测试：25');
INSERT INTO `idtest` VALUES ('754900395952627712', '测试：75');
INSERT INTO `idtest` VALUES ('754927979104632832', '1测试：14');
INSERT INTO `idtest` VALUES ('754929129451175936', '1测试：86');
INSERT INTO `idtest` VALUES ('754944872318685184', '1测试：51');
INSERT INTO `idtest` VALUES ('754944873258209280', '2测试：44');

-- ----------------------------
-- Table structure for test
-- ----------------------------
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of test
-- ----------------------------
INSERT INTO `test` VALUES ('1', '测试');
INSERT INTO `test` VALUES ('2', '测试：20');
INSERT INTO `test` VALUES ('3', '测试：85');
INSERT INTO `test` VALUES ('4', '测试：57');
INSERT INTO `test` VALUES ('5', '测试：62');
INSERT INTO `test` VALUES ('6', '测试异步：42');
INSERT INTO `test` VALUES ('7', '测试：90');
