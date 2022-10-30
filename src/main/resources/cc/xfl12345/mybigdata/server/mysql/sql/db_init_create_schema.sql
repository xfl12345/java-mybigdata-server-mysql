/**
* Source Server Type             : MySQL
* Source Server AppInfo          : 5.7.26
* Source Host                    : 127.0.0.1:3306
* FileOperation Encoding         : utf-8
* Date: 2021/6/9 17:00:00
*/

SET FOREIGN_KEY_CHECKS = 0;

# 优先创建 字符串记录表 ，毕竟 全局ID记录表 对 字符串表 存在必要依赖
/**
  一个庞大的 字符串记录表，暂时还没做来源系统
 */
CREATE TABLE string_content
(
    `global_id`      bigint NOT NULL comment '当前表所在数据库实例里的全局ID',
    `data_format`    bigint comment '字符串结构格式',
    `content_length` smallint        NOT NULL default -1 comment '字符串长度',
    `content`        varchar(768)    NOT NULL comment '字符串内容，最大长度为 768 个字符',
    unique key unique_global_id (global_id) comment '确保每一行数据对应一个相对于数据库唯一的global_id',
    index boost_query_id (data_format, content_length) comment '加速查询主键，避免全表扫描',
    unique key boost_query_content (content(768)) comment '加速检索字符串内容'
) ENGINE = InnoDB
  ROW_FORMAT = DYNAMIC;

INSERT INTO string_content (global_id, data_format, content)
    # 第一个字符串，关于数据格式——text
values (1, 1, 'text');

# 设置字符串格式的默认值为 text
ALTER TABLE string_content
    ALTER COLUMN data_format SET DEFAULT 1;

INSERT INTO string_content (global_id, content)
    # 第二个字符串，是一个空字符串
VALUES (2, ''),
       # 第三个字符串，关于 "描述" 本身
       (3, '说明、描述'),
       # 第四个字符串，关于 第一个字符串 的描述
       (4, '一种字符串内容格式'),
       # 第五个字符串，关于 字符串表 的名称
       (5, '字符串记录表的名称'),
       # 第六个字符串，关于 字符串表 的名称
       (6, 'string_content'),
       # 第七个字符串，关于 全局ID记录表 的描述
       (7, '全局ID记录表的名称'),
       # 第八个字符串，关于 全局ID记录表 的名称
       (8, 'global_data_record'),
       # 第九个字符串，关于 常量 true
       (9, 'true'),
       # 第十个字符串，关于 常量 false
       (10, 'false'),
       # 第十一个字符串，关于 布尔值表 的描述
       (11, '布尔值表的名称'),
       # 第十二个字符串，关于 布尔值表 的名称
       (12, 'boolean_content');



/**
  全局ID记录表，记录并关联当前数据库内所有表的每一行数据。
  如果 table_name 字段 是空值，则意味着它是未使用的记录（用以加速插入数据），
  而且该行数据有可能会被定期删除。
 */
CREATE TABLE global_data_record
(
    `id`             bigint NOT NULL PRIMARY KEY AUTO_INCREMENT comment '当前表所在数据库实例里的全局ID',
    `uuid`           char(36)        NOT NULL comment '关于某行数据的，整个MySQL数据库乃至全球唯一的真正的全局ID',
    `create_time`    datetime        DEFAULT CURRENT_TIMESTAMP comment '创建时间',
    `update_time`    datetime        DEFAULT CURRENT_TIMESTAMP comment '修改时间',
    `modified_count` bigint DEFAULT 1 comment '修改次数（版本迭代）',
    `table_name`     bigint DEFAULT NULL comment '该行数据所在的表名',
    `description`    bigint DEFAULT NULL comment '该行数据的附加简述',
    # 全局ID 记录表，删除乃大忌。拒绝一切外表级联删除行记录，只允许按 global_id 或 uuid 删除行记录
    # 遵循 一切普通文本 由 字符串记录表
    unique key index_uuid (uuid) comment '确保UUID的唯一性',
    index boost_query_all (uuid, create_time, update_time, modified_count, table_name, description)
) ENGINE = InnoDB
  ROW_FORMAT = DYNAMIC;


