-- MySQL dump 10.13  Distrib 8.0.28, for macos11 (arm64)
--
-- Host: 127.0.0.1    Database: payment_demo
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `payment_demo`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `payment_demo` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `payment_demo`;

--
-- Table structure for table `t_order_info`
--

DROP TABLE IF EXISTS `t_order_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_order_info` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `title` varchar(256) DEFAULT NULL COMMENT '订单标题',
  `order_no` varchar(50) DEFAULT NULL COMMENT '商户订单编号',
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `product_id` bigint DEFAULT NULL COMMENT '支付产品id',
  `total_fee` int DEFAULT NULL COMMENT '订单金额(分)',
  `code_url` varchar(50) DEFAULT NULL COMMENT '订单二维码连接',
  `order_status` varchar(10) DEFAULT NULL COMMENT '订单状态',
  `payment_type` varchar(20) DEFAULT NULL COMMENT '支付类型',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1598594957323640835 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_order_info`
--

LOCK TABLES `t_order_info` WRITE;
/*!40000 ALTER TABLE `t_order_info` DISABLE KEYS */;
INSERT INTO `t_order_info` VALUES (1598585743606984706,'购买大数据课程','ORDER_20221202155156949',NULL,2,1,NULL,'支付成功','支付宝','2022-12-02 15:51:56','2022-12-02 16:00:36'),(1598589737088634882,'购买大数据课程','ORDER_20221202160748858',NULL,2,1,NULL,'支付成功','支付宝','2022-12-02 16:07:48','2022-12-02 16:13:04'),(1598591650471399425,'购买大数据课程','ORDER_20221202161524921',NULL,2,1,NULL,'超时已关闭','支付宝','2022-12-02 16:15:24','2022-12-02 16:21:00'),(1598592253612367874,'购买Java开发课程','ORDER_20221202161748110',NULL,1,1,NULL,'支付成功','支付宝','2022-12-02 16:17:48','2022-12-02 16:19:25'),(1598593073250672642,'购买Java开发课程','ORDER_20221202162103140',NULL,1,1,NULL,'支付成功','支付宝','2022-12-02 16:21:03','2022-12-02 16:21:29'),(1598594773634097154,'购买Java开发课程','ORDER_20221202162749409',NULL,1,1,NULL,'退款异常','支付宝','2022-12-02 16:27:49','2022-12-02 16:32:28'),(1598594957323640834,'购买Java开发课程','ORDER_20221202162832576',NULL,1,1,NULL,'用户已取消','支付宝','2022-12-02 16:28:32','2022-12-02 16:29:42');
/*!40000 ALTER TABLE `t_order_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_payment_info`
--

DROP TABLE IF EXISTS `t_payment_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_payment_info` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '支付记录id',
  `order_no` varchar(50) DEFAULT NULL COMMENT '商户订单编号',
  `transaction_id` varchar(50) DEFAULT NULL COMMENT '支付系统交易编号',
  `payment_type` varchar(20) DEFAULT NULL COMMENT '支付类型',
  `trade_type` varchar(20) DEFAULT NULL COMMENT '交易类型',
  `trade_state` varchar(50) DEFAULT NULL COMMENT '交易状态',
  `payer_total` int DEFAULT NULL COMMENT '支付金额(分)',
  `content` text COMMENT '通知参数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1598594900369186819 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_payment_info`
--

