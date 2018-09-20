/*
Navicat MySQL Data Transfer

Source Server         : 127.0.0.1_3306
Source Server Version : 50723
Source Host           : 127.0.0.1:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50723
File Encoding         : 65001

Date: 2018-09-20 11:42:56
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
INSERT INTO `idtest` VALUES ('755257177509773312', '1测试：54');
INSERT INTO `idtest` VALUES ('755257178449297408', '2测试：44');
INSERT INTO `idtest` VALUES ('755265311674404864', '1测试：13');
INSERT INTO `idtest` VALUES ('755265312613928960', '2测试：96');
INSERT INTO `idtest` VALUES ('755435402110476288', '1测试：74');
INSERT INTO `idtest` VALUES ('755435402110476289', '\'2测试：61');
INSERT INTO `idtest` VALUES ('756274242912206848', '测试：52');
INSERT INTO `idtest` VALUES ('756274243348414464', '1测试：17');
INSERT INTO `idtest` VALUES ('756274243348414465', '\'2测试：25');
INSERT INTO `idtest` VALUES ('756275495968907264', '测试：27');
INSERT INTO `idtest` VALUES ('756275496371560448', '1测试：86');
INSERT INTO `idtest` VALUES ('756275496371560449', '\'2测试：97');
INSERT INTO `idtest` VALUES ('756305674757689344', '测试：82');
INSERT INTO `idtest` VALUES ('756305675831431168', '1测试：24');
INSERT INTO `idtest` VALUES ('756305675831431169', '\'2测试：33');
INSERT INTO `idtest` VALUES ('760703794148835328', '1测试：99');
INSERT INTO `idtest` VALUES ('760703795088359424', '\'\'2测试：71');
INSERT INTO `idtest` VALUES ('760940958551552000', '测试：26');
INSERT INTO `idtest` VALUES ('760940959658848256', '1测试：50');
INSERT INTO `idtest` VALUES ('760940959658848257', '\'\'2测试：50');
INSERT INTO `idtest` VALUES ('760944077738729472', '测试：89');
INSERT INTO `idtest` VALUES ('760944078778916864', '1测试：16');
INSERT INTO `idtest` VALUES ('760944078778916865', '\'\'2测试：64');
INSERT INTO `idtest` VALUES ('760945024107458560', '测试：78');
INSERT INTO `idtest` VALUES ('760945025281863680', '1测试：67');
INSERT INTO `idtest` VALUES ('760945025281863681', '\'\'2测试：56');
INSERT INTO `idtest` VALUES ('760947421001801728', '测试：65');
INSERT INTO `idtest` VALUES ('760947421907771392', '1测试：51');
INSERT INTO `idtest` VALUES ('760947421907771393', '\'\'2测试：50');
INSERT INTO `idtest` VALUES ('760948839078227968', '测试：80');
INSERT INTO `idtest` VALUES ('760948840722395136', '1测试：38');
INSERT INTO `idtest` VALUES ('760948840722395137', '\'\'2测试：67');
INSERT INTO `idtest` VALUES ('760959297425018880', '测试：24');
INSERT INTO `idtest` VALUES ('760959298532315136', '1测试：20');
INSERT INTO `idtest` VALUES ('760959298532315137', '\'\'2测试：61');
INSERT INTO `idtest` VALUES ('760969490287620096', '测试：49');
INSERT INTO `idtest` VALUES ('760969491629797376', '1测试：48');
INSERT INTO `idtest` VALUES ('760969491629797377', '\'\'2测试：10');
INSERT INTO `idtest` VALUES ('760975804158488576', '测试：37');
INSERT INTO `idtest` VALUES ('760975806104645632', '1测试：11');
INSERT INTO `idtest` VALUES ('760975806104645633', '\'\'2测试：71');

-- ----------------------------
-- Table structure for test
-- ----------------------------
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4;

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
INSERT INTO `test` VALUES ('8', '测试：95');
INSERT INTO `test` VALUES ('9', '测试：41');
INSERT INTO `test` VALUES ('10', '测试：41');
INSERT INTO `test` VALUES ('11', '测试：32');
INSERT INTO `test` VALUES ('12', '测试异步：77');
INSERT INTO `test` VALUES ('13', '测试：97');
INSERT INTO `test` VALUES ('14', '测试异步：38');
INSERT INTO `test` VALUES ('15', '测试：96');
INSERT INTO `test` VALUES ('16', '测试异步：45');
INSERT INTO `test` VALUES ('17', '测试：34');
INSERT INTO `test` VALUES ('18', '测试：49');
INSERT INTO `test` VALUES ('19', '测试：97');
INSERT INTO `test` VALUES ('20', '测试异步：15');
INSERT INTO `test` VALUES ('21', '测试：52');
INSERT INTO `test` VALUES ('22', '测试异步：11');
INSERT INTO `test` VALUES ('23', '测试：29');
INSERT INTO `test` VALUES ('24', '测试异步：88');
INSERT INTO `test` VALUES ('25', '测试：68');
INSERT INTO `test` VALUES ('26', '测试异步：98');
INSERT INTO `test` VALUES ('27', '测试：92');
INSERT INTO `test` VALUES ('28', '测试异步：87');
INSERT INTO `test` VALUES ('29', '测试：20');
INSERT INTO `test` VALUES ('30', '测试异步：75');
INSERT INTO `test` VALUES ('31', '测试：13');
INSERT INTO `test` VALUES ('32', '测试异步：32');
INSERT INTO `test` VALUES ('33', '测试：41');
INSERT INTO `test` VALUES ('34', '测试异步：89');