INSERT INTO global_data_record (id, uuid, table_name, description)
    # 先斩后奏 之 关联已有的 字符串 数据
VALUES (1, '00000000-cb7a-11eb-0000-f828196a1686', 6, 4),
       (2, '00000001-cb7a-11eb-0000-f828196a1686', 6, 2),
       (3, '00000002-cb7a-11eb-0000-f828196a1686', 6, 3),
       (4, '00000003-cb7a-11eb-0000-f828196a1686', 6, 3),
       (5, '00000004-cb7a-11eb-0000-f828196a1686', 6, 3),
       (6, '00000005-cb7a-11eb-0000-f828196a1686', 6, 5),
       (7, '00000006-cb7a-11eb-0000-f828196a1686', 6, 3),
       (8, '00000007-cb7a-11eb-0000-f828196a1686', 6, 7),
       (9, '00000008-cb7a-11eb-0000-f828196a1686', 6, 2),
       (10, '00000009-cb7a-11eb-0000-f828196a1686', 6, 2),
       (11, '0000000a-cb7a-11eb-0000-f828196a1686', 6, 2),
       (12, '0000000b-cb7a-11eb-0000-f828196a1686', 6, 2);

# 常量，自己解释自己
UPDATE global_data_record SET description = 9 WHERE id = 9;
UPDATE global_data_record SET description = 10 WHERE id = 10;

# 为 字符串表 添加 全局ID 约束
ALTER TABLE string_content
    add foreign key (global_id) references global_data_record (id) on delete restrict on update cascade;

# 添加 字符串格式 引用出处（自环）
ALTER TABLE string_content
    add foreign key (data_format) references string_content (global_id) on delete restrict on update cascade;

# 启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

/**
  布尔值表。只有两行数据的表。为的只是维护架构逻辑的一致性。
 */
CREATE TABLE boolean_content
(
    `global_id`      bigint  NOT NULL comment '当前表所在数据库实例里的全局ID',
    `content`        boolean NOT NULL comment '布尔值',
    unique key unique_global_id (global_id) comment '确保每一行数据对应一个相对于数据库唯一的global_id',
    unique key boost_query_content (content) comment '唯一限制。意味着该表只可能有两个值。'
) ENGINE = InnoDB
  ROW_FORMAT = DYNAMIC;

INSERT INTO global_data_record (id, uuid, table_name, description)
VALUES (20, '00000014-cb7a-11eb-0000-f828196a1686', 12, 9);
INSERT INTO boolean_content (global_id, content) VALUES (20, true);

INSERT INTO global_data_record (id, uuid, table_name, description)
VALUES (21, '00000015-cb7a-11eb-0000-f828196a1686', 12, 10);
INSERT INTO boolean_content (global_id, content) VALUES (21, false);


ALTER TABLE global_data_record AUTO_INCREMENT = 100;

/**
  因为自环引用会导致自锁，所以以下给出 将 string_content 表里的
  global_id 从 1 改成 1000 的方法：

    UPDATE string_content
    SET data_format = 2
    WHERE data_format = 1

    UPDATE global_data_record SET global_id = 1000 WHERE global_id = 1

    UPDATE string_content
    SET data_format = 1000
    WHERE data_format = 2

 */

# 创建个视图，方便跨表查看数据
CREATE OR REPLACE VIEW view_global_data_record AS
SELECT g.id,
       g.uuid,
       g.create_time,
       g.update_time,
       g.modified_count,
       (SELECT content FROM string_content AS s1 WHERE s1.global_id = g.table_name)  AS `table_name`,
       (SELECT content FROM string_content AS s2 WHERE s2.global_id = g.description) AS `description`
FROM global_data_record AS g
ORDER BY `g`.id;

CREATE OR REPLACE VIEW view_string_content AS
SELECT g.id,
       g.uuid,
       g.create_time,
       g.update_time,
       g.modified_count,
       (SELECT content FROM string_content AS s WHERE s.global_id = g.description) AS `description`,
       data_src_table.content                                                      AS `content`