LOCK TABLES `t_payment_info` WRITE;
/*!40000 ALTER TABLE `t_payment_info` DISABLE KEYS */;
INSERT INTO `t_payment_info` VALUES (1598587936239046657,'ORDER_20221202155156949','2022120222001402400501713926','支付宝','电脑网站支付','TRADE_SUCCESS',0,'{\"code\":\"10000\",\"msg\":\"Success\",\"buyer_logon_id\":\"sqo***@sandbox.com\",\"buyer_pay_amount\":\"0.00\",\"buyer_user_id\":\"2088722003402404\",\"buyer_user_type\":\"PRIVATE\",\"invoice_amount\":\"0.00\",\"out_trade_no\":\"ORDER_20221202155156949\",\"point_amount\":\"0.00\",\"receipt_amount\":\"0.00\",\"send_pay_date\":\"2022-12-02 15:52:19\",\"total_amount\":\"0.01\",\"trade_no\":\"2022120222001402400501713926\",\"trade_status\":\"TRADE_SUCCESS\"}','2022-12-02 16:00:39','2022-12-02 16:00:39'),(1598591065097555969,'ORDER_20221202160748858','2022120222001402400501714416','支付宝','电脑网站支付','TRADE_SUCCESS',0,'{\"code\":\"10000\",\"msg\":\"Success\",\"buyer_logon_id\":\"sqo***@sandbox.com\",\"buyer_pay_amount\":\"0.00\",\"buyer_user_id\":\"2088722003402404\",\"buyer_user_type\":\"PRIVATE\",\"invoice_amount\":\"0.00\",\"out_trade_no\":\"ORDER_20221202160748858\",\"point_amount\":\"0.00\",\"receipt_amount\":\"0.00\",\"send_pay_date\":\"2022-12-02 16:08:09\",\"total_amount\":\"0.01\",\"trade_no\":\"2022120222001402400501714416\",\"trade_status\":\"TRADE_SUCCESS\"}','2022-12-02 16:13:04','2022-12-02 16:13:04'),(1598592662267600898,'ORDER_20221202161748110','2022120222001402400501714236','支付宝','电脑网站支付','TRADE_SUCCESS',0,'{\"gmt_create\":\"2022-12-02 16:18:42\",\"charset\":\"UTF-8\",\"gmt_payment\":\"2022-12-02 16:18:58\",\"notify_time\":\"2022-12-02 16:19:01\",\"subject\":\"购买Java开发课程\",\"buyer_id\":\"2088722003402404\",\"invoice_amount\":\"0.01\",\"version\":\"1.0\",\"notify_id\":\"2022120200222161900002400521062748\",\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"out_trade_no\":\"ORDER_20221202161748110\",\"total_amount\":\"0.01\",\"trade_status\":\"TRADE_SUCCESS\",\"trade_no\":\"2022120222001402400501714236\",\"auth_app_id\":\"2021000121692283\",\"receipt_amount\":\"0.01\",\"point_amount\":\"0.00\",\"app_id\":\"2021000121692283\",\"buyer_pay_amount\":\"0.01\",\"seller_id\":\"2088621993877530\"}','2022-12-02 16:19:25','2022-12-02 16:19:25'),(1598593180314476546,'ORDER_20221202162103140','2022120222001402400501714075','支付宝','电脑网站支付','TRADE_SUCCESS',0,'{\"gmt_create\":\"2022-12-02 16:21:13\",\"charset\":\"UTF-8\",\"gmt_payment\":\"2022-12-02 16:21:22\",\"notify_time\":\"2022-12-02 16:21:24\",\"subject\":\"购买Java开发课程\",\"buyer_id\":\"2088722003402404\",\"invoice_amount\":\"0.01\",\"version\":\"1.0\",\"notify_id\":\"2022120200222162123002400521061671\",\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"out_trade_no\":\"ORDER_20221202162103140\",\"total_amount\":\"0.01\",\"trade_status\":\"TRADE_SUCCESS\",\"trade_no\":\"2022120222001402400501714075\",\"auth_app_id\":\"2021000121692283\",\"receipt_amount\":\"0.01\",\"point_amount\":\"0.00\",\"app_id\":\"2021000121692283\",\"buyer_pay_amount\":\"0.01\",\"seller_id\":\"2088621993877530\"}','2022-12-02 16:21:29','2022-12-02 16:21:29'),(1598594900369186818,'ORDER_20221202162749409','2022120222001402400501714417','支付宝','电脑网站支付','TRADE_SUCCESS',0,'{\"gmt_create\":\"2022-12-02 16:28:08\",\"charset\":\"UTF-8\",\"gmt_payment\":\"2022-12-02 16:28:16\",\"notify_time\":\"2022-12-02 16:28:19\",\"subject\":\"购买Java开发课程\",\"buyer_id\":\"2088722003402404\",\"invoice_amount\":\"0.01\",\"version\":\"1.0\",\"notify_id\":\"2022120200222162818002400521062749\",\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"out_trade_no\":\"ORDER_20221202162749409\",\"total_amount\":\"0.01\",\"trade_status\":\"TRADE_SUCCESS\",\"trade_no\":\"2022120222001402400501714417\",\"auth_app_id\":\"2021000121692283\",\"receipt_amount\":\"0.01\",\"point_amount\":\"0.00\",\"app_id\":\"2021000121692283\",\"buyer_pay_amount\":\"0.01\",\"seller_id\":\"2088621993877530\"}','2022-12-02 16:28:19','2022-12-02 16:28:19');
/*!40000 ALTER TABLE `t_payment_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_product`
--

DROP TABLE IF EXISTS `t_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商Bid',
  `title` varchar(20) DEFAULT NULL COMMENT '商品名称',
  `price` int DEFAULT NULL COMMENT '价格(分)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_product`
--

LOCK TABLES `t_product` WRITE;
/*!40000 ALTER TABLE `t_product` DISABLE KEYS */;
INSERT INTO `t_product` VALUES (1,'Java开发课程',1,'2022-11-26 10:42:06','2022-11-27 15:24:53'),(2,'大数据课程',1,'2022-11-26 10:42:07','2022-11-26 10:42:07'),(3,'前端课程',1,'2022-11-26 10:42:07','2022-11-27 15:59:10'),(4,'UI课程',1,'2022-11-26 10:42:07','2022-11-27 15:59:10');
/*!40000 ALTER TABLE `t_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_refund_info`
--

DROP TABLE IF EXISTS `t_refund_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_refund_info` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '款单id',
  `order_no` varchar(50) DEFAULT NULL COMMENT '商户订单编号',
  `refund_no` varchar(50) DEFAULT NULL COMMENT '商户退款单编号',
  `refund_id` varchar(50) DEFAULT NULL COMMENT '支付系统退款单号',
  `total_fee` int DEFAULT NULL COMMENT '原订单金额(分)',
  `refund` int DEFAULT NULL COMMENT '退款金额(分)',
  `reason` varchar(50) DEFAULT NULL COMMENT '退款原因',
  `refund_status` varchar(50) DEFAULT NULL COMMENT '退款状态',
  `content_return` text COMMENT '申请退款返回参数',
  `content_notify` text COMMENT '退款结果通知参数',
  `payment_type` varchar(20) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1598595809883037699 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_refund_info`
--

LOCK TABLES `t_refund_info` WRITE;
/*!40000 ALTER TABLE `t_refund_info` DISABLE KEYS */;
INSERT INTO `t_refund_info` VALUES (1598595809883037698,'ORDER_20221202162749409','REFUND_20221202163156648',NULL,1,1,'不喜欢','REFUND_ERROR','{\"alipay_trade_refund_apply_response\":{\"code\":\"20000\",\"msg\":\"Service Currently Unavailable\",\"sub_code\":\"aop.ACQ.SYSTEM_ERROR\",\"sub_msg\":\"系统繁忙\"},\"sign\":\"CnqIJWIqY4vyQ5l8B/t/2WYaNZQ7bqbqKcwegoFBShd5gC1x42d21bqi/JAQY/XTEu8Y/3ykSfqxqhV7Gb4eAvI8HevA+HYL7lWhaTK9BS2BPQsYfuoYNxc1pJtilr1BindKVCbi3Vz3Ll8lK0/y2E+/WTITBeO6RxRWgyx+8SElk0/9pR1EKNG3fQz5TJXGfvpZPO/i/rM9OOxZiLweUz6qKkBpoqh4TUfuJ8kEuER8b/KowIPu47WKsgw4wrQgUMnK+b8oqVp1IwTK9vMXL8YmVzA7p5EhyP3zQV1GqEaRatHEPEDLRYikb6d66vK1Pzur7zBpRnfQOTbB3ZnbCA==\"}',NULL,'支付宝','2022-12-02 16:31:56','2022-12-02 16:32:28');
/*!40000 ALTER TABLE `t_refund_info` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-12-02 16:57:34
