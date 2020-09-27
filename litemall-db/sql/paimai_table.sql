-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: litemall
-- ------------------------------------------------------
-- Server version	5.7.21-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `litemall_ad`
--

--
-- Table structure for table `b_corpinfo`
--
DROP TABLE IF EXISTS `litemall_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_airport                                    */
/*==============================================================*/
create table  `litemall_company`
(
   id  int(11) not null auto_increment,
   poskey varchar(50) DEFAULT '' COMMENT '线下关键主键',
   name varchar(255) DEFAULT '' COMMENT '公司信息',
   desc text DEFAULT '' COMMENT '公司简介',
   app_id int(11) default null COMMENT '公众号ID',
   pid  int(11) not null default 0 COMMENT '上一级公司Id',
   level  int(11) not null default 1  COMMENT '当前层级',
   app_secret varchar(255) DEFAULT '' COMMENT '公众号密钥',
   owned tinyint(1) default 0 COMMENT '是否微商母公司',
   enabled tinyint(1) default 0 COMMENT '是否启用',
   ordernumber int(11) default 0 COMMENT '排序',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id),
   KEY `enabled` (`enabled`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='集团子公司';

--
-- Table structure for table `litemall_company_brand`
--
DROP TABLE IF EXISTS `litemall_company_brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_airport                                    */
/*==============================================================*/
create table  `litemall_company_brand`
(
   id  int(11) not null auto_increment,
   com_id int(11) DEFAULT null COMMENT '公司Id',
   com_name varchar(255) DEFAULT null COMMENT '公司名称',
   brand_id int(11) DEFAULT null COMMENT '品牌Id',
   brand_name varchar(255) DEFAULT null COMMENT '品牌名称',
   ordernumber int(11) default 0 COMMENT '排序',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='公司信息_品牌表';


--以下是拍卖特有的规则 表
--
-- Table structure for table `litemall_authororcorp`
--

DROP TABLE IF EXISTS `litemall_authororcorp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_authororcorp` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type_code` varchar(50) DEFAULT NULL COMMENT '出品人类型编码',
  `type_name` varchar(255) NOT NULL DEFAULT '' COMMENT '出品人类型名称',
  `gallery` varchar(1023) NOT NULL DEFAULT '' COMMENT '出品人代表作，采用JSON数组格式',
  `country_id` int(11) DEFAULT null COMMENT '国家或地区ID',
  `country_name` varchar(255) DEFAULT '' COMMENT '国家或地区名称',
  `code` varchar(50) DEFAULT NULL COMMENT '统一社会信用代码',
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `jian_name` varchar(255) DEFAULT null COMMENT '简称',
  `desc` varchar(1023) DEFAULT null COMMENT '简介',
  `address` varchar(255) default null  COMMENT '地址',
  `contact_person` varchar(255) default null  COMMENT '联系人',
  `telphone` varchar(255) default null  COMMENT '电话',
  `wx` varchar(255) default null  COMMENT '微信号',
  `qq` varchar(255) default null  COMMENT 'QQ号',
  `weibo_site` varchar(255) default null  COMMENT '微博主页',
  `email` varchar(255) default null  COMMENT '电子邮件',
  `bank` varchar(255) default null  COMMENT '开户行',
  `bank_account` varchar(255) default null  COMMENT '银行帐号',
  `bank_account_name` varchar(255) default null  COMMENT '银行户名',
  `web_site` varchar(255) default null  COMMENT '网址',
  `faren` varchar(255) default null  COMMENT '法人代表',
  `faren_id_card` varchar(255) default null  COMMENT '身份证号',
  `province` varchar(255) default null  COMMENT '省',
  `city` varchar(255) default null  COMMENT '市',
  `xian` varchar(255) default null  COMMENT '县',
  `order_number` int(11) default null  COMMENT '排序',
  `enabled` tinyint(1) default '0'  COMMENT '启用标记',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `tenant_id` int(11) default NULL  COMMENT '租户ID',
  `user_create_id` int(11) DEFAULT NULL COMMENT '外部创建用户Id',
  `user_modify_id` int(11) DEFAULT NULL COMMENT '外部修改用户Id',
  `op_create_id` int(11) DEFAULT NULL COMMENT '内部后台创建用户Id',
  `op_modify_id` int(11) DEFAULT NULL COMMENT '内部后台修改用户Id',
  PRIMARY KEY (`id`),
  KEY `enabled` (`enabled`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='出品人或公司';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_auction_zhuanchangl_rule_current`
--
DROP TABLE IF EXISTS `litemall_auction_zhuanchang_rule_current`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_auction_zhuanchang_rule_current                                    */
/*==============================================================*/
create table  `litemall_auction_zhuanchang_rule_current`
(
   id  int(11) not null auto_increment,
   zhuanchang_name varchar(255) default NULL COMMENT '活动名称',
   com_id int(11) default NULL COMMENT '公司ID',
   com_name varchar(255) default NULL COMMENT '公司名称',
   author_id int(11) default NULL COMMENT '出品人或公司Id',
   author_name varchar(255) default NULL COMMENT '出品人名称',
   author_desc varchar(1023) default NULL COMMENT '出品人简介',
   author_zuopin varchar(1023) default null COMMENT '出品人代表作，采用JSON数组格式',
   auction_desc varchar(1023) default NULL COMMENT '本次拍品简介',
   auction_pic_head varchar(255) default NULL COMMENT '拍品头图',
   goods_count int(11) DEFAULT  0 COMMENT '拍品数',
   visit_count int(11) DEFAULT  0 COMMENT '围观数',
   auction_count int(11) default 0 COMMENT '出价数',
   auction_person_count int(11) default 0 COMMENT '出价人数',
   if_display_dajia tinyint(1) default 0 COMMENT '拍品拍卖时不能在大家拍展示',
   begin_time datetime default NULL COMMENT '拍卖开始时间',
   end_time datetime default NULL COMMENT '拍卖结束时间',
   expire_flag tinyint(1) default 0 COMMENT '过期标志 0-未过期 1-过期',
   preview_flag tinyint(1) default 0 COMMENT '预展标志 0-预展 1-不预展',
   free_post tinyint(1) default 0 COMMENT '是否免邮',
   if_use_coupon tinyint(1) DEFAULT  0 COMMENT '是否可用优惠券',
   if_use_bonus tinyint(1) DEFAULT  0 COMMENT '是否可用积分',
   `enabled` tinyint(1) DEFAULT 1 COMMENT '是否启用',
   remark varchar(255) default null COMMENT '执行备注',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
  `tenant_id` int(11) default NULL  COMMENT '租户ID',
  `user_create_id` int(11) DEFAULT NULL COMMENT '外部创建用户Id',
  `user_modify_id` int(11) DEFAULT NULL COMMENT '外部修改用户Id',
  `op_create_id` int(11) DEFAULT NULL COMMENT '内部后台创建用户Id',
  `op_modify_id` int(11) DEFAULT NULL COMMENT '内部后台修改用户Id',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='活动_专场拍_规则_当前';

--
-- Table structure for table `litemall_auction_zhuanchang_goods_current`
--
DROP TABLE IF EXISTS `litemall_auction_zhuanchang_goods_current`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_auction_zhuanchang_goods_current                                    */
/*==============================================================*/
create table  `litemall_auction_zhuanchang_goods_current`
(
   id  int(11) not null auto_increment,
   zhuanchang_id int(11) default NULL COMMENT '专场拍卖活动Id',
   zhuanchang_name varchar(255) default NULL COMMENT '专场拍卖活动名称',
   goods_id int(11) default NULL COMMENT '商品ID',
   goods_sn varchar(63) default NULL COMMENT '商品编号',
   goods_name varchar(255) default NULL COMMENT '商品名称',
   goods_product_id int(11) default null COMMENT '商品货品ID',
   goods_product_specifications varchar(1023) default '' COMMENT '商品规格值列表，采用JSON数组格式',
   gallery_big varchar(1023) default '' COMMENT '商品大图，采用JSON数组格式',
   galllery_small varchar(1023) default '' COMMENT '商品头图，采用JSON数组格式',
   auction_pic_head varchar(1023) default '' COMMENT '列表头图',
   auction_desc varchar(1023) default NULL COMMENT '拍品简介',
   min_price decimal(10,2) default NULL COMMENT '最低起拍价',
   add_price decimal(10,2) default NULL COMMENT '加价单位',
   max_price decimal(10,2) default 0 COMMENT '目前最高价',
   offer_id int(11) default NULL COMMENT '出价主键ID',
   offer_flag tinyint(1) DEFAULT  0 COMMENT '出价标志 0-未出价 1-已出价',
   min_person int(11) default NULL COMMENT '最低出价人数',
   store_num int(11) default NULL COMMENT '活动库存',
   remain_num int(11) default NULL COMMENT '剩余库存',
   remark varchar(255) default null COMMENT '执行备注',
   free_post tinyint(1) default 0 COMMENT '是否免邮',
   visit_count int(11) DEFAULT  0 COMMENT '围观数',
   auction_count int(11) default 0 COMMENT '出价数',
   auction_person_count int(11) default 0 COMMENT '出价人数',
   if_use_coupon tinyint(1) DEFAULT  0 COMMENT '是否可用优惠券',
   if_use_bonus tinyint(1) DEFAULT  0 COMMENT '是否可用积分',
   `order_create_auto` tinyint(1) DEFAULT 1 COMMENT '活动结束按最高出价自动创建订单 0-否 1-是',
   `order_create_flag` varchar(63) NOT NULL DEFAULT '不应创建' COMMENT '订单创建标志 不应创建 应建未建 已经创建',
   order_id int(11) DEFAULT NULL COMMENT '订单ID',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
  `tenant_id` int(11) default NULL  COMMENT '租户ID',
  `user_create_id` int(11) DEFAULT NULL COMMENT '外部创建用户Id',
  `user_modify_id` int(11) DEFAULT NULL COMMENT '外部修改用户Id',
  `op_create_id` int(11) DEFAULT NULL COMMENT '内部后台创建用户Id',
  `op_modify_id` int(11) DEFAULT NULL COMMENT '内部后台修改用户Id',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='活动_专场拍_商品明细_当前';

--
-- Table structure for table `litemall_auction_zhuanchang_offer_current`
--
DROP TABLE IF EXISTS `litemall_auction_zhuanchang_offer_current`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_auction_zhuanchang_offer_current                                    */
/*==============================================================*/
create table  `litemall_auction_zhuanchang_offer_current`
(
   id  int(11) not null auto_increment,
   user_id int(11) default 0 COMMENT '用户ID',
   user_name varchar(255) default '' COMMENT '用户名称',
   rules_id int(11) default 0 COMMENT '专场拍的规则ID，关联litemall_auction_zhuanchangl_rule_current表ID字段',
   rules_mx_id int(11) default 0 COMMENT '专场拍的商品明细Id，关联litemall_auction_zhuanchang_goods_current表Id字段',
   offer_time datetime default NULL COMMENT '出价时间',
   offer_money decimal(10,2) default NULL COMMENT '出价金额',
   ordernumber int(11) default null COMMENT '出价排序',
   offer_state varchar(255) default '' COMMENT '出价状态 出局 最高',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
  `tenant_id` int(11) default NULL  COMMENT '租户ID',
  `user_create_id` int(11) DEFAULT NULL COMMENT '外部创建用户Id',
  `user_modify_id` int(11) DEFAULT NULL COMMENT '外部修改用户Id',
  `op_create_id` int(11) DEFAULT NULL COMMENT '内部后台创建用户Id',
  `op_modify_id` int(11) DEFAULT NULL COMMENT '内部后台修改用户Id',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='活动_专场拍_出价记录_当前';

--
-- Table structure for table `litemall_auciton_zhuanchang_order_current`
--
DROP TABLE IF EXISTS `litemall_auciton_zhuanchang_order_current`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_auciton_zhuanchang_order_current                                    */
/*==============================================================*/
create table  `litemall_auciton_zhuanchang_order_current`
(
   id  int(11) not null auto_increment,
   user_id int(11) default NULL COMMENT '用户ID',
   order_id int(11) default NULL COMMENT '关联的订单ID',
   rules_id int(11) default NULL COMMENT '专场拍的规则ID，关联litemall_auction_zhuanchangl_rule表ID字段',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
  `tenant_id` int(11) default NULL  COMMENT '租户ID',
  `user_create_id` int(11) DEFAULT NULL COMMENT '外部创建用户Id',
  `user_modify_id` int(11) DEFAULT NULL COMMENT '外部修改用户Id',
  `op_create_id` int(11) DEFAULT NULL COMMENT '内部后台创建用户Id',
  `op_modify_id` int(11) DEFAULT NULL COMMENT '内部后台修改用户Id',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='活动_专场拍_订单_当前';

--
-- Table structure for table `litemall_auction_dajia_rule_current`
--

DROP TABLE IF EXISTS `litemall_auction_dajia_rule_current`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_auction_dajia_rule_current` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `min_price` decimal(10,2) NOT NULL COMMENT '最低起拍价',
  `add_price` decimal(10,2) DEFAULT NULL COMMENT '加价单位',
  `min_person` int(11) NOT NULL COMMENT '最低出价人数',
  `begin_time` datetime NOT NULL COMMENT '拍卖开始时间',
  `end_time` datetime NOT NULL COMMENT '拍卖结束时间',
  `expire_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '过期标志 0-未过期 1-过期',
  `free_post` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否免邮',
  `if_use_coupon` varchar(255) NOT NULL DEFAULT '0' COMMENT '是否可用优惠券',
  `if_use_bonus` varchar(255) NOT NULL DEFAULT '0' COMMENT '是否可用积分',
  `goods_id` int(11) NOT NULL COMMENT '商品ID',
  `goods_sn` varchar(63) NOT NULL COMMENT '商品编号',
  `goods_name` varchar(255) NOT NULL COMMENT '商品名称',
  `goods_product_id` int(11) NOT NULL COMMENT '商品货品ID',
  `goods_product_specifications` varchar(1023) NOT NULL COMMENT '商品规格值列表，采用JSON数组格式',
   auction_pic_head varchar(255) default NULL COMMENT '拍品头图',
   gallery_big varchar(1023) default '' COMMENT '商品大图，采用JSON数组格式',
   galllery_small varchar(1023) default '' COMMENT '商品头图，采用JSON数组格式',
  `dajiapai_category_code` varchar(255) DEFAULT null COMMENT '所属大家拍分类目编码',
  `dajiapai_category_name` varchar(255) DEFAULT null COMMENT '所属大家拍分类目名称',
  `auction_desc` varchar(1023) DEFAULT NULL COMMENT '拍品简介',
  `store_num` int(11) DEFAULT NULL COMMENT '活动库存',
  `remain_num` int(11) DEFAULT NULL COMMENT '剩余库存',
  `remark` varchar(255) DEFAULT NULL COMMENT '执行备注',
  `enabled` tinyint(1) DEFAULT 1 COMMENT '是否启用',
   max_price decimal(10,2) default 0 COMMENT '目前最高价',
   offer_id int(11) default NULL COMMENT '出价主键ID',
  `offer_flag` tinyint(1) DEFAULT 0 COMMENT '出价标志',
   goods_count int(11) DEFAULT  0 COMMENT '拍品数',
   visit_count int(11) DEFAULT  0 COMMENT '围观数',
   auction_count int(11) default 0 COMMENT '出价数',
   auction_person_count int(11) default 0 COMMENT '出价人数',
   `order_create_auto` tinyint(1) DEFAULT 1 COMMENT '活动结束按最高出价自动创建订单 0-否 1-是',
   `order_create_flag` varchar(63) NOT NULL DEFAULT '不应创建' COMMENT '订单创建标志 不应创建 应建未建 已经创建',
   order_id int(11) DEFAULT NULL COMMENT '订单ID',
  `add_time` datetime DEFAULT  NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `tenant_id` int(11) default NULL  COMMENT '租户ID',
  `user_create_id` int(11) DEFAULT NULL COMMENT '外部创建用户Id',
  `user_modify_id` int(11) DEFAULT NULL COMMENT '外部修改用户Id',
  `op_create_id` int(11) DEFAULT NULL COMMENT '内部后台创建用户Id',
  `op_modify_id` int(11) DEFAULT NULL COMMENT '内部后台修改用户Id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='活动_大家拍_商品_当前';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_auction_dajia_offer_current`
--

DROP TABLE IF EXISTS `litemall_auction_dajia_offer_current`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_auction_dajia_offer_current` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `user_name` varchar(255) DEFAULT NULL COMMENT '用户名称',
  `rules_id` int(11) NOT NULL COMMENT '大家拍的规则ID，关联litemall_auction_dajia_rule_current表ID字段',
  `offer_time` datetime NOT NULL COMMENT '出价时间',
  `offer_money` decimal(10,2) NOT NULL COMMENT '出价金额',
  `ordernumber` int(11) DEFAULT NULL COMMENT '出价排序',
  `offer_state` varchar(255) NOT NULL COMMENT '出价状态',
  `add_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `tenant_id` int(11) default NULL  COMMENT '租户ID',
  `user_create_id` int(11) DEFAULT NULL COMMENT '外部创建用户Id',
  `user_modify_id` int(11) DEFAULT NULL COMMENT '外部修改用户Id',
  `op_create_id` int(11) DEFAULT NULL COMMENT '内部后台创建用户Id',
  `op_modify_id` int(11) DEFAULT NULL COMMENT '内部后台修改用户Id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='活动_大家拍_出价记录_当前';
/*!40101 SET character_set_client = @saved_cs_client */;
--
-- Table structure for table `litemall_auction_dajia_order_current`
--

DROP TABLE IF EXISTS `litemall_auction_dajia_order_current`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_auction_dajia_order_current` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `order_id` int(11) NOT NULL COMMENT '关联的订单ID',
  `rules_id` int(11) NOT NULL COMMENT '大家拍的规则ID，关联litemall_auction_dajia_rule_current表ID字段',
  `add_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `tenant_id` int(11) default NULL  COMMENT '租户ID',
  `user_create_id` int(11) DEFAULT NULL COMMENT '外部创建用户Id',
  `user_modify_id` int(11) DEFAULT NULL COMMENT '外部修改用户Id',
  `op_create_id` int(11) DEFAULT NULL COMMENT '内部后台创建用户Id',
  `op_modify_id` int(11) DEFAULT NULL COMMENT '内部后台修改用户Id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='活动_大家拍_订单_当前';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `litemall_authororcorp_para`
--

DROP TABLE IF EXISTS `litemall_authororcorp_para`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_authororcorp_para` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `author_id` int(11) NOT NULL COMMENT '出品人或公司Id',
  `attribute` varchar(255) DEFAULT NULL COMMENT '参数名称',
  `value` varchar(255) DEFAULT NULL COMMENT '参数值',
  `ordernumber` int(11) DEFAULT NULL COMMENT '排序',
  `add_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `tenant_id` int(11) default NULL  COMMENT '租户ID',
  `user_create_id` int(11) DEFAULT NULL COMMENT '外部创建用户Id',
  `user_modify_id` int(11) DEFAULT NULL COMMENT '外部修改用户Id',
  `op_create_id` int(11) DEFAULT NULL COMMENT '内部后台创建用户Id',
  `op_modify_id` int(11) DEFAULT NULL COMMENT '内部后台修改用户Id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='出品人或公司_参数表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_authororcorp_items`
--

DROP TABLE IF EXISTS `litemall_authororcorp_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_authororcorp_items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `author_id` int(11) NOT NULL COMMENT '出品人或公司Id',
  `item_name` varchar(255) DEFAULT NULL COMMENT '预约品项名称',
  `item_min_price` decimal(10,2) DEFAULT NULL COMMENT '预约起价',
  `item_ding_money` decimal(10,2) DEFAULT NULL COMMENT '预约订金',
  `item_finish_days` int(11) DEFAULT NULL COMMENT '完工周期(天)',
  `ordernumber` int(11) DEFAULT NULL COMMENT '排序',
  `add_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `tenant_id` int(11) default NULL  COMMENT '租户ID',
  `user_create_id` int(11) DEFAULT NULL COMMENT '外部创建用户Id',
  `user_modify_id` int(11) DEFAULT NULL COMMENT '外部修改用户Id',
  `op_create_id` int(11) DEFAULT NULL COMMENT '内部后台创建用户Id',
  `op_modify_id` int(11) DEFAULT NULL COMMENT '内部后台修改用户Id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='出品人或公司_可预约项目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_private_make_rule`
--

DROP TABLE IF EXISTS `litemall_private_make_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_private_make_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `author_id` int(11) NOT NULL COMMENT '出品人Id',
  `author_name` varchar(255) DEFAULT NULL COMMENT '出品人名称',
  `rule_desc` varchar(1023) DEFAULT NULL COMMENT '预约规则',
  `goods_id` int(11) DEFAULT NULL COMMENT '商品ID',
  `goods_sn` varchar(63) DEFAULT NULL COMMENT '商品编号',
  `goods_name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `goods_product_id` int(11) DEFAULT NULL COMMENT '商品货品ID',
  `goods_product_specifications` varchar(1023) DEFAULT NULL COMMENT '商品规格值列表，采用JSON数组格式',
  `private_category_code` varchar(255) DEFAULT null COMMENT '所属私人定制类目编码',
  `private_category_name` varchar(255) DEFAULT null COMMENT '所属私人定制类目名称',
  `private_pic_head` varchar(255) DEFAULT NULL COMMENT '私人定制头图',
   gallery_big varchar(1023) default '' COMMENT '商品大图，采用JSON数组格式',
   galllery_small varchar(1023) default '' COMMENT '商品头图，采用JSON数组格式',
  `low_price` decimal(10,2) DEFAULT NULL COMMENT '标准报价低',
  `max_price` decimal(10,2) DEFAULT NULL COMMENT '标准报价高',
  `dingjin_price` decimal(10,2) DEFAULT NULL COMMENT '预付订金',
  `order_count` int(11) DEFAULT 0 COMMENT '成交单量',
  `begin_time` datetime DEFAULT NULL COMMENT '报价有效开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '报价有效截止时间',
  `expire_flag` tinyint(1) DEFAULT '0' COMMENT '过期标志 0-未过期 1-过期',
  `free_post` tinyint(1) DEFAULT '0' COMMENT '是否免邮',
  `enabled` tinyint(1) DEFAULT 1 COMMENT '是否启用',
  `offer_flag` tinyint(1) DEFAULT 0 COMMENT '出价标志',
  `remark` varchar(1023) DEFAULT NULL COMMENT '备注',
  `auction_desc` varchar(1023) DEFAULT NULL COMMENT '商品描述',
  `add_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `tenant_id` int(11) default NULL  COMMENT '租户ID',
  `user_create_id` int(11) DEFAULT NULL COMMENT '外部创建用户Id',
  `user_modify_id` int(11) DEFAULT NULL COMMENT '外部修改用户Id',
  `op_create_id` int(11) DEFAULT NULL COMMENT '内部后台创建用户Id',
  `op_modify_id` int(11) DEFAULT NULL COMMENT '内部后台修改用户Id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='活动_私人定制_规则_当前';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_private_make_order`
--

DROP TABLE IF EXISTS `litemall_private_make_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_private_make_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `order_id` int(11) NOT NULL COMMENT '关联的订单ID',
  `rules_id` int(11) NOT NULL COMMENT '私人定制规则ID',
  `add_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `tenant_id` int(11) default NULL  COMMENT '租户ID',
  `user_create_id` int(11) DEFAULT NULL COMMENT '外部创建用户Id',
  `user_modify_id` int(11) DEFAULT NULL COMMENT '外部修改用户Id',
  `op_create_id` int(11) DEFAULT NULL COMMENT '内部后台创建用户Id',
  `op_modify_id` int(11) DEFAULT NULL COMMENT '内部后台修改用户Id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='活动_私人定制_订单_当前';
/*!40101 SET character_set_client = @saved_cs_client */;