FROM string_content AS data_src_table,
     global_data_record AS g
WHERE data_src_table.global_id = g.id
ORDER BY `g`.id;

/**
  专门记录 “数字” 的表
 */
CREATE TABLE number_content
(
    `global_id`        bigint NOT NULL comment '当前表所在数据库实例里的全局ID',
    `numberIsInteger`  boolean NOT NULL comment '是否为整数（无论长度）',
    `numberIs64bit`    boolean NOT NULL comment '是否为64bit整数',
    `content`          varchar(760) NOT NULL comment '字符串形式的十进制数字（最多760个字符）',
    foreign key (global_id) references global_data_record (id) on delete restrict on update cascade,
    unique key unique_global_id (global_id) comment '确保每一行数据对应一个相对于数据库唯一的global_id',
    unique key boost_query_all (numberIsInteger, numberIs64bit, content) comment '加速查询全部数据'
) ENGINE = InnoDB
  ROW_FORMAT = DYNAMIC;

/**
  MyBigData 特色功能之一，就是使用JSON Schema完成对MySQL模型化操作
  这个表主要存放JSON模型
 */
CREATE TABLE table_schema_record
(
    `global_id`      bigint NOT NULL comment '当前表所在数据库实例里的全局ID',
    `schema_name`    bigint NOT NULL comment '插表模型名称',
    `content_length` smallint        NOT NULL default -1 comment 'json_schema 字段的长度',
    # 这里不遵循 “一切普通文本 由 字符串记录表” 的原则
    # 是因为json格式的字符串可以使用json格式存储，MySQL原生支持JSON格式
    # 暂不考虑使用JSON格式存储JSON字符串，暂且先保留修改空间
    `json_schema`    varchar(16000)  NOT NULL comment '插表模型',
    foreign key (global_id) references global_data_record (id) on delete restrict on update cascade,
    foreign key (schema_name) references global_data_record (id) on delete restrict on update cascade,
    unique key unique_global_id (global_id) comment '确保每一行数据对应一个相对于数据库唯一的global_id',
    unique key index_schema_name (schema_name) comment '确保插表模型名称的唯一性',
    index boost_query_id (global_id, schema_name, content_length) comment '加速查询主键，避免全表扫描'
) ENGINE = InnoDB
  ROW_FORMAT = DYNAMIC;

CREATE OR REPLACE VIEW view_table_schema_record AS
SELECT g.id,
       g.uuid,
       g.create_time,
       g.update_time,
       g.modified_count,
       (SELECT content FROM string_content AS s WHERE s.global_id = g.description) AS `description`,
       data_src_table.schema_name                                                  AS schema_name,
       data_src_table.content_length                                               AS content_length,
       data_src_table.json_schema                                                  AS json_schema
FROM table_schema_record AS data_src_table,
     global_data_record AS g
WHERE data_src_table.global_id = g.id
ORDER BY g.id;

/**
  专门记录树状结构的表
 */
CREATE TABLE tree_struct_record
(
    `global_id`      bigint NOT NULL comment '当前表所在数据库实例里的全局ID',
    `root_id`        bigint NOT NULL comment '根节点对象',
    `item_count`     int    NOT NULL comment '整个树的节点个数',
    `tree_deep`      int    NOT NULL comment '整个树的深度（有几层）',
    `content_length` smallint        NOT NULL default -1 comment 'JSON文本长度',
    # 暂不考虑使用JSON格式存储JSON字符串，且先保留修改空间
    `struct_data`    varchar(16000)  NOT NULL comment '以JSON字符串形式记载树形结构',
    foreign key (global_id) references global_data_record (id) on delete restrict on update cascade,
    unique key unique_global_id (global_id) comment '确保每一行数据对应一个相对于数据库唯一的global_id',
    foreign key (root_id) references global_data_record (id) on delete restrict on update cascade,
    index boost_query_id (root_id, item_count, tree_deep, content_length) comment '加速查询主键，避免全表扫描'
) ENGINE = InnoDB
  ROW_FORMAT = DYNAMIC;

CREATE OR REPLACE VIEW view_tree_struct_record AS
SELECT g.id,
       g.uuid,
       g.create_time,
       g.update_time,
       g.modified_count,
       (SELECT content FROM string_content AS s WHERE s.global_id = g.description) AS `description`,
       data_src_table.root_id                                                      AS `root_id`,
       data_src_table.item_count                                                   AS `item_count`,
       data_src_table.tree_deep                                                    AS `tree_deep`,
       data_src_table.content_length                                               AS `content_length`,
       data_src_table.struct_data                                                  AS `struct_data`
FROM tree_struct_record AS data_src_table,
     global_data_record AS g
WHERE data_src_table.global_id = g.id
ORDER BY g.id;

/**
  专门记录二元关系的表
 */
CREATE TABLE binary_relationship_record
(
    `global_id` bigint NOT NULL comment '当前表所在数据库实例里的全局ID',
    `item_a`    bigint NOT NULL comment '对象A',
    `item_b`    bigint NOT NULL comment '对象B',
    foreign key (global_id) references global_data_record (id) on delete restrict on update cascade,
    unique key unique_global_id (global_id) comment '确保每一行数据对应一个相对于数据库唯一的global_id',
    foreign key (item_a) references global_data_record (id) on delete restrict on update cascade,
    foreign key (item_b) references global_data_record (id) on delete restrict on update cascade,
    unique key unique_limit_ab (item_a, item_b) comment '不允许出现重复关系，以免浪费空间',
    index boost_query_all (item_b, item_a) comment '加速查询全部数据'
) ENGINE = InnoDB
  ROW_FORMAT = DYNAMIC;

CREATE OR REPLACE VIEW view_binary_relationship_record AS
SELECT g.id,
       g.uuid,
       g.create_time,
       g.update_time,
       g.modified_count,
       (SELECT content FROM string_content AS s WHERE s.global_id = g.description) AS `description`,
       data_src_table.item_a                                                       AS `item_a`,
       (SELECT content
        FROM string_content AS s
        WHERE s.global_id = (SELECT description
                             FROM global_data_record AS s
                             WHERE s.id = data_src_table.item_a))                  AS `item_a_description`,
       data_src_table.item_b                                                       AS `item_b`,
       (SELECT content
        FROM string_content AS s
        WHERE s.global_id = (SELECT description
                             FROM global_data_record AS s
                             WHERE s.id = data_src_table.item_b))                  AS `item_b_description`
FROM binary_relationship_record AS data_src_table,
     global_data_record AS g
WHERE data_src_table.global_id = g.id
ORDER BY g.id;

/**
  专门记录 “组” 的表
  不过这 group_record 表只记 组号
 */
CREATE TABLE group_record
(
    `global_id`    bigint NOT NULL comment '当前表所在数据库实例里的全局ID',
    `group_name`   bigint NOT NULL default 2 comment '组名',
    `unique_items` boolean NOT NULL default false comment '元素是否都是唯一的（默认否）',
    foreign key (global_id) references global_data_record (id) on delete restrict on update cascade,
    unique key unique_global_id (global_id) comment '确保每一行数据对应一个相对于数据库唯一的global_id',
    foreign key (group_name) references string_content (global_id) on delete restrict on update cascade,
    index boost_query_all (group_name, unique_items) comment '加速查询全部数据'
) ENGINE = InnoDB
  ROW_FORMAT = DYNAMIC;

/**
  专门记录 “组” 的表，所有关于“列表（一维数组）”、“集合”和“分组”等概念的数据的 关系 都记录于该表
 */
CREATE TABLE group_content
(
    `global_id`  bigint NOT NULL comment '组id',
    `item_index` bigint NOT NULL comment '组内对象的下标',
    `item`       bigint NOT NULL comment '组内对象',
    # 关联 group_record 表。毕竟 “组” 这种概念，本就是一对多的关系。
    foreign key (global_id) references group_record (global_id) on delete restrict on update cascade,
    foreign key (item) references global_data_record (id) on delete restrict on update cascade,
    unique key boost_query_all (global_id, item_index, item) comment '加速查询全部数据'
) ENGINE = InnoDB
  ROW_FORMAT = DYNAMIC;

