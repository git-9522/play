#finished
ALTER TABLE `t_mall_rewards` ADD COLUMN bit_period INT(2) NOT NULL DEFAULT 0 AFTER `limit_day` 

#todo
CREATE TABLE `t_questionnaire` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `modify_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `name` varchar(32) NOT NULL DEFAULT '' COMMENT '名称',
  `is_used` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否使用中 0 ：未被使用，1 ：使用中',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `t_questionnaire_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `questionnaire_id` bigint(20) NOT NULL COMMENT '问卷id',
  `content` varchar(255) NOT NULL DEFAULT '' COMMENT '问卷问题',
  `description` varchar(50) NOT NULL DEFAULT '' COMMENT '描述',
  `sort` int(2) NOT NULL DEFAULT '0' COMMENT '排序',
  `is_multiple` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否多选',
  `size` int(2) NOT NULL DEFAULT '0' COMMENT '大小',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `modify_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `questionnaire_id` (`questionnaire_id`),
  CONSTRAINT `t_questionnaire_detail_ibfk_1` FOREIGN KEY (`questionnaire_id`) REFERENCES `t_questionnaire` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `t_questionnaire_option` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `questionnaire_detail_id` bigint(20) NOT NULL COMMENT '问卷问题id',
  `content` varchar(255) NOT NULL DEFAULT '' COMMENT '问卷选项',
  `description` varchar(50) NOT NULL DEFAULT '' COMMENT '描述',
  `score` int(3) NOT NULL DEFAULT '0' COMMENT '分数',
  `sort` int(2) NOT NULL DEFAULT '0' COMMENT '排序',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `modify_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `questionnaire_detail_id` (`questionnaire_detail_id`),
  CONSTRAINT `t_questionnaire_option_ibfk_1` FOREIGN KEY (`questionnaire_detail_id`) REFERENCES `t_questionnaire_detail` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `t_questionnaire_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `questionnaire_id` bigint(20) NOT NULL COMMENT '问卷id',
  `questionnaire_detail_id` bigint(20) NOT NULL COMMENT '问卷问题id',
  `questionnaire_option_id` bigint(20) NOT NULL COMMENT '问卷选项id',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `questionnaire_id` (`questionnaire_id`),
  KEY `questionnaire_option_id` (`questionnaire_option_id`),
  KEY `questionnaire_detail_id` (`questionnaire_detail_id`),
  CONSTRAINT `t_questionnaire_record_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `t_questionnaire_record_ibfk_2` FOREIGN KEY (`questionnaire_id`) REFERENCES `t_questionnaire` (`id`),
  CONSTRAINT `t_questionnaire_record_ibfk_4` FOREIGN KEY (`questionnaire_option_id`) REFERENCES `t_questionnaire_option` (`id`),
  CONSTRAINT `t_questionnaire_record_ibfk_5` FOREIGN KEY (`questionnaire_detail_id`) REFERENCES `t_questionnaire_detail` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

insert into `t_questionnaire` (`id`, `create_time`, `modify_time`, `name`, `is_used`, `description`) values('1','2017-05-25 10:03:21',NULL,'问卷调查1','','测试专用啦啦啦');

insert into `t_questionnaire_detail` (`id`, `questionnaire_id`, `content`, `description`, `sort`, `is_multiple`, `size`, `create_time`, `modify_time`) values('1','1','您的性别是？','问题1','1','','2','2017-05-25 10:04:43',NULL);
insert into `t_questionnaire_detail` (`id`, `questionnaire_id`, `content`, `description`, `sort`, `is_multiple`, `size`, `create_time`, `modify_time`) values('2','1','您的年龄是？','问题2','2','','6','2017-05-25 10:06:47',NULL);
insert into `t_questionnaire_detail` (`id`, `questionnaire_id`, `content`, `description`, `sort`, `is_multiple`, `size`, `create_time`, `modify_time`) values('3','1','	您是通过什么途径了解虹金所？','问题3','3','','5','2017-05-25 10:06:49',NULL);
insert into `t_questionnaire_detail` (`id`, `questionnaire_id`, `content`, `description`, `sort`, `is_multiple`, `size`, `create_time`, `modify_time`) values('4','1','您更希望通过哪个途径获取虹银？','问题4','4','','5','2017-05-25 10:07:18',NULL);
insert into `t_questionnaire_detail` (`id`, `questionnaire_id`, `content`, `description`, `sort`, `is_multiple`, `size`, `create_time`, `modify_time`) values('5','1','活动奖品您更喜欢哪类奖品？','问题5','5','','5','2017-05-25 10:08:26',NULL);
insert into `t_questionnaire_detail` (`id`, `questionnaire_id`, `content`, `description`, `sort`, `is_multiple`, `size`, `create_time`, `modify_time`) values('6','1','	您对这份调研问卷满意吗？','问题6','6','','4','2017-05-25 10:08:48',NULL);

insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('1','1','男','问题1答案1','50','1','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('2','1','女','问题1答案2','50','2','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('3','2','24周岁以下','问题2答案1','17','1','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('4','2','24周岁-30周岁','问题2答案2','17','2','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('5','2','31周岁-40周岁','问题2答案3','17','3','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('6','2','41周岁-50周岁','问题2答案4','17','4','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('7','2','51周岁-65周岁 ','问题2答案5','16','5','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('8','2','65周岁以上','问题2答案6','16','6','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('9','3','网络媒体','问题3答案1','20','1','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('10','3','朋友介绍','问题3答案2','20','2','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('11','3','新闻报刊','问题3答案3','20','3','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('12','3','返利渠道','问题3答案4','20','4','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('13','3','其它','问题3答案5','20','5','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('14','4','每日签到','问题4答案1','20','1','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('15','4','邀请好友','问题4答案2','20','2','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('16','4','任务获取','问题4答案3','20','3','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('17','4','投资获取','问题4答案4','20','4','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('18','4','其它','问题4答案5','20','5','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('19','5','实物类（3C产品、书籍、家居、户外等）','问题5答案1','20','1','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('20','5','虚拟卡类（红包券、手机卡、京东卡、购物卡等）','问题5答案2','20','2','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('21','5','旅游类（国内游、东南亚、澳洲、欧美等）','问题5答案3','20','3','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('22','5','现金类（10元、50元、100元等）','问题5答案4','20','4','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('23','5','其它','问题5答案5','20','5','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('24','6','非常满意','问题6答案1','25','1','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('25','6','同A','问题6答案2','25','2','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('26','6','同B','问题6答案3','25','3','2017-05-25 10:10:19',NULL);
insert into `t_questionnaire_option` (`id`, `questionnaire_detail_id`, `content`, `description`, `score`, `sort`, `create_time`, `modify_time`) values('27','6','以上均是','问题6答案4','25','4','2017-05-25 10:10:19',NULL);