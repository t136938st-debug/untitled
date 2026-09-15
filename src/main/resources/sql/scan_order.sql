-- ============================================
-- 扫码点餐系统 - 数据库建表脚本
-- 数据库名：scan_order
-- ============================================

CREATE DATABASE IF NOT EXISTS `scan_order` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `scan_order`;

-- -------------------------------------------
-- 1. 用户表
-- -------------------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`    VARCHAR(50)  NOT NULL                COMMENT '用户名',
    `password`    VARCHAR(100) DEFAULT NULL            COMMENT '密码（微信登录用户可为空）',
    `name`        VARCHAR(50)  DEFAULT NULL            COMMENT '姓名',
    `phone`       VARCHAR(20)  DEFAULT NULL            COMMENT '手机号',
    `role`        INT          NOT NULL DEFAULT 1      COMMENT '角色：1-顾客，2-管理员',
    `avatar`      VARCHAR(255) DEFAULT NULL            COMMENT '头像URL',
    `openid`      VARCHAR(100) DEFAULT NULL            COMMENT '微信小程序openid',
    `create_time` DATETIME     DEFAULT NULL            COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT NULL            COMMENT '更新时间',
    `deleted`     INT          NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- -------------------------------------------
-- 2. 菜品分类表
-- -------------------------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`        VARCHAR(50)  NOT NULL                COMMENT '分类名称',
    `sort`        INT          DEFAULT 0               COMMENT '排序字段',
    `create_time` DATETIME     DEFAULT NULL            COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT NULL            COMMENT '更新时间',
    `deleted`     INT          NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品分类表';

-- -------------------------------------------
-- 3. 菜品表
-- -------------------------------------------
DROP TABLE IF EXISTS `dish`;
CREATE TABLE `dish` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`          VARCHAR(100)  NOT NULL                COMMENT '菜品名称',
    `category_id`   BIGINT        NOT NULL                COMMENT '分类ID',
    `price`         DECIMAL(10,2) NOT NULL                COMMENT '菜品价格',
    `image`         VARCHAR(255)  DEFAULT NULL            COMMENT '菜品图片URL',
    `description`   VARCHAR(500)  DEFAULT NULL            COMMENT '菜品描述',
    `status`        INT           NOT NULL DEFAULT 1      COMMENT '状态：0-下架，1-上架',
    `sort`          INT           DEFAULT 0               COMMENT '排序字段',
    `create_time`   DATETIME      DEFAULT NULL            COMMENT '创建时间',
    `update_time`   DATETIME      DEFAULT NULL            COMMENT '更新时间',
    `create_user_id` BIGINT       DEFAULT NULL            COMMENT '创建人ID',
    `update_user_id` BIGINT       DEFAULT NULL            COMMENT '更新人ID',
    `deleted`       INT           NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

-- -------------------------------------------
-- 4. 餐桌表
-- -------------------------------------------
DROP TABLE IF EXISTS `dining_table`;
CREATE TABLE `dining_table` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `table_number` VARCHAR(20)  NOT NULL                COMMENT '桌号',
    `seats`        INT          DEFAULT 4               COMMENT '座位数',
    `status`       INT          NOT NULL DEFAULT 0      COMMENT '状态：0-空闲，1-占用',
    `create_time`  DATETIME     DEFAULT NULL            COMMENT '创建时间',
    `update_time`  DATETIME     DEFAULT NULL            COMMENT '更新时间',
    `deleted`      INT          NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_table_number` (`table_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='餐桌表';

-- -------------------------------------------
-- 5. 购物车表
-- -------------------------------------------
DROP TABLE IF EXISTS `shopping_cart`;
CREATE TABLE `shopping_cart` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     BIGINT        NOT NULL                COMMENT '用户ID',
    `dish_id`     BIGINT        NOT NULL                COMMENT '菜品ID',
    `dish_name`   VARCHAR(100)  DEFAULT NULL            COMMENT '菜品名称（冗余）',
    `dish_image`  VARCHAR(255)  DEFAULT NULL            COMMENT '菜品图片（冗余）',
    `price`       DECIMAL(10,2) NOT NULL                COMMENT '单价',
    `quantity`    INT           NOT NULL DEFAULT 1      COMMENT '数量',
    `create_time` DATETIME      DEFAULT NULL            COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- -------------------------------------------
-- 6. 订单表
-- -------------------------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
    `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no`     VARCHAR(50)   NOT NULL                COMMENT '订单号',
    `user_id`      BIGINT        NOT NULL                COMMENT '用户ID',
    `table_id`     BIGINT        NOT NULL                COMMENT '餐桌ID',
    `total_amount` DECIMAL(10,2) NOT NULL                COMMENT '订单总金额',
    `status`       INT           NOT NULL DEFAULT 0      COMMENT '状态：0-待付款，1-已支付，2-已完成，3-已取消',
    `remark`       VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
    `pay_time`     DATETIME      DEFAULT NULL            COMMENT '支付时间',
    `create_time`  DATETIME      DEFAULT NULL            COMMENT '创建时间',
    `update_time`  DATETIME      DEFAULT NULL            COMMENT '更新时间',
    `deleted`      INT           NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- -------------------------------------------
-- 7. 订单明细表
-- -------------------------------------------
DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail` (
    `id`         BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_id`   BIGINT        NOT NULL                COMMENT '订单ID',
    `dish_id`    BIGINT        NOT NULL                COMMENT '菜品ID',
    `dish_name`  VARCHAR(100)  DEFAULT NULL            COMMENT '菜品名称',
    `dish_image` VARCHAR(255)  DEFAULT NULL            COMMENT '菜品图片',
    `price`      DECIMAL(10,2) NOT NULL                COMMENT '单价',
    `quantity`   INT           NOT NULL                COMMENT '数量',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- -------------------------------------------
-- 初始数据
-- -------------------------------------------

-- 插入管理员账号（密码：admin123）
INSERT INTO `user` (`username`, `password`, `name`, `phone`, `role`, `create_time`, `update_time`)
VALUES ('admin', 'admin123', '系统管理员', '13800000000', 2, NOW(), NOW());

-- 插入测试顾客账号（密码：123456）
INSERT INTO `user` (`username`, `password`, `name`, `phone`, `role`, `create_time`, `update_time`)
VALUES ('customer', '123456', '测试顾客', '13800000001', 1, NOW(), NOW());

-- 插入菜品分类
INSERT INTO `category` (`name`, `sort`, `create_time`, `update_time`) VALUES ('热菜', 1, NOW(), NOW());
INSERT INTO `category` (`name`, `sort`, `create_time`, `update_time`) VALUES ('凉菜', 2, NOW(), NOW());
INSERT INTO `category` (`name`, `sort`, `create_time`, `update_time`) VALUES ('主食', 3, NOW(), NOW());
INSERT INTO `category` (`name`, `sort`, `create_time`, `update_time`) VALUES ('饮品', 4, NOW(), NOW());

-- 插入餐桌数据
INSERT INTO `dining_table` (`table_number`, `seats`, `create_time`, `update_time`) VALUES ('A01', 4, NOW(), NOW());
INSERT INTO `dining_table` (`table_number`, `seats`, `create_time`, `update_time`) VALUES ('A02', 4, NOW(), NOW());
INSERT INTO `dining_table` (`table_number`, `seats`, `create_time`, `update_time`) VALUES ('B01', 6, NOW(), NOW());
INSERT INTO `dining_table` (`table_number`, `seats`, `create_time`, `update_time`) VALUES ('B02', 8, NOW(), NOW());
INSERT INTO `dining_table` (`table_number`, `seats`, `create_time`, `update_time`) VALUES ('VIP01', 10, NOW(), NOW());