CREATE OR REPLACE VIEW view_group_content AS
SELECT g.id,
       g.uuid,
       g.create_time,
       g.update_time,
       g.modified_count,
       (SELECT content FROM string_content AS s WHERE s.global_id = g.description) AS `description`,
       (SELECT content
        FROM string_content AS s
        WHERE s.global_id = (SELECT id
                             FROM group_record AS s
                             WHERE s.global_id = data_src_table.global_id))         AS `group_name`,
       data_src_table.item_index                                                   AS `item_index`,
       data_src_table.item                                                         AS `item`
FROM group_content AS data_src_table,
     global_data_record AS g
WHERE data_src_table.global_id = g.id
ORDER BY g.id;


/**
  专门记录 “字典” 的表
  不过这 object_record 表只记 对象号
 */
CREATE TABLE object_record
(
    `global_id`     bigint NOT NULL comment '对象id',
    `object_schema` bigint NOT NULL comment '用于检验该对象的JSON Schema',
    `schema_path`   bigint NOT NULL comment 'JSON Schema 的 相对路径（用于检验子对象）',
    `object_name`   bigint NOT NULL comment '对象名称',
    foreign key (global_id) references global_data_record (id) on delete restrict on update cascade,
    foreign key (object_schema) references table_schema_record (global_id) on delete restrict on update cascade,
    foreign key (object_name) references string_content (global_id) on delete restrict on update cascade,
    unique key unique_global_id (global_id) comment '确保每一行数据对应一个相对于数据库唯一的global_id',
    unique unique_schema (global_id, object_schema),
    unique unique_name (global_id, object_name),
    unique key boost_query_all (global_id, object_schema, object_name) comment '加速查询全部数据'
) ENGINE = InnoDB
  ROW_FORMAT = DYNAMIC;

/**
  专门记录 “对象” 的表，所有关于“字典”概念的数据的 关系 都记录于该表
 */
CREATE TABLE object_content
(
    `global_id`     bigint NOT NULL comment '对象id',
    `the_key`       bigint NOT NULL comment '属性名称',
    `the_value`     bigint comment '属性值',
    foreign key (global_id) references object_record (global_id) on delete restrict on update cascade,
    foreign key (the_key) references string_content (global_id) on delete restrict on update cascade,
    foreign key (the_value) references global_data_record (id) on delete restrict on update cascade,
    unique key unique_object_key (global_id, the_key),
    unique key boost_query_all (global_id, the_key, the_value) comment '加速查询全部数据'
) ENGINE = InnoDB
  ROW_FORMAT = DYNAMIC;


# 大杀器，修改所有表的引擎为 InnoDB 。适用于对MySQL支持不太好的 ORM框架
# SELECT CONCAT( 'ALTER TABLE ', TABLE_NAME, ' ENGINE=InnoDB;' )
# FROM information_schema.tables
# WHERE table_schema = 'mybigdata';

/**
  账号表
 */
CREATE TABLE auth_account
(
    `account_id`    bigint          NOT NULL PRIMARY KEY AUTO_INCREMENT comment '账号ID',
    `password_hash` char(128)       NOT NULL comment '账号密码的哈希值',
    `password_salt` char(128)       NOT NULL comment '账号密码的哈希值计算的佐料',
    `extra_info_id` bigint          DEFAULT NULL comment '账号额外信息',
    unique key unique_account(account_id),
    index boost_query_all (account_id, password_hash, password_salt, extra_info_id),
    foreign key (extra_info_id) references global_data_record (id) on delete restrict on update restrict
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  ROW_FORMAT = Dynamic;

# 补完字符串长度
UPDATE string_content
SET content_length = CHAR_LENGTH(content)
WHERE content_length = default(content_length);
