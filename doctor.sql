/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50723
Source Host           : localhost:3306
Source Database       : es

Target Server Type    : MYSQL
Target Server Version : 50723
File Encoding         : 65001

Date: 2019-03-10 17:04:45
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `doctor`
-- ----------------------------
DROP TABLE IF EXISTS `doctor`;
CREATE TABLE `doctor` (
  `doctor_id` varchar(10) NOT NULL,
  `doctor_name` varchar(200) DEFAULT '',
  `doctor_title` varchar(50) DEFAULT '',
  `specialty` varchar(4000) DEFAULT '',
  `label` varchar(2000) DEFAULT '',
  `doctor_des` varchar(4000) DEFAULT '',
  `hospital_id` varchar(100) NOT NULL DEFAULT '',
  `hospital_name` varchar(200) NOT NULL,
  PRIMARY KEY (`doctor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;