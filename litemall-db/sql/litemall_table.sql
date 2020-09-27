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

DROP TABLE IF EXISTS `litemall_ad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_ad` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(63) NOT NULL DEFAULT '' COMMENT '广告标题',
  `link` varchar(255) NOT NULL DEFAULT '' COMMENT '所广告的商品页面或者活动页面链接地址',
  `url` varchar(255) NOT NULL COMMENT '广告宣传图片',
  `position` tinyint(3) DEFAULT '1' COMMENT '广告位置：1则是首页',
  `content` varchar(255) DEFAULT '' COMMENT '活动内容',
  `start_time` datetime DEFAULT NULL COMMENT '广告开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '广告结束时间',
  `enabled` tinyint(1) DEFAULT '0' COMMENT '是否启动',
  `ordernumber` int(3) default null  COMMENT '排序',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `enabled` (`enabled`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='广告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_home_icon`
--
DROP TABLE IF EXISTS `litemall_home_icon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_home_icon                                    */
/*==============================================================*/
create table  `litemall_home_icon`
(
   id  int(11) not null auto_increment,
   name varchar(63) DEFAULT '' COMMENT '图标标题',
   link varchar(255) DEFAULT '' COMMENT 'icon链接地址',
   url  varchar(255) DEFAULT '' COMMENT 'icon图片',
   enabled tinyint(1) default 0 COMMENT '是否启动',
   ordernumber int(11) default 0 COMMENT '排序',
   remark varchar(255) DEFAULT '' COMMENT '备注',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id),
   KEY `enabled` (`enabled`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='首页_链接图标_Icon';

--
-- Table structure for table `litemall_home_backgroud_image`
--
DROP TABLE IF EXISTS `litemall_home_background_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_home_background_image                                    */
/*==============================================================*/
create table  `litemall_home_background_image`
(
   id  int(11) not null auto_increment,
   name varchar(63) DEFAULT '' COMMENT '背景标题',
   url  varchar(255) DEFAULT '' COMMENT '背景图片',
   enabled tinyint(1) default 0 COMMENT '是否启动',
   ordernumber int(11) default 0 COMMENT '排序',
   remark varchar(255) DEFAULT '' COMMENT '备注',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id),
   KEY `enabled` (`enabled`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='首页_背景图_BackImage';


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
-- Table structure for table `litemall_company_hangzhanlou`
--
DROP TABLE IF EXISTS `litemall_company_hangzhanlou`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_company_hangzhanlou                                    */
/*==============================================================*/
create table  `litemall_company_hangzhanlou`
(
   id  int(11) not null auto_increment,
   com_id int(11) DEFAULT null COMMENT '公司Id',
   com_name varchar(255) DEFAULT null COMMENT '公司名称',
   hangzhanlou_name varchar(255) DEFAULT null COMMENT '取货点名称',
   ordernumber int(11) default 0 COMMENT '排序',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='集团公司_取货点设置';

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

--
-- Table structure for table `litemall_company_hangzhanlou`
--
DROP TABLE IF EXISTS `litemall_company_hangzhanlou`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_company_hangzhanlou                                    */
/*==============================================================*/
create table  `litemall_company_hangzhanlou`
(
   id  int(11) not null auto_increment,
   com_id int(11) DEFAULT null COMMENT '公司Id',
   com_name varchar(255) DEFAULT null COMMENT '公司名称',
   hangzhanlou_name varchar(255) DEFAULT null COMMENT '航站楼名称',
   ordernumber int(11) default 0 COMMENT '排序',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='公司信息_航站楼';

--
-- Table structure for table `litemall_store`
--
DROP TABLE IF EXISTS `litemall_store`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_store                                    */
/*==============================================================*/
create table  `litemall_store`
(
   id  int(11) not null auto_increment,
   com_id int(11) not null DEFAULT '0' COMMENT '所属公司ID',
   com_name varchar(255) DEFAULT '' COMMENT '所属公司信息',
   hangzhanlou_id int(11)  DEFAULT NULL COMMENT '所属航站楼ID',
   hangzhanlou_name varchar(255) DEFAULT '' COMMENT '所属航站楼名称',
   own_type tinyint(1) default 0 COMMENT '店铺类型,0-线下,1-电商',
   poskey varchar(50) DEFAULT '' COMMENT '线下关键主键',
   name varchar(255) DEFAULT '' COMMENT '店铺名称',
   posdes varchar(255) DEFAULT '' COMMENT '店铺位置',
   phone varchar(50) DEFAULT '' COMMENT '门店电话',
   work_time varchar(255) DEFAULT '' COMMENT '营业时间',
   work_week varchar(255) DEFAULT '' COMMENT '营业星期',
   yuyue_time varchar(1023) DEFAULT null COMMENT '店铺预约时间',
   yuyue_time_desc varchar(255) DEFAULT null COMMENT '店铺预约时间描述',
   yuyue_week varchar(1023) DEFAULT null COMMENT '店铺预约周几',
   storedesc text DEFAULT '' COMMENT '店铺经营简介',
   enabled tinyint(1) default 0 COMMENT '是否启用',
   ordernumber int(11) default 0 COMMENT '排序',
   remark varchar(255) default '' COMMENT '备注',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id),
   KEY `enabled` (`enabled`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='线下_店铺表';


--
-- Table structure for table `litemall_store_brand`
--
DROP TABLE IF EXISTS `litemall_store_brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_store_brand                                    */
/*==============================================================*/
create table  `litemall_store_brand`
(
   id  int(11) not null auto_increment,
   store_id int(11) not null DEFAULT '0' COMMENT '店铺Id',
   store_name varchar(255) DEFAULT '' COMMENT '店铺名称',
   brand_id int(11) not null DEFAULT '0' COMMENT '品牌商ID',
   brand_name varchar(255) DEFAULT '' COMMENT '品牌商名称',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id)

) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='线下_店铺_品牌表';


--
-- Table structure for table `litemall_dic_main`
--
DROP TABLE IF EXISTS `litemall_dic_main`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_dic_main                                    */
/*==============================================================*/
create table  `litemall_dic_main`
(
   id  int(11) not null auto_increment,
   name varchar(255) not null  DEFAULT '' COMMENT '字典列表名称',
   desp varchar(255) DEFAULT '' COMMENT '字典表描述',
   systemed tinyint(1) default 0 COMMENT '系统标记 0-正常 1-系统',
   enabled tinyint(1) default 0 COMMENT '停用标记 0-正常 1-停用',
   ordernumber tinyint(3) default 0 COMMENT '排序',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id),
   KEY `enabled` (`enabled`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='字典_主表';

--
-- Table structure for table `litemall_dic_code`
--
DROP TABLE IF EXISTS `litemall_dic_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_dic_code                                    */
/*==============================================================*/
create table  `litemall_dic_code`
(
   id  int(11) not null auto_increment,
   mainid int(11) not null  DEFAULT '' COMMENT '字典主表ID',
   mainname varchar(255)  default '' COMMENT '主表名称',
   code varchar(50)  not null DEFAULT '' COMMENT '代码编码',
   name varchar(255)  not null default '' COMMENT '代码名称',
   ordernumber tinyint(3) default 0 COMMENT '排序',
   remark varchar(255) DEFAULT '' COMMENT '代码备注',
   systemed tinyint(1) default 0 COMMENT '系统标记 0-正常 1-系统',
   enabled tinyint(1) default 0 COMMENT '停用标记 0-正常 1-停用',
   attr1 varchar(255) DEFAULT '' COMMENT '代码属性1',
   attr2 varchar(255) DEFAULT '' COMMENT '代码属性2',
   attr3 varchar(255) DEFAULT '' COMMENT '代码属性3',
   attr4 varchar(255) DEFAULT '' COMMENT '代码属性4',
   attr5 varchar(255) DEFAULT '' COMMENT '代码属性5',
   attr6 varchar(255) DEFAULT '' COMMENT '代码属性6',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id),
   KEY `enabled` (`enabled`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='字典_代码表';


--
-- Table structure for table `litemall_goods_recommend`
--
DROP TABLE IF EXISTS `litemall_goods_recommend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_goods_recommend                                    */
/*==============================================================*/
create table  `litemall_goods_recommend`
(
   id  int(11) not null auto_increment,
   pos_type varchar(50) not null  DEFAULT '' COMMENT '展示位置名称',
   com_id int(11) default 0 COMMENT '公司ID',
   com_name varchar(255) default '' COMMENT '公司信息',
   goods_id int(11) default 0 COMMENT '商品ID',
   goods_sn varchar(63) default '' COMMENT '商品编号',
   goods_name varchar(255) default '' COMMENT '商品名称',
   goods_product_id int(11) default 0 COMMENT '商品货品ID',
   goods_product_specifications varchar(255) default '' COMMENT '商品规格值列表，采用JSON数组格式',
   ordernumber tinyint(3) default 0 COMMENT '排序',

   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='推荐商品';



--
-- Table structure for table `litemall_promotion_seckill_rule`
--
DROP TABLE IF EXISTS `litemall_promotion_seckill_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_promotion_seckill_rule                                    */
/*==============================================================*/
create table  `litemall_promotion_seckill_rule`
(
   id  int(11) not null auto_increment,
   seckill_name varchar(255) not null  DEFAULT '' COMMENT '活动名称',
   com_id int(11) default 0 COMMENT '公司信息ID',
   com_name varchar(255) default '' COMMENT '公司信息',
   goods_id int(11) default 0 COMMENT '商品ID',
   goods_sn varchar(63) default '' COMMENT '商品编号',
   goods_name varchar(255) default '' COMMENT '商品名称',
   goods_product_id int(11) default 0 COMMENT '商品货品ID',
   goods_product_specifications varchar(255) default '' COMMENT '商品规格值列表，采用JSON数组格式',

   source_price decimal(10,2) default 0 COMMENT '原价',
   seckill_price decimal(10,2) default 0 COMMENT '秒杀价',
   seckill_store_num int(11) default 0 COMMENT '活动库存',
   seckill_remain_num int(11) default 0 COMMENT '剩余库存',
   seckill_begin_date datetime DEFAULT  NULL COMMENT '开始时间',
   seckill_end_date datetime DEFAULT  NULL COMMENT '结束时间',
   seckill_only_one tinyint(1) default 0 COMMENT '每人限抢一次 1-是 0-否',
   seckill_only_buy_one tinyint(1) default 0 COMMENT '每人限购一件 1-是 0-否',
   seckill_expire_flag tinyint(1) default 0 COMMENT '过期标志 0-未过期 1-过期',
   pic_url varchar(255) default '' COMMENT '活动图片路径',
   free_post tinyint(1) default 0 COMMENT '是否免邮 0-否 1-是',
   if_use_coupon tinyint(1) default 0 COMMENT '是否可用优惠券 0-否 1-是',
   if_use_bonus tinyint(1) default 0 COMMENT '是否可用积分 0-否 1-是',
   remark varchar(255) default '' COMMENT '执行备注',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='秒杀活动_规则';



--
-- Table structure for table `litemall_promotion_seckill_order`
--
DROP TABLE IF EXISTS `litemall_promotion_seckill_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_promotion_seckill_order                                    */
/*==============================================================*/
create table  `litemall_promotion_seckill_order`
(
   id  int(11) not null auto_increment,
   user_id int(11) default 0 COMMENT '用户ID',
   order_id int(11) default 0 COMMENT '关联的订单ID',
   rules_id int(11) default 0 COMMENT '秒杀的规则ID，关联litemall_promotion_seckill_rule表ID字段',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='秒杀活动_订单';

--
-- Table structure for table `litemall_promotion_seckill_order`
--
DROP TABLE IF EXISTS `litemall_promotion_goods_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_promotion_goods_rule                                    */
/*==============================================================*/
create table  `litemall_promotion_goods_rule`
(
    id  int(11) not null auto_increment,
    name varchar(255) default '' COMMENT '活动名称',
    com_id int(11) default null COMMENT '公司ID',
    com_name varchar(255) default '' COMMENT '公司名称',
    category_id int(11) default null COMMENT '商品类别ID',
    category_name varchar(255) default null COMMENT '商品所属类目名称',
    goods_id int(11) default null COMMENT '商品ID',
    goods_sn varchar(63) default '' COMMENT '商品编号',
    goods_name varchar(255) default '' COMMENT '商品名称',
    goods_product_id int(11) default null COMMENT '商品规格ID',
    goods_product_specifications varchar(1023) default '' COMMENT '商品规格值列表，采用JSON数组格式',
    source_price decimal(10,2) default null COMMENT '原价',
    user_price decimal(10,2) default null COMMENT '会员价',
    store_num int(11) default null COMMENT '活动库存',
    remain_num int(11) default null COMMENT '剩余库存',
    begin_date datetime DEFAULT  NULL COMMENT '开始时间',
    end_date datetime DEFAULT  NULL COMMENT '结束时间',
    only_one tinyint(1) default 0 COMMENT '每人限购一次 1-是 0-否',
    only_buy_one tinyint(1) default 0 COMMENT '每人限购一件 1-是 0-否',
    expire_flag tinyint(1) default 0 COMMENT '过期标志 0-未过期 1-过期',
    remark varchar(255) default '' COMMENT '执行备注',
    pic_url varchar(255) default '' COMMENT '活动图片路径',
    free_post tinyint(1) default 0 COMMENT '是否免邮 0-否 1-是',
    goods_pos_key varchar(50) default null COMMENT  '线下Pos店商品码',
    if_only_use_bonus tinyint(1) default 0 COMMENT '会员商品是否只能积分购买 0-否 1-是',
    if_use_coupon tinyint(1) default 0 COMMENT '是否可用优惠券',
    if_use_bonus tinyint(1) default 0 COMMENT '是否可用积分',
    if_allow_addcart tinyint(1) default 0 COMMENT '是否单独结算',
    add_time datetime DEFAULT  NULL COMMENT '创建时间',
    update_time datetime DEFAULT  NULL COMMENT '更新时间',
    deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='活动_专享商品_规则';

--
-- Table structure for table `litemall_promotion_goods_rule_user`
--
DROP TABLE IF EXISTS `litemall_promotion_goods_rule_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_promotion_goods_rule_user                                    */
/*==============================================================*/
create table  `litemall_promotion_goods_rule_user`
(
   id  int(11) not null auto_increment,
   rule_id int(11) default 0 COMMENT '规则ID',
   user_class_attr1_code varchar(50) default null COMMENT '用户分类属性一编码',
   user_class_attr1_name varchar(255) default '' COMMENT '用户分类属性一名称',
   user_class_attr2_code varchar(50) default null COMMENT '用户分类属性二ID',
   user_class_attr2_name varchar(255) default '' COMMENT '用户分类属性二名称',
   user_id int(11) default null COMMENT '会员ID',
   username varchar(63) default '' COMMENT '会员名称',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='活动_专享商品_规则_会员';




--
-- Table structure for table `litemall_promotion_goods_user_order`
--
DROP TABLE IF EXISTS `litemall_promotion_goods_user_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_promotion_user_goods_order                                    */
/*==============================================================*/
create table  `litemall_promotion_goods_user_order`
(
   id  int(11) not null auto_increment,
   user_id int(11) default 0 COMMENT 'id',
   order_id int(11) default 0 COMMENT '关联的订单ID',
   rules_id int(11) default null COMMENT '会员活动的规则ID，关联litemall_promotion_user_goods_rule表ID字段',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='活动_专享商品_会员_订单';


--
-- Table structure for table `litemall_promotion_goods_rebate_rule`
--
DROP TABLE IF EXISTS `litemall_promotion_goods_rebate_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_promotion_goods_rebate_rule                                    */
/*==============================================================*/
create table  `litemall_promotion_goods_rebate_rule`
(
    id  int(11) not null auto_increment,
    name varchar(255) default '' COMMENT '活动名称',
    com_id int(11) default null COMMENT '公司ID',
    com_name varchar(255) default '' COMMENT '公司名称',
    rebate decimal(10,2) default null COMMENT '折扣',
    begin_date datetime DEFAULT  NULL COMMENT '开始时间',
    end_date datetime DEFAULT  NULL COMMENT '结束时间',
    expire_flag tinyint(1) default 0 COMMENT '过期标志 0-未过期 1-过期',
    remark varchar(255) default '' COMMENT '执行备注',
    add_time datetime DEFAULT  NULL COMMENT '创建时间',
    update_time datetime DEFAULT  NULL COMMENT '更新时间',
    deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='活动_品项折扣_规则';


--
-- Table structure for table `litemall_promotion_goods_rebate_goods`
--
DROP TABLE IF EXISTS `litemall_promotion_goods_rebate_goods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_promotion_goods_rebate_goods                                    */
/*==============================================================*/
create table  `litemall_promotion_goods_rebate_goods`
(
   id  int(11) not null auto_increment,
   rule_id int(11) default null COMMENT '商品折扣规则Id',
   goods_id int(11) default null COMMENT '商品ID',
   goods_sn varchar(63) default '' COMMENT '商品编号',
   goods_name varchar(255) default '' COMMENT '商品名称',
   goods_product_id int(11) default null COMMENT '商品规格ID',
   goods_product_specifications varchar(1023) default '' COMMENT '商品规格值列表，采用JSON数组格式',
   source_price decimal(10,2) default null COMMENT '原价',
   rebate_price decimal(10,2) default null COMMENT '折后价',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='活动_品项折扣_商品';



--
-- Table structure for table `litemall_promotion_goods_rebate_order_goods`
--
DROP TABLE IF EXISTS `litemall_promotion_goods_rebate_order_goods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_promotion_goods_rebate_order_goods                                    */
/*==============================================================*/
create table  `litemall_promotion_goods_rebate_order_goods`
(
   id  int(11) not null auto_increment,
   user_id int(11) default null COMMENT '用户ID',
   order_id int(11) default null COMMENT '关联的订单ID',
   product_id int(11) default null COMMENT '关联的商品ID',
   rule_id int(11) default null COMMENT '品项折扣的规则ID',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='活动_品项折扣_订单_商品';


--
-- Table structure for table `litemall_huodong_main`
--
DROP TABLE IF EXISTS `litemall_huodong_main`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_huodong_main                                    */
/*==============================================================*/
create table  `litemall_huodong_main`
(
   id  int(11) not null auto_increment,
   name varchar(255)  default null COMMENT '活动名称',
   begin_date datetime DEFAULT  NULL COMMENT '活动起始时间',
   end_date datetime DEFAULT  NULL COMMENT '活动截止时间',
   expire_flag tinyint(1) default 0 COMMENT '是否过期',
   link_pic_url varchar(255) default null COMMENT '活动头图',
   link_web_url varchar(255) default null COMMENT '活动链接',
   sort_order int(11) default null COMMENT '活动排序',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='活动承接页_主表';

--
-- Table structure for table `litemall_huodong_detail_goods_list`
--
DROP TABLE IF EXISTS `litemall_huodong_detail_goods_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_huodong_detail_goods_list                                    */
/*==============================================================*/
create table  `litemall_huodong_detail_goods_list`
(
   id  int(11) not null auto_increment,
   main_id int(11) default null COMMENT '活动主表Id',
   goods_id int(11) DEFAULT  NULL COMMENT '活动商品Id',
   goods_name varchar(255) default null COMMENT '商品名称',
   goods_pic_url varchar(255) default null COMMENT '商品图片',
   sort_order int(11) DEFAULT  NULL COMMENT '排序',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='活动承接页_子表_活动商品列表';

--
-- Table structure for table `litemall_huodong_detail_pic_link`
--
DROP TABLE IF EXISTS `litemall_huodong_detail_pic_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_huodong_detail_pic_link                                    */
/*==============================================================*/
create table  `litemall_huodong_detail_pic_link`
(
   id  int(11) not null auto_increment,
   main_id int(11) default null COMMENT '活动主表Id',
   pic_url varchar(255) default null COMMENT '活动图片',
   goods_id int(11) DEFAULT  NULL COMMENT '活动商品Id',
   goods_name varchar(255) default null COMMENT '商品名称',
   goods_pic_url varchar(255) default null COMMENT '商品图片',
   sort_order int(11) DEFAULT  NULL COMMENT '排序',
   link varchar(255) default null COMMENT '活动商品链接',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='活动承接页_子表_活动页商品链接';

--
-- Table structure for table `litemall_article_class`
--
DROP TABLE IF EXISTS `litemall_article_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_article_class                                    */
/*==============================================================*/
create table  `litemall_article_class`
(
   id  int(11) not null auto_increment,
   name varchar(255) default '' COMMENT '分类名称',
   pid int(11) default 0 COMMENT '父类目ID',
   p_path varchar(1023) default null COMMENT '父类路径',
   level int(11) default null COMMENT '层级',
   sort_order int(11) default null COMMENT '排序',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='文章分类';

--
-- Table structure for table `litemall_article_list`
--
DROP TABLE IF EXISTS `litemall_article_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_article_list                                    */
/*==============================================================*/
create table  `litemall_article_list`
(
   id  int(11) not null auto_increment,
   class_id int(11) default null COMMENT '所属分类Id',
   class_name varchar(255) default null COMMENT '所属分类名称',
   class_path varchar(255) default null COMMENT '分类路径',
   title varchar(255) default null COMMENT '文章标题',
   content text default null COMMENT '文章内容',
   author varchar(50) default null COMMENT '作者',
   pub_date datetime default null COMMENT '发表日期',
   sort_order int(11) default null COMMENT '排序',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='文章列表';


--
-- Table structure for table `litemall_wenboguan_article`
--
DROP TABLE IF EXISTS `litemall_wenboguan_article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_wenboguan_article                                    */
/*==============================================================*/
create table  `litemall_wenboguan_article`
(
   id  int(11) not null auto_increment,
   class_id int(11) default null COMMENT '所属分类Id',
   class_name varchar(255) default null COMMENT '所属分类名称',
   class_path varchar(255) default null COMMENT '分类路径',
   title varchar(255) default null COMMENT '文章标题',
   fengmian text default null COMMENT '文章封面',
   content text default null COMMENT '文章内容',
   author varchar(50) default null COMMENT '作者',
   pic_head varchar(255) default null COMMENT '作者头像',
   pub_date date default null COMMENT '发表日期',
   sort_order int(11) default null COMMENT '排序',
   browse_count int(11) default null COMMENT '浏览量',
   zan_count int(11) default null COMMENT '点赞数',
   share_count int(11) default null COMMENT '分享数',
   card_pic varchar(255) default null COMMENT '卡片配图',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   tenant_id int(11) default null COMMENT '租户ID',
   user_create_id int(11) default null COMMENT '外部创建用户Id',
   user_modify_id int(11) default null COMMENT '外部修改用户Id',
   op_create_id int(11) default null COMMENT '内部后台创建用户Id',
   op_modify_id int(11) default null COMMENT '内部后台修改用户Id',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='文博馆_推荐排序';


--
-- Table structure for table `litemall_weboguan_recommend`
--
DROP TABLE IF EXISTS `litemall_weboguan_recommend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
/*==============================================================*/
/* Table: litemall_weboguan_recommend                                    */
/*==============================================================*/
create table  `litemall_weboguan_recommend`
(
   id int(11) not null auto_increment,
   article_id int(11) default null COMMENT '文博馆文章ID',
   ordernumber tinyint(3) default 0 COMMENT '排序',
   add_time datetime DEFAULT  NULL COMMENT '创建时间',
   update_time datetime DEFAULT  NULL COMMENT '更新时间',
   deleted tinyint(1) default 0 COMMENT '逻辑删除',
   tenant_id int(11) default null COMMENT '租户ID',
   user_create_id int(11) default null COMMENT '外部创建用户Id',
   user_modify_id int(11) default null COMMENT '外部修改用户Id',
   op_create_id int(11) default null COMMENT '内部后台创建用户Id',
   op_modify_id int(11) default null COMMENT '内部后台修改用户Id',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='文博馆_推荐排序';


--
-- Table structure for table `litemall_address`
--

DROP TABLE IF EXISTS `litemall_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_address` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(63) NOT NULL COMMENT '收货人名称',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户表的用户ID',
  `wx_nickname` varchar(63)  DEFAULT null COMMENT '微信名称',
  `weixin_openid` varchar(63)  DEFAULT null COMMENT '微信OpenId',
  `crm_id` varchar(100)  DEFAULT null COMMENT 'CRM编号',
  `province` varchar(63) NOT NULL COMMENT '行政区域表的省ID',
  `province_name` varchar(127) NOT NULL COMMENT '行政区域表的省名称',
  `city` varchar(63) NOT NULL COMMENT '行政区域表的市ID',
  `city_name` varchar(127) NOT NULL COMMENT '行政区域表的市名称',
  `county` varchar(63) NOT NULL COMMENT '行政区域表的区县ID',
  `country_name` varchar(127) NOT NULL COMMENT '行政区域表的区县名称',
  `address_detail` varchar(127) NOT NULL DEFAULT '' COMMENT '详细收货地址',
  `area` varchar(127) DEFAULT NULL COMMENT '楼号门牌',
  `postal_code` char(6) DEFAULT NULL COMMENT '邮政编码',
  `tel` varchar(20) NOT NULL DEFAULT '' COMMENT '手机号码',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认地址',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_admin`
--

DROP TABLE IF EXISTS `litemall_admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(63) NOT NULL DEFAULT '' COMMENT '管理员名称',
  `password` varchar(63) NOT NULL DEFAULT '' COMMENT '管理员密码',
  `last_login_ip` varchar(63) DEFAULT '' COMMENT '最近一次登录IP地址',
  `last_login_time` datetime DEFAULT NULL COMMENT '最近一次登录时间',
  `avatar` varchar(255) DEFAULT '''' COMMENT '头像图片',
  `if_modify_on_sale` tinyint(1) DEFAULT '0' COMMENT '是否可以上架'
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `role_ids` varchar(127) DEFAULT '[]' COMMENT '角色列表',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_brand`
--

DROP TABLE IF EXISTS `litemall_brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_brand` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sn` varchar(50) NOT NULL DEFAULT '' COMMENT '品牌商编号',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '品牌商名称',
  `desc` varchar(255) NOT NULL DEFAULT '' COMMENT '品牌商简介',
  `pic_url` varchar(255) NOT NULL DEFAULT '' COMMENT '品牌商页的品牌商图片',
  `sort_order` tinyint(3) DEFAULT '50',
  `floor_price` decimal(10,2) DEFAULT '0.00' COMMENT '品牌商的商品低价，仅用于页面展示',
  `enabled` tinyint(1) DEFAULT '0' COMMENT '停用标记',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1046003 DEFAULT CHARSET=utf8mb4 COMMENT='品牌商表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_cart`
--

DROP TABLE IF EXISTS `litemall_cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_cart` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT '用户表的用户ID',
  `goods_id` int(11) DEFAULT NULL COMMENT '商品表的商品ID',
  `goods_sn` varchar(63) DEFAULT NULL COMMENT '商品编号',
  `goods_name` varchar(127) DEFAULT NULL COMMENT '商品名称',
  `product_id` int(11) DEFAULT NULL COMMENT '商品货品表的货品ID',
  `goods_pos_key` varchar(50) DEFAULT NULL COMMENT 'Pos店码',
  `price` decimal(10,2) DEFAULT '0.00' COMMENT '商品货品的价格',
  `number` smallint(5) DEFAULT '0' COMMENT '商品货品的数量',
  `specifications` varchar(1023) DEFAULT NULL COMMENT '商品规格值列表，采用JSON数组格式',
  `checked` tinyint(1) DEFAULT '1' COMMENT '购物车中商品是否选择状态',
  `pic_url` varchar(255) DEFAULT NULL COMMENT '商品图片或者商品货品图片',
  `com_id` int(11) DEFAULT null  COMMENT '所属公司信息ID',
  `store_id` int(11) DEFAULT null  COMMENT '所属店铺ID',
  `store_name` varchar(255)  DEFAULT '' COMMENT '所属店铺名称',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='购物车商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_category`
--

DROP TABLE IF EXISTS `litemall_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(63) NOT NULL DEFAULT '' COMMENT '类目名称',
  `keywords` varchar(1023) NOT NULL DEFAULT '' COMMENT '类目关键字，以JSON数组格式',
  `desc` varchar(255) DEFAULT '' COMMENT '类目广告语介绍',
  `pid` int(11) NOT NULL DEFAULT '0' COMMENT '父类目ID',
  `icon_url` varchar(255) DEFAULT '' COMMENT '类目图标',
  `pic_url` varchar(255) DEFAULT '' COMMENT '类目图片',
  `level` varchar(255) DEFAULT 'L1',
  `sort_order` int(11) DEFAULT null COMMENT '排序',
  `com_id` int(11) DEFAULT null  COMMENT '所属公司信息ID',
  `com_name` varchar(255)  DEFAULT '' COMMENT '所属公司信息名称',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `parent_id` (`pid`)
) ENGINE=InnoDB AUTO_INCREMENT=1036007 DEFAULT CHARSET=utf8mb4 COMMENT='类目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_collect`
--

DROP TABLE IF EXISTS `litemall_collect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_collect` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户表的用户ID',
  `value_id` int(11) NOT NULL DEFAULT '0' COMMENT '如果type=0，则是商品ID；如果type=1，则是专题ID',
  `type` tinyint(3) NOT NULL DEFAULT '0' COMMENT '收藏类型，如果type=0，则是商品ID；如果type=1，则是专题ID',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `goods_id` (`value_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_comment`
--

DROP TABLE IF EXISTS `litemall_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `value_id` int(11) NOT NULL DEFAULT '0' COMMENT '如果type=0，则是商品评论；如果是type=1，则是专题评论。',
  `type` tinyint(3) NOT NULL DEFAULT '0' COMMENT '评论类型，如果type=0，则是商品评论；如果是type=1，则是专题评论；如果type=3，则是订单商品评论。',
  `content` varchar(1023) NOT NULL COMMENT '评论内容',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户表的用户ID',
  `has_picture` tinyint(1) DEFAULT '0' COMMENT '是否含有图片',
  `pic_urls` varchar(1023) DEFAULT NULL COMMENT '图片地址列表，采用JSON数组格式',
  `star` smallint(6) DEFAULT '1' COMMENT '评分， 1-5',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `id_value` (`value_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1012 DEFAULT CHARSET=utf8mb4 COMMENT='评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_coupon`
--

DROP TABLE IF EXISTS `litemall_coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_coupon` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(63) NOT NULL COMMENT '优惠券名称',
  `desc` varchar(127) DEFAULT '' COMMENT '优惠券介绍，通常是显示优惠券使用限制文字',
  `tag` varchar(63) DEFAULT '' COMMENT '优惠券标签，例如新人专用',
  `total` int(11) NOT NULL DEFAULT '0' COMMENT '优惠券数量，如果是0，则是无限量',
  `discount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠金额，',
  `min` decimal(10,2) DEFAULT '0.00' COMMENT '最少消费金额才能使用优惠券。',
  `limit` smallint(6) DEFAULT '1' COMMENT '用户领券限制数量，如果是0，则是不限制；默认是1，限领一张.',
  `type` smallint(6) DEFAULT '0' COMMENT '优惠券赠送类型，如果是0则通用券，用户领取；如果是1，则是注册赠券；如果是2，则是优惠券码兑换；',
  `status` smallint(6) DEFAULT '0' COMMENT '优惠券状态，如果是0则是正常可用；如果是1则是过期; 如果是2则是下架。',
  `goods_type` smallint(6) DEFAULT '0' COMMENT '商品限制类型，如果0则全商品，如果是1则是类目限制，如果是2则是商品限制。',
  `goods_value` varchar(1023) DEFAULT '[]' COMMENT '商品限制值，goods_type如果是0则空集合，如果是1则是类目集合，如果是2则是商品集合。',
  `code` varchar(63) DEFAULT NULL COMMENT '优惠券兑换码',
  `time_type` smallint(6) DEFAULT '0' COMMENT '有效时间限制，如果是0，则基于领取时间的有效天数days；如果是1，则start_time和end_time是优惠券有效期；',
  `days` smallint(6) DEFAULT '0' COMMENT '基于领取时间的有效天数days。',
  `start_time` datetime DEFAULT NULL COMMENT '使用券开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '使用券截至时间',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='优惠券信息及规则表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_coupon_user`
--

DROP TABLE IF EXISTS `litemall_coupon_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_coupon_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `coupon_id` int(11) NOT NULL COMMENT '优惠券ID',
  `status` smallint(6) DEFAULT '0' COMMENT '使用状态, 如果是0则未使用；如果是1则已使用；如果是2则已过期；如果是3则已经下架；',
  `used_time` datetime DEFAULT NULL COMMENT '使用时间',
  `start_time` datetime DEFAULT NULL COMMENT '有效期开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '有效期截至时间',
  `order_id` int(11) DEFAULT NULL COMMENT '订单ID',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='优惠券用户使用表';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `litemall_pick_site`
--

DROP TABLE IF EXISTS `litemall_pick_site`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_pick_site` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_name` varchar(255) DEFAULT NULL COMMENT '提货点名称',
  `site_pos` varchar(255) DEFAULT NULL COMMENT '提货点位置',
  `site_tel` varchar(50) DEFAULT NULL COMMENT '提货点联系电话',
  `site_time` varchar(255) DEFAULT NULL COMMENT '提货时间',
  `site_time` varchar(255) DEFAULT NULL COMMENT '提货时间',
  `site_pick_week` varchar(1023) DEFAULT NULL COMMENT '店铺提货星期几',
  `site_pick_time` varchar(1023) DEFAULT NULL COMMENT '店铺提货时间',
  `ordernumber` tinyint(3) DEFAULT NULL COMMENT '排序',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `enabled` tinyint(1) DEFAULT '0' COMMENT '是否启用',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='线下_提货点';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_feedback`
--

DROP TABLE IF EXISTS `litemall_feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_feedback` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户表的用户ID',
  `username` varchar(63) NOT NULL DEFAULT '' COMMENT '用户名称',
  `mobile` varchar(20) NOT NULL DEFAULT '' COMMENT '手机号',
  `feed_type` varchar(63) NOT NULL DEFAULT '' COMMENT '反馈类型',
  `content` varchar(1023) NOT NULL COMMENT '反馈内容',
  `status` int(3) NOT NULL DEFAULT '0' COMMENT '状态',
  `has_picture` tinyint(1) DEFAULT '0' COMMENT '是否含有图片',
  `pic_urls` varchar(1023) DEFAULT NULL COMMENT '图片地址列表，采用JSON数组格式',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `id_value` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='意见反馈表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_footprint`
--

DROP TABLE IF EXISTS `litemall_footprint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_footprint` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户表的用户ID',
  `wx_nickname` varchar(63)  DEFAULT null COMMENT '微信名称',
  `weixin_openid` varchar(63)  DEFAULT null COMMENT '微信OpenId',
  `crm_id` varchar(100)  DEFAULT null COMMENT 'CRM编号',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '浏览商品ID',
  `goods_sn` varchar(63)  DEFAULT null COMMENT '浏览商品名称',
  `goods_name` varchar(127)  DEFAULT null COMMENT '浏览商品名称',
  `dajiapai_id` int(11) DEFAULT null  COMMENT '浏览大家拍规则ID',
  `zhuanchang_id` int(11) DEFAULT null  COMMENT '浏览专场拍规则ID',
  `zhuanchang_mx_id` int(11) DEFAULT null COMMENT '浏览专场拍列表商品明细ID',
  `private_make_id` int(11) DEFAULT null COMMENT '私人定制规则ID',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户浏览足迹表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_goods`
--

DROP TABLE IF EXISTS `litemall_goods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_goods` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_sn` varchar(63) NOT NULL DEFAULT '' COMMENT '商品编号',
  `goods_barcode` varchar(50) NOT NULL DEFAULT '' COMMENT '商品条码',
  `name` varchar(127) NOT NULL DEFAULT '' COMMENT '商品名称',
  `category_id` int(11) DEFAULT null COMMENT '商品所属电商类目ID',
  `category_code` varchar(255) DEFAULT null COMMENT '所属电商类目编码',
  `category_name` varchar(1023) DEFAULT null COMMENT '所属电商类目名称',
  `dajiapai_category_id` int(11) DEFAULT null COMMENT '所属大家拍分类ID',
  `dajiapai_category_code` varchar(255) DEFAULT null COMMENT '所属大家拍分类目编码',
  `dajiapai_category_name` varchar(255) DEFAULT null COMMENT '所属大家拍分类目名称',
  `private_category_id` int(11) DEFAULT null COMMENT '所属私人定制分类ID',
  `private_category_code` varchar(255) DEFAULT null COMMENT '所属私人定制类目编码',
  `private_category_name` varchar(255) DEFAULT null COMMENT '所属私人定制类目名称',
  `brand_id` int(11) DEFAULT null  COMMENT '所属品牌ID',
  `brand_name` varchar(255) DEFAULT null COMMENT '所属品牌名称',
  `gallery_big` varchar(1023) DEFAULT NULL COMMENT '商品宣传图片大图列表，采用JSON数组格式',
  `gallery` varchar(1023) DEFAULT NULL COMMENT '商品宣传图片列表，采用JSON数组格式',
  `keywords` varchar(255) DEFAULT '' COMMENT '商品关键字，采用逗号间隔',
  `brief` varchar(255) DEFAULT '' COMMENT '商品简介',
  `is_on_sale` tinyint(1) DEFAULT '1' COMMENT '是否上架',
  `sort_order` smallint(4) DEFAULT '100',
  `pic_url` varchar(255) DEFAULT NULL COMMENT '商品页面商品图片',
  `share_url` varchar(255) DEFAULT NULL COMMENT '商品分享朋友圈图片',
  `is_new` tinyint(1) DEFAULT '0' COMMENT '是否新品首发，如果设置则可以在新品首发页面展示',
  `is_hot` tinyint(1) DEFAULT '0' COMMENT '是否人气推荐，如果设置则可以在人气推荐页面展示',
  `unit` varchar(31) DEFAULT null COMMENT '商品单位，例如件、盒',
  `counter_price` decimal(10,2) DEFAULT '0.00' COMMENT '原价',
  `retail_price` decimal(10,2) DEFAULT '0.00' COMMENT '零售价格',
  `price_desc` varchar(255) DEFAULT NULL COMMENT '描述价格',
  `default_product_id` int(11)  DEFAULT null COMMENT '默认商品规格Id',
  `detail` text COMMENT '商品详细介绍，是富文本格式',
  `com_id` int(11) DEFAULT null  COMMENT '所属公司信息ID',
  `com_name` varchar(255)  DEFAULT '' COMMENT '所属公司信息名称',
  `store_id` int(11) DEFAULT null  COMMENT '预约店铺ID',
  `store_name` varchar(255)  DEFAULT '' COMMENT '预约店铺名称',
  `if_xuni` tinyint(1) DEFAULT '0' COMMENT '是否虚拟商品',
  `if_ticket` tinyint(1) DEFAULT '0' COMMENT '是否CRM购券 0-否 1-是',
  `if_list_goods` tinyint(1) DEFAULT '1' COMMENT '是否列表商品 0-否 1-是 默认1',
  if_use_coupon tinyint(1) default 0 COMMENT '是否可用优惠券 0-否 1-是',
  if_use_bonus tinyint(1) default 0 COMMENT '是否可用积分 0-否 1-是',
  if_store_bigger_zero tinyint(1) default 0 COMMENT '商品下可能用多个规格型号，其中任一规格型号库存大于零则为1，做为一个是否可销售的标志',
  `used_range` varchar(255) DEFAULT '电商专用' COMMENT '商品应用类型:  电商专用  拍卖专用  全场通用',
  `author_id` int(11) DEFAULT null  COMMENT '出品人ID',
  `author_name` varchar(255)  DEFAULT null COMMENT '出品人名称',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `goods_sn` (`goods_sn`),
  KEY `cat_id` (`category_id`),
  KEY `brand_id` (`brand_id`),
  KEY `sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=1181004 DEFAULT CHARSET=utf8mb4 COMMENT='商品基本信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_goods_store`
--

DROP TABLE IF EXISTS `litemall_goods_store`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_goods_store` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品表的规格型号商品ID',
  `store_id` int(11) DEFAULT NULL COMMENT '店铺ID',
  `store_name` varchar(255) DEFAULT '' COMMENT '店铺名称',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=877 DEFAULT CHARSET=utf8mb4 COMMENT='商品_店铺表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_goods_attribute`
--

DROP TABLE IF EXISTS `litemall_goods_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_goods_attribute` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品表的商品ID',
  `attribute` varchar(255) NOT NULL COMMENT '商品参数名称',
  `value` varchar(255) NOT NULL COMMENT '商品参数值',
  `ordernumber` int(11)  DEFAULT NULL COMMENT '排序',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `goods_id` (`goods_id`)
) ENGINE=InnoDB AUTO_INCREMENT=877 DEFAULT CHARSET=utf8mb4 COMMENT='商品参数表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_goods_product`
--

DROP TABLE IF EXISTS `litemall_goods_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_goods_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品表的商品ID',
  `specifications` varchar(1023) NOT NULL COMMENT '商品规格值列表，采用JSON数组格式',
  `specification_ids` varchar(1023) NOT NULL COMMENT '商品规格ID列表，采用JSON数组格式',
  `price_method` varchar(20) DEFAULT '固定单价' COMMENT '定价方式',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '商品货品价格',
  `price_desc` varchar(255) DEFAULT NULL COMMENT '描述价格',
  `number` int(11) NOT NULL DEFAULT '0' COMMENT '商品货品数量',
  `url` varchar(125) DEFAULT NULL COMMENT '商品货品图片',
  `goods_pos_key` varchar(50) DEFAULT NULL COMMENT 'Pos店码',
  `min_storenum` int(11) DEFAULT  null COMMENT '最低库存数量',
  `yuyue_number` int(11) DEFAULT  null COMMENT '预订数量',
  `remain_number` int(11) DEFAULT  null COMMENT '可用库存',
  `store_ids` varchar(1023) DEFAULT  null COMMENT '可预约店铺Id',
  `store_names` varchar(1023) DEFAULT  null COMMENT '可预约店铺名称',
  `return_type` varchar(50) DEFAULT NULL COMMENT '返回当前商品的类型 正常商品 秒杀商品 团购商品 会员商品 虚拟商品',
  `return_rule_id` int(11) DEFAULT NULL COMMENT '返回当前商品的活动规则Id',
  `return_rule_number` int(11) DEFAULT  null COMMENT '返回商品类型的可用数量',
  `return_rule_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '返回商品类型的单价',
  `return_rule_source_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '返回商品类型的原价',
  `return_allow_buy_goods` tinyint(1) DEFAULT '0' COMMENT '是否每次只能购买一件单品',
  `return_allow_order` tinyint(1) DEFAULT '0' COMMENT '是否允许创建订单(如果单人单次，已经创建后则不可创建)',
  `return_groupon_info` varchar(255) DEFAULT NULL COMMENT '返回团购参团数量/团购最低数量',
  `return_if_only_user_bonus` tinyint(1) DEFAULT '0' COMMENT '会员商品只支持积分购买 0-否 1-是',
  `return_if_use_coupon` tinyint(1) DEFAULT '0' COMMENT '是否允许使用优惠券 0-否 1-是',
  `return_if_use_bonus` tinyint(1) DEFAULT '0' COMMENT '是否允许使用积分 0-否 1-是',
  `seckill_end_date` datetime DEFAULT NULL COMMENT '秒杀截至时间',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=251 DEFAULT CHARSET=utf8mb4 COMMENT='商品货品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_goods_specification`
--

DROP TABLE IF EXISTS `litemall_goods_specification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_goods_specification` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品表的商品ID',
  `specification` varchar(255) NOT NULL DEFAULT '' COMMENT '商品规格名称',
  `value` varchar(255) NOT NULL DEFAULT '' COMMENT '商品规格值',
  `pic_url` varchar(255) NOT NULL DEFAULT '' COMMENT '商品规格图片',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `goods_id` (`goods_id`)
) ENGINE=InnoDB AUTO_INCREMENT=250 DEFAULT CHARSET=utf8mb4 COMMENT='商品规格表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_groupon_order`
--

DROP TABLE IF EXISTS `litemall_groupon_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_groupon_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `order_id` int(11) NOT NULL COMMENT '关联的订单ID',
  `rules_id` int(11) NOT NULL COMMENT '团购活动规则ID，关联litemall_groupon_rules表ID字段',
  `add_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `litemall_groupon_order`
--

DROP TABLE IF EXISTS `litemall_groupon_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_groupon_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `order_id` int(11) NOT NULL COMMENT '关联的订单ID',
  `rules_id` int(11) NOT NULL COMMENT '团购活动规则ID，关联litemall_groupon_rules表ID字段',
  `add_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_groupon_rules`
--

DROP TABLE IF EXISTS `litemall_groupon_rules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_groupon_rules` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '活动名称',
  com_id int(11) default 0 COMMENT '公司信息ID',
  com_name varchar(255) default '' COMMENT '公司信息',
  `goods_id` int(11) NOT NULL COMMENT '商品表的商品ID',
  `goods_sn` varchar(63) NOT NULL default '' COMMENT '商品编号',
  `goods_name` varchar(255) NOT NULL default '' COMMENT '商品名称',
   goods_product_id int(11) default 0 COMMENT '商品货品ID',
   goods_product_specifications varchar(255) default '' COMMENT '商品规格值列表，采用JSON数组格式',
  `source_price` decimal(10,2) NOT NULL COMMENT '商品原价',
  `groupon_price` decimal(10,2) NOT NULL COMMENT '团购单价',
  `group_minperson` int(11) NOT NULL COMMENT '团购最低人数',
  `group_max_store` int(11) NOT NULL COMMENT '活动库存',
  `group_remain_store` int(11) NOT NULL COMMENT '剩余库存',
  `begin_date` datetime DEFAULT NULL COMMENT '开始时间',
  `expire_time` datetime DEFAULT NULL COMMENT '团购过期时间',
  `expire_flag` tinyint(1) DEFAULT '0' COMMENT '过期标志 0-未过期 1-过期',
  `remark` varchar(255) NOT NULL default '' COMMENT '执行备注',
  pic_url varchar(255) default '' COMMENT '活动图片路径',
  free_post tinyint(1) default 0 COMMENT '是否免邮 0-否 1-是',
  if_use_coupon tinyint(1) default 0 COMMENT '是否可用优惠券 0-否 1-是',
  if_use_bonus tinyint(1) default 0 COMMENT '是否可用积分 0-否 1-是',
  `add_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_issue`
--

DROP TABLE IF EXISTS `litemall_issue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_issue` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `question` varchar(255) DEFAULT NULL COMMENT '问题标题',
  `answer` varchar(255) DEFAULT NULL COMMENT '问题答案',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='常见问题表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_keyword`
--

DROP TABLE IF EXISTS `litemall_keyword`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_keyword` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `keyword` varchar(127) NOT NULL DEFAULT '' COMMENT '关键字',
  `url` varchar(255) NOT NULL DEFAULT '' COMMENT '关键字的跳转链接',
  `is_hot` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是热门关键字',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是默认关键字',
  `sort_order` int(11) NOT NULL DEFAULT '100' COMMENT '排序',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT='关键字表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_log`
--

DROP TABLE IF EXISTS `litemall_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `admin` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '管理员',
  `ip` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '管理员地址',
  `type` int(11) DEFAULT NULL COMMENT '操作分类',
  `action` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作动作',
  `status` tinyint(1) DEFAULT NULL COMMENT '操作状态',
  `result` varchar(127) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作结果，或者成功消息，或者失败消息',
  `comment` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '补充信息',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_userinfo_def`
--

DROP TABLE IF EXISTS `litemall_userinfo_def`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_userinfo_def` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type_code` varchar(50) DEFAULT NULL COMMENT '消息类型ID',
  `type_name` varchar(255)  DEFAULT NULL COMMENT '消息类型名称',
  `title` varchar(255) DEFAULT NULL COMMENT '消息主题',
  `content` varchar(1023) DEFAULT NULL COMMENT '消息内容',
  `begin_date` datetime  DEFAULT NULL COMMENT '消息生效时间',
  `end_date` datetime  DEFAULT NULL COMMENT '消息失效时间',
  `expire_flag` tinyint(1) DEFAULT '0' COMMENT '消息过期标志',
  `web_hint` tinyint(1) DEFAULT '0' COMMENT '站内提醒',
  `sms_hint` tinyint(1) DEFAULT '0' DEFAULT NULL COMMENT '短信标志',
  `mail_hint` tinyint(1) DEFAULT '0' COMMENT '邮件标志',
  `pop_hint` tinyint(1) DEFAULT '0' COMMENT '弹窗提醒',
  `deal_web_link` varchar(255) DEFAULT NULL COMMENT '处理链接网址',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='前端消息表';
/*!40101 SET character_set_client = @saved_cs_client */;



--
-- Table structure for table `litemall_userinfo_pub`
--

DROP TABLE IF EXISTS `litemall_userinfo_pub`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_userinfo_pub` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `info_id` int(11) DEFAULT NULL COMMENT '消息ID',
  `if_all_visit`  tinyint(1)  DEFAULT 0 COMMENT '是否全部用户',
  `if_all_user` tinyint(1) DEFAULT 0 COMMENT '是否全部会员用户',
  `user_group_code1` varchar(50) DEFAULT NULL COMMENT '会员分组编码1',
  `user_group_name1` varchar(255) DEFAULT '' COMMENT '会员分组名称1',
  `user_group_code2` varchar(50) DEFAULT NULL COMMENT '会员分组编码2',
  `user_group_name2` varchar(255)  DEFAULT '' COMMENT '会员分组名称2',
  `user_id` int(11)  DEFAULT NULL COMMENT '会员ID',
  `user_name` varchar(255)  DEFAULT '' COMMENT '会员名称',
  `if_viewed` tinyint(1) DEFAULT '0' COMMENT '是否查看',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='前端消息表_通知用户表';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `litemall_opadmin_def`
--

DROP TABLE IF EXISTS `litemall_opadmin_def`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_opadmin_def` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type_code` varchar(50) DEFAULT NULL COMMENT '消息类型ID',
  `type_name` varchar(255)  DEFAULT NULL COMMENT '消息类型名称',
  `title` varchar(255) DEFAULT NULL COMMENT '消息主题',
  `content` varchar(1023) DEFAULT NULL COMMENT '消息内容',
  `begin_date` datetime  DEFAULT NULL COMMENT '消息生效时间',
  `end_date` datetime  DEFAULT NULL COMMENT '消息失效时间',
  `expire_flag` tinyint(1) DEFAULT '0' COMMENT '消息过期标志',
  `web_hint` tinyint(1) DEFAULT '0' COMMENT '站内提醒',
  `sms_hint` tinyint(1) DEFAULT '0' DEFAULT NULL COMMENT '短信标志',
  `mail_hint` tinyint(1) DEFAULT '0' COMMENT '邮件标志',
  `pop_hint` tinyint(1) DEFAULT '0' COMMENT '弹窗提醒',
  `deal_web_link` varchar(255) DEFAULT NULL COMMENT '处理链接网址',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后端消息_定义表';
/*!40101 SET character_set_client = @saved_cs_client */;



--
-- Table structure for table `litemall_opadmin_pub`
--

DROP TABLE IF EXISTS `litemall_opadmin_pub`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_opadmin_pub` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `info_id` int(11) DEFAULT NULL COMMENT '消息ID',
  `user_id` int(11)  DEFAULT NULL COMMENT '管理员ID',
  `user_name` varchar(255)  DEFAULT '' COMMENT '管理员名称',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后端消息_通知用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_opadmin_info`
--

DROP TABLE IF EXISTS `litemall_opadmin_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_opadmin_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `opadmin_id` int(11) DEFAULT NULL COMMENT '操作用户ID',
  `opadmin_name`  varchar(255) DEFAULT NULL COMMENT '操作用户名称',
  `info_id` int(11) DEFAULT NULL COMMENT '定义消息ID',
  `type_code` varchar(50) DEFAULT NULL COMMENT '消息类型编码',
  `type_name` varchar(255)  DEFAULT NULL COMMENT '消息类型名称',
  `source_code` varchar(50) DEFAULT NULL COMMENT '消息来源编码',
  `source_name` varchar(255) DEFAULT NULL COMMENT '消息来源名称',
  `title` varchar(255) DEFAULT NULL COMMENT '消息主题',
  `content` varchar(1023) DEFAULT NULL COMMENT '消息内容',
  `web_hint` tinyint(1) DEFAULT '0' COMMENT '站内提醒',
  `sms_hint` tinyint(1) DEFAULT '0' DEFAULT NULL COMMENT '短信标志',
  `mail_hint` tinyint(1) DEFAULT '0' COMMENT '邮件标志',
  `pop_hint` tinyint(1) DEFAULT '0' COMMENT '弹窗提醒',
  `display_flag` tinyint(1) DEFAULT '0' COMMENT '停显标志',
  `if_viewed` tinyint(1) DEFAULT '0' COMMENT '查看标志',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后端消息表';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `litemall_user_order_info`
--

DROP TABLE IF EXISTS `litemall_user_order_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_user_order_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) DEFAULT NULL COMMENT '订单ID',
  `user_id` int(11)  DEFAULT NULL COMMENT '会员ID',
  `status_code` int(11) DEFAULT NULL COMMENT '订单状态码',
  `status_name` varchar(255) DEFAULT NULL COMMENT '订单状态名称',
  `happen_date` varchar(255) DEFAULT NULL COMMENT '发生时间',
  `content` varchar(255) DEFAULT NULL COMMENT '消息内容',
  `web_hint` tinyint(1) DEFAULT '0' COMMENT '站内提醒',
  `sms_hint` tinyint(1) DEFAULT '0' DEFAULT NULL COMMENT '短信标志',
  `mail_hint` tinyint(1) DEFAULT '0' COMMENT '邮件标志',
  `if_send_user` tinyint(1) DEFAULT '0' COMMENT '是否通知客户',
  `if_viewed` tinyint(1) DEFAULT '0' COMMENT '查看标志',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息推送_前端_订单消息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_interface_monitor`
--

DROP TABLE IF EXISTS `litemall_interface_monitor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_interface_monitor` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `log_ip` varchar(255) DEFAULT '' COMMENT '来源IP',
  `log_source_code` varchar(50) DEFAULT null COMMENT '数据来源类型代码',
  `log_source`  varchar(63)  DEFAULT '' COMMENT '数据来源类型',
  `log_direction_code` varchar(50) DEFAULT null COMMENT '接口调用类型代码',
  `log_direction` varchar(63) DEFAULT '' COMMENT '接口调用类型',
  `log_type_code` varchar(50) DEFAULT  null COMMENT '日志类型代码',
  `log_type` varchar(255) DEFAULT '' COMMENT '日志类型',
  `log_content` varchar(255) DEFAULT  '' COMMENT '同步内容描述',
  `log_state_desc` varchar(255) DEFAULT  '' COMMENT '传输状态描述',
  `log_start_time` datetime DEFAULT null COMMENT '同步起始时间',
  `log_end_time` datetime DEFAULT null COMMENT '同步截止时间',
  `log_sync_type` varchar(63) DEFAULT '' COMMENT '同步方式',
  `log_insert_sum` int(11) DEFAULT null COMMENT '本次插入N条',
  `log_update_sum` int(11) DEFAULT null COMMENT '本次更新N条',
  `log_deal_sum` int(11) DEFAULT null COMMENT '本次传输总数N条',
  `log_right_sum` int(11) DEFAULT null COMMENT '本次传输正确N条',
  `log_error_sum` int(11) DEFAULT null COMMENT '本次传输失败N条',
  `error_date` datetime DEFAULT null COMMENT '错误发生时间',
  `error_id_desc` varchar(255) DEFAULT '' COMMENT '错误主键描述',
  `error_msg` text DEFAULT '' COMMENT '错误消息',
  `error_flag` tinyint(1) DEFAULT '0' COMMENT '错误标识',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据传输日志表';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `litemall_order`
--

DROP TABLE IF EXISTS `litemall_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT '用户表的用户ID',
  `user_name` varchar(255) DEFAULT NULL COMMENT '用户名称',
  `user_nickname` varchar(255) DEFAULT NULL COMMENT '用户昵称',
  `user_phone` varchar(255) DEFAULT NULL COMMENT '用户电话',
  `crm_id` varchar(50) DEFAULT NULL COMMENT '会员编号',
  `crm_name` varchar(50) DEFAULT NULL COMMENT '会员姓名',
  `order_sn` varchar(63) DEFAULT NULL COMMENT '订单编号',
  `order_status` smallint(6) DEFAULT NULL COMMENT '订单状态',
  `order_status_name` varchar(255) DEFAULT NULL COMMENT '订单状态名称',
  `consignee` varchar(63) NOT NULL DEFAULT '' COMMENT '收货人名称',
  `mobile` varchar(63) NOT NULL DEFAULT '' COMMENT '收货人手机号',
  `address` varchar(127) NOT NULL DEFAULT '' COMMENT '收货具体地址',
  `message` varchar(512) NOT NULL DEFAULT '' COMMENT '用户订单留言',
  `goods_price` decimal(10,2) DEFAULT NULL COMMENT '商品总费用',
  `freight_price` decimal(10,2) DEFAULT NULL COMMENT '配送费用',
  `coupon_price` decimal(10,2) DEFAULT NULL COMMENT '优惠券减免',
  `integral_bonus` decimal(10,2) DEFAULT NULL COMMENT '用户使用了多少积分',
  `integral_price` decimal(10,2) DEFAULT NULL COMMENT '用户积分减免',
  `groupon_price` decimal(10,2) DEFAULT NULL COMMENT '团购优惠价减免',
  `order_price` decimal(10,2) DEFAULT NULL COMMENT '订单费用， = goods_price + freight_price - coupon_price',
  `actual_price` decimal(10,2) DEFAULT NULL COMMENT '实付费用， = order_price - integral_price',
  `pay_method` int(11) DEFAULT NULL COMMENT '支付方式 1-支付宝 2-微信',
  `pay_method_name` varchar(512)  DEFAULT NULL COMMENT '支付方式名称 1-支付宝 2-微信',
  `pay_id` varchar(63) DEFAULT NULL COMMENT '微信付款编号',
  `pay_time` datetime DEFAULT NULL COMMENT '微信付款时间',
  `ship_sn` varchar(63) DEFAULT NULL COMMENT '发货编号',
  `ship_channel` varchar(63) DEFAULT NULL COMMENT '发货快递公司',
  `ship_time` datetime DEFAULT NULL COMMENT '发货开始时间',
  `confirm_time` datetime DEFAULT NULL COMMENT '用户确认收货时间',
  `comments` smallint(6) DEFAULT '0' COMMENT '待评价订单商品数量',
  `end_time` datetime DEFAULT NULL COMMENT '订单关闭时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '订单取消时间',
  `order_status_refund` smallint(6) DEFAULT NULL COMMENT '申请退款时订单状态',
  `return_time` datetime DEFAULT NULL COMMENT '申请退款时间',
  `return_frontreason` varchar(255) DEFAULT NULL COMMENT '前台填写退款理由',
  `return_reason` varchar(255) DEFAULT NULL COMMENT '后台批准退款理由',
  `return_op` varchar(50) DEFAULT NULL COMMENT '退款操作人',
  `return_type` varchar(50) DEFAULT NULL COMMENT '退款类型',
  `return_part_remark` varchar(255) DEFAULT NULL COMMENT '部分退款备注',
  `return_goods_time` datetime DEFAULT NULL COMMENT '收到退货时间',
  `return_pay_time` datetime DEFAULT NULL COMMENT '支付退款时间',
  `type_code` varchar(50) DEFAULT NULL COMMENT '订单类型编码',
  `type_name` varchar(255) DEFAULT NULL COMMENT '订单类型名称',
  `mall_order_type_code` varchar(50) DEFAULT NULL COMMENT '电商订单类型编码如果是电商订单，又分成:10、正常单 20、秒杀单 30、团购单 ',
  `mall_order_type_name` varchar(255) DEFAULT NULL COMMENT '电商订单类型名称',
  `mall_order_if_xuni` tinyint(1) DEFAULT '0' COMMENT '是否虚拟商品订单',
  `mall_order_if_ticket` tinyint(1) DEFAULT '0' COMMENT '是否购券订单',
  `mall_order_if_huiyuan` tinyint(1) DEFAULT '0' COMMENT '是否会员商品订单',
  `source_code` varchar(50) DEFAULT NULL COMMENT '订单来源编码',
  `source_name` varchar(255) DEFAULT NULL COMMENT '订单来源名称',
  `yuyue_com_id` int(11) DEFAULT NULL COMMENT '预约机场Id',
  `yuyue_com_name` varchar(255) DEFAULT NULL COMMENT '预约机场名称',
  `yuyue_com_hangzhanlou` varchar(255) DEFAULT NULL COMMENT '预约航站楼',
  `yuyue_store_id` int(11) DEFAULT NULL COMMENT '预约店铺',
  `yuyue_store_pos_key` varchar(50) DEFAULT NULL COMMENT '预约店铺编码',
  `yuyue_store_name` varchar(255) DEFAULT NULL COMMENT '预约店铺名称',
  `yuyue_store_phone` varchar(255) DEFAULT NULL COMMENT '预约店铺电话',
  `yuyue_fetch_time` datetime DEFAULT NULL COMMENT '预约取货时间',
  `yuyue_fetch_expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `yuyue_status_code` smallint(6) DEFAULT NULL COMMENT '预约状态',
  `yuyue_status_name` varchar(255) DEFAULT NULL COMMENT '预约状态名称',
  `yuyue_hint_first` tinyint(1) DEFAULT '0' COMMENT '预约首提醒标志',
  `yuyue_hint_second` tinyint(1) DEFAULT '0' COMMENT '预约复提醒标志',

  `private_item_name` varchar(255) DEFAULT NULL COMMENT '私人定制品项名称',
  `private_deliver_date` datetime DEFAULT NULL COMMENT '私人定制交付时间',
  `private_content_desc` varchar(1023) DEFAULT NULL COMMENT '私人定制内容描述',
  `private_remark` varchar(1023) DEFAULT NULL COMMENT '私人定制特别备注',
  `private_upload_pic` varchar(255) DEFAULT NULL COMMENT '私人定制上传图',

  `auction_type` varchar(255) DEFAULT NULL COMMENT '专场拍还是大家拍类型',
  `zhuanchang_offer_id` int(11) DEFAULT NULL COMMENT '关联专场拍出价ID',
  `dajiapai_offer_id` int(11) DEFAULT NULL COMMENT '关联大家拍出价ID',
  `private_make_rule_id` int(11) DEFAULT NULL COMMENT '关联私人定制规则ID',

  `send_way` varchar(50) DEFAULT NULL COMMENT '运输方式',
  `pick_site_id` int(11) DEFAULT NULL COMMENT '自提货点ID',
  `pick_site_name` varchar(255) DEFAULT NULL COMMENT '自提货点名称',
  `pick_time` datetime DEFAULT NULL COMMENT '自提货日期时间',
  `pick_person` varchar(50) DEFAULT NULL COMMENT '取货人真实姓名',
  `pick_mobile` varchar(50) DEFAULT NULL COMMENT '取货人手机号码',

  `admin_allow_refund` tinyint(1) DEFAULT '0' COMMENT '对于有些订单，如果用户收货后要求退货，则由系统管理员后台强行设置允许退货，由客户在手机端完成退款申请，再由管理员完成退款支付',
  `if_fapiao` tinyint(1) DEFAULT '0' COMMENT '是否开票',
  `fapiao_status` varchar(50) DEFAULT NULL COMMENT '开票状态',
  `if_send_crm` tinyint(1) DEFAULT '0' COMMENT '是否已经同步至CRM',

  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_order_goods`
--

DROP TABLE IF EXISTS `litemall_order_goods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_order_goods` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11)  DEFAULT NULL COMMENT '订单表的订单ID',
  `goods_id` int(11) DEFAULT NULL COMMENT '商品表的商品ID',
  `goods_name` varchar(127) DEFAULT '' COMMENT '商品名称',
  `goods_sn` varchar(63)  DEFAULT '' COMMENT '商品编号',
  `product_id` int(11)  NOT NULL COMMENT '商品货品表的货品ID',
  `goods_pos_key` varchar(50) DEFAULT NULL COMMENT 'Pos店码',
  `number` smallint(5)  DEFAULT NULL COMMENT '商品货品的购买数量',
  `price` decimal(10,2) DEFAULT NULL COMMENT '商品货品的售价',
  `specifications` varchar(1023) NOT NULL COMMENT '商品货品的规格列表',
  `pic_url` varchar(255) NOT NULL DEFAULT '' COMMENT '商品货品图片或者商品图片',
  `comment` int(11) DEFAULT '0' COMMENT '订单商品评论，如果是-1，则超期不能评价；如果是0，则可以评价；如果其他值，则是comment表里面的评论ID。',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `goods_id` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `litemall_order_tickets`
--

DROP TABLE IF EXISTS `litemall_order_tickets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_order_tickets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11)  DEFAULT NULL COMMENT '订单表的订单ID',
  `ticket_id` varchar(50) DEFAULT NULL COMMENT 'CRM中的优惠券Id',
  `ticket_desci` varchar(100) DEFAULT NULL COMMENT '电子券描述',
  `serial_id` varchar(50)  DEFAULT NULL COMMENT '兑换码',
  `ticket_type` varchar(1)  DEFAULT NULL COMMENT '电子券种类',
  `min_amt` decimal(10,2)  DEFAULT NULL COMMENT '最小能抵多少金额',
  `increase_amt` decimal(10,2) DEFAULT NULL COMMENT '增加多少钱能用这张券',
  `max_amt` decimal(10,2) DEFAULT NULL COMMENT '最多能抵多少金额',
  `discount` decimal(10,2) DEFAULT NULL COMMENT '折扣比例',
  `effect_date` varchar(50) DEFAULT NULL COMMENT '生效日期',
  `expiry_date` varchar(50) DEFAULT NULL COMMENT '过期日期',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `status` int(11) DEFAULT NULL COMMENT '状态',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单优惠券表';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `litemall_fapiao_info`
--

DROP TABLE IF EXISTS `litemall_fapiao_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_fapiao_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `name` varchar(255) NOT NULL COMMENT '单位名称',
  `taxno` varchar(50) NOT NULL COMMENT '纳税人识别号',
  `address` varchar(255) NOT NULL COMMENT '注册地址',
  `telphone` varchar(50) NOT NULL COMMENT '注册电话',
  `bank` varchar(255) NOT NULL COMMENT '开户银行',
  `bank_no` varchar(50) NOT NULL COMMENT '银行帐号',
  `is_default` tinyint(1)  DEFAULT '0'  COMMENT '是否默认开票信息',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COMMENT='开票单位信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_oder_fapiao`
--

DROP TABLE IF EXISTS `litemall_order_fapiao`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_order_fapiao` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL COMMENT '订单ID',
  `fapiao_type` varchar(50) DEFAULT NULL COMMENT '发票类型 个人 单位',
  `name` varchar(255) DEFAULT NULL COMMENT '单位名称',
  `taxno` varchar(50) DEFAULT NULL COMMENT '纳税人识别号',
  `address` varchar(255) DEFAULT NULL COMMENT '注册地址',
  `telphone` varchar(50) DEFAULT NULL COMMENT '注册电话',
  `bank` varchar(255) DEFAULT NULL COMMENT '开户银行',
  `bank_no` varchar(50) DEFAULT NULL COMMENT '银行帐号',
  `money` decimal(10,2)  DEFAULT '0'  COMMENT '开票金额',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COMMENT='订单开票表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_permission`
--

DROP TABLE IF EXISTS `litemall_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) DEFAULT NULL COMMENT '角色ID',
  `permission` varchar(63) DEFAULT NULL COMMENT '权限',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COMMENT='权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_region`
--

DROP TABLE IF EXISTS `litemall_region`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_region` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pid` int(11) NOT NULL DEFAULT '0' COMMENT '行政区域父ID，例如区县的pid指向市，市的pid指向省，省的pid则是0',
  `name` varchar(120) NOT NULL DEFAULT '' COMMENT '行政区域名称',
  `type` tinyint(3) NOT NULL DEFAULT '0' COMMENT '行政区域类型，如如1则是省， 如果是2则是市，如果是3则是区县',
  `code` int(11) NOT NULL DEFAULT '0' COMMENT '行政区域编码',
  PRIMARY KEY (`id`),
  KEY `parent_id` (`pid`),
  KEY `region_type` (`type`),
  KEY `agency_id` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=3232 DEFAULT CHARSET=utf8mb4 COMMENT='行政区域表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_role`
--

DROP TABLE IF EXISTS `litemall_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(63) NOT NULL COMMENT '角色名称',
  `desc` varchar(1023) DEFAULT NULL COMMENT '角色描述',
  `enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_menu`
--

DROP TABLE IF EXISTS `litemall_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(255) NOT NULL COMMENT '编码',
  `name` varchar(255) DEFAULT NULL COMMENT '分类名称',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `href` varchar(1023) DEFAULT NULL COMMENT '链接路径',
  `path` varchar(1023) DEFAULT NULL COMMENT '链接全路径',
  `level` int(11) DEFAULT NULL COMMENT '当前层级',
  `is_show` tinyint(1) DEFAULT 1 COMMENT '是否显示 0-不显示 1-显示',
  `parent_id` int(11) DEFAULT 0 COMMENT '父ID',
  `remarks` varchar(255) DEFAULT NULL COMMENT '描述备注',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `tenant_id` int(11) default NULL  COMMENT '租户ID',
  `user_create_id` int(11) DEFAULT NULL COMMENT '外部创建用户Id',
  `user_modify_id` int(11) DEFAULT NULL COMMENT '外部修改用户Id',
  `op_create_id` int(11) DEFAULT NULL COMMENT '内部后台创建用户Id',
  `op_modify_id` int(11) DEFAULT NULL COMMENT '内部后台修改用户Id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_role_menu`
--

DROP TABLE IF EXISTS `litemall_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_role_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  `menu_id` int(11) DEFAULT NULL COMMENT '菜单ID',
  `menu_code` varchar(255) DEFAULT NULL COMMENT '菜单Code',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='菜单_角色权限表';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `litemall_search_history`
--

DROP TABLE IF EXISTS `litemall_search_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_search_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户表的用户ID',
  `wx_nickname` varchar(63)  DEFAULT null COMMENT '微信名称',
  `weixin_openid` varchar(63)  DEFAULT null COMMENT '微信OpenId',
  `crm_id` varchar(100)  DEFAULT null COMMENT 'CRM编号',
  `keyword` varchar(63) NOT NULL COMMENT '搜索关键字',
  `from` varchar(63) NOT NULL DEFAULT '' COMMENT '搜索来源，如pc、wx、app',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索历史表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_storage`
--

DROP TABLE IF EXISTS `litemall_storage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_storage` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(63) NOT NULL COMMENT '文件的唯一索引',
  `name` varchar(255) NOT NULL COMMENT '文件名',
  `type` varchar(20) NOT NULL COMMENT '文件类型',
  `size` int(11) NOT NULL COMMENT '文件大小',
  `url` varchar(255) DEFAULT NULL COMMENT '文件访问链接',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件存储表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_system`
--

DROP TABLE IF EXISTS `litemall_system`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_system` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key_name` varchar(255) NOT NULL COMMENT '系统配置名',
  `key_value` varchar(255) NOT NULL COMMENT '系统配置值',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='系统配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_topic`
--

DROP TABLE IF EXISTS `litemall_topic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_topic` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL DEFAULT '''' COMMENT '专题标题',
  `subtitle` varchar(255) DEFAULT '''' COMMENT '专题子标题',
  `content` text COMMENT '专题内容，富文本格式',
  `price` decimal(10,2) DEFAULT '0.00' COMMENT '专题相关商品最低价',
  `read_count` varchar(255) DEFAULT '1k' COMMENT '专题阅读量',
  `pic_url` varchar(255) DEFAULT '' COMMENT '专题图片',
  `sort_order` int(11) DEFAULT '100' COMMENT '排序',
  `goods` varchar(1023) DEFAULT '' COMMENT '专题相关商品，采用JSON数组格式',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `topic_id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=319 DEFAULT CHARSET=utf8mb4 COMMENT='专题表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_user`
--

DROP TABLE IF EXISTS `litemall_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `source_id` varchar(100) DEFAULT NULL COMMENT '系统来源Id',
  `source_code` varchar(100) DEFAULT NULL COMMENT '系统来源编码',
  `source_name` varchar(255) DEFAULT NULL COMMENT '系统来源名称',
  `crm_id` varchar(100) DEFAULT NULL COMMENT 'CRM里的ID',
  `crm_name` varchar(100) DEFAULT NULL COMMENT 'CRM里的名称',
  `username` varchar(63) DEFAULT NULL COMMENT '用户名称',
  `password` varchar(63)  DEFAULT  NULL COMMENT '用户密码',
  `gender` tinyint(3)  DEFAULT '0' COMMENT '性别：0 未知， 1男， 1 女',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `last_login_time` datetime DEFAULT NULL COMMENT '最近一次登录时间',
  `last_login_ip` varchar(63)  DEFAULT NULL COMMENT '最近一次登录IP地址',
  `user_level` tinyint(3) DEFAULT '0' COMMENT '0 普通用户，1 VIP用户，2 高级VIP用户',
  `nickname` varchar(63) DEFAULT NULL COMMENT '用户昵称或网络名称',
  `mobile` varchar(20)  DEFAULT NULL COMMENT '用户手机号码',
  `avatar` varchar(255)   DEFAULT NULL COMMENT '用户头像图片',
  `weixin_openid` varchar(63)  DEFAULT NULL COMMENT '微信登录openid',
  `session_key` varchar(100)  DEFAULT NULL COMMENT '微信登录会话KEY',
  `weixin_unionid` varchar(100) DEFAULT NULL COMMENT '微信登录unionid',
  `status` tinyint(3)  DEFAULT '0' COMMENT '0 可用, 1 禁用, 2 注销',
  `user_class_attr1_code` varchar(50)  DEFAULT null COMMENT '用户分类1_ID',
  `user_class_attr1_name` varchar(255) DEFAULT NULL COMMENT '用户分类1_名称',
  `user_class_attr2_code` varchar(50)  DEFAULT null COMMENT '用户分类2_ID',
  `user_class_attr2_name` varchar(255) DEFAULT NULL COMMENT '用户分类2_名称',
  `app_id` varchar(255) DEFAULT NULL COMMENT '公众号ID',
  `com_id` int(11) DEFAULT null COMMENT '公司信息ID',
  `com_name` varchar(255) DEFAULT NULL COMMENT '公司信息名称',
  `falt_count` int(11) DEFAULT null COMMENT '预约违约次数',
  `cancel_count` int(11) DEFAULT null COMMENT '主动取消次数',
  `if_blacklist` tinyint(1) DEFAULT '0' COMMENT '是否黑名单',
  `token` varchar(255) DEFAULT NULL COMMENT 'Token',
  `token_expire` varchar(255) DEFAULT NULL COMMENT 'Token过期时间',

  `charge_sum_money` decimal(10,2) DEFAULT 0.00 COMMENT '充值总额',
  `charge_return_money` decimal(10,2) DEFAULT 0.00 COMMENT '退还金额',
  `charge_lock_money` decimal(10,2) DEFAULT 0.00 COMMENT '押金占用',
  `charge_remain_money` decimal(10,2) DEFAULT 0.00 COMMENT '可用余额',
  `charge_lock_to_order_money` decimal(10,2) DEFAULT 0.00 COMMENT '押金抵销金额',

  `sum_bonus` int(11) DEFAULT 0 COMMENT '用户汇总积分',
  `cost_bonus` int(11) DEFAULT 0 COMMENT '用户消费积分',
  `remain_bonus` int(11) DEFAULT 0 COMMENT '用户可用积分',

  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `litemall_user_charge_money`
--

DROP TABLE IF EXISTS `litemall_user_charge_money`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_user_charge_money` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `charge_time` datetime NOT NULL COMMENT '充值时间',
  `charge_money` decimal(10,2) NOT NULL COMMENT '充值金额',
  `pay_method` varchar(50) NOT NULL COMMENT '充值渠道',
  `pay_method_name` varchar(255) NOT NULL COMMENT '充值渠道名称',
  `charge_sheet_no` varchar(255) NOT NULL COMMENT '充值单号',
  `pay_no` varchar(255) DEFAULT NULL COMMENT '充值渠道返回支付号',
  `pay_return` tinyint(1) DEFAULT 0 COMMENT '是否退还',
  `pay_success` tinyint(1) DEFAULT 0 COMMENT '支付成功标识',
  `pay_time` datetime DEFAULT NULL COMMENT '支付成功时间',
   refund_status varchar(50) DEFAULT NULL COMMENT '申请退款状态',
   refund_time datetime DEFAULT NULL COMMENT '申请退款时间',
   deal_refund_op varchar(50) DEFAULT NULL COMMENT '处理退款人',
   deal_refund_time datetime DEFAULT NULL COMMENT '处理退款时间',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `tenant_id` int(11) default NULL  COMMENT '租户ID',
  `user_create_id` int(11) DEFAULT NULL COMMENT '外部创建用户Id',
  `user_modify_id` int(11) DEFAULT NULL COMMENT '外部修改用户Id',
  `op_create_id` int(11) DEFAULT NULL COMMENT '内部后台创建用户Id',
  `op_modify_id` int(11) DEFAULT NULL COMMENT '内部后台修改用户Id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户充值押金表';

--
-- Table structure for table `litemall_user_charge_money_lock`
--

DROP TABLE IF EXISTS `litemall_user_charge_money_lock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_user_charge_money_lock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `lock_type` varchar(50) NOT NULL COMMENT '锁定类型',
  `rule_mx_id` int(11) NOT NULL COMMENT '参与活动的ID',
  `goods_name` varchar(255) NOT NULL COMMENT '活动的商品名称',
  `lock_money` decimal(10,2) NOT NULL COMMENT '锁定金额',
  `lock_time` datetime NOT NULL COMMENT '锁定时间',
  `unlock_time` datetime DEFAULT NULL COMMENT '解锁时间',
  `lock_to_pay_time` datetime DEFAULT NULL COMMENT '锁定转订单时间',
  `order_id` int(11) DEFAULT NULL COMMENT '抵消的订单ID',
  `offer_id` int(11) DEFAULT NULL COMMENT '出价ID',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `tenant_id` int(11) default NULL  COMMENT '租户ID',
  `user_create_id` int(11) DEFAULT NULL COMMENT '外部创建用户Id',
  `user_modify_id` int(11) DEFAULT NULL COMMENT '外部修改用户Id',
  `op_create_id` int(11) DEFAULT NULL COMMENT '内部后台创建用户Id',
  `op_modify_id` int(11) DEFAULT NULL COMMENT '内部后台修改用户Id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户押金锁定表';

--
-- Table structure for table `litemall_user_formid`
--

DROP TABLE IF EXISTS `litemall_user_formid`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `litemall_user_formid` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `formId` varchar(63) NOT NULL COMMENT '缓存的FormId',
  `isprepay` tinyint(1) NOT NULL COMMENT '是FormId还是prepayId',
  `useAmount` int(2) NOT NULL COMMENT '可用次数，fromId为1，prepay为3，用1次减1',
  `expire_time` datetime NOT NULL COMMENT '过期时间，腾讯规定为7天',
  `openId` varchar(63) NOT NULL COMMENT '微信登录openid',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-12-10 16:59:09

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
   auction_gallery varchar(1023) default null COMMENT '专场拍Gallery',
   auction_gallery_big varchar(1023) default null COMMENT '专场拍Gallery大图',
   display_order int(11) default 0 COMMENT '推荐排序',
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
   deposit decimal(10,2) DEFAULT 0 COMMENT '保证金',
   expire_flag tinyint(1) default 0 COMMENT '过期标志 0-未过期 1-过期',
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
   order_id int(11) default NULL COMMENT '创建订单ID',
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
   deposit decimal(10,2) DEFAULT 0 COMMENT '保证金',
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
   order_id  int(11) DEFAULT NULL COMMENT '创建订单ID',
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
  `item_price_desc` varchar(255) DEFAULT NULL COMMENT '定价描述',
  `item_ding_money` decimal(10,2) DEFAULT 0 COMMENT '预约订金',
  `item_ship_cost` decimal(10,2) DEFAULT 0 COMMENT '运费金额',
  `item_finish_days` varchar(255)  DEFAULT NULL COMMENT '交付周期',
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
  `price_desc` varchar(255) DEFAULT NULL COMMENT '报价描述',
  `finish_days` varchar(255) DEFAULT null COMMENT '交付周期',
  `dingjin_price` decimal(10,2) DEFAULT 0 COMMENT '预付订金',
  `ship_cost` decimal(10,2) DEFAULT 0 COMMENT '运费金额',
  `order_count` int(11) DEFAULT 0 COMMENT '成交单量',
  `begin_time` datetime DEFAULT NULL COMMENT '报价有效开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '报价有效截止时间',
  `expire_flag` tinyint(1) DEFAULT '0' COMMENT '过期标志 0-未过期 1-过期',
  `free_post` tinyint(1) DEFAULT '0' COMMENT '是否免邮',
  `enabled` tinyint(1) DEFAULT 1 COMMENT '是否启用',
  `offer_flag` tinyint(1) DEFAULT 0 COMMENT '出价标志',
  `remark` varchar(1023) DEFAULT NULL COMMENT '备注',
  `auction_desc` varchar(1023) DEFAULT NULL COMMENT '商品描述',
   display_order int(11) default 0 COMMENT '推荐排序',
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
