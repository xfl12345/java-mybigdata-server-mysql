DELIMITER $$

use xfl_mybigdata$$

# 准备插入一些初始信息
# 来个插入数据的正规流程示范
# 先向 global_data_record 表注册，通过查询 UUID 拿到 global_id
DROP FUNCTION IF EXISTS insert_global_data_record $$
CREATE FUNCTION insert_global_data_record(
    the_uuid char(36),
    table_name_gid bigint unsigned,
    description_gid bigint unsigned)
    RETURNS bigint unsigned

BEGIN
    DECLARE insert_row_gid bigint unsigned;
    # 新增一行全局ID
    INSERT INTO global_data_record (uuid, table_name, description)
    VALUES (the_uuid, table_name_gid, description_gid);
    # 获取全局ID
    SET insert_row_gid = (SELECT id FROM global_data_record WHERE uuid = the_uuid);

    RETURN insert_row_gid;
END $$



DROP FUNCTION IF EXISTS insert_description_to_string_content $$
CREATE FUNCTION insert_description_to_string_content(
    description_uuid char(36),
    description varchar(768))
    RETURNS bigint unsigned

BEGIN
    DECLARE normal_text_description_gid bigint unsigned;
    DECLARE string_content_table_name_gid bigint unsigned;
    DECLARE insert_content_gid bigint unsigned;
    SET normal_text_description_gid = (SELECT global_id FROM string_content WHERE content = '说明、描述');
    SET string_content_table_name_gid = (SELECT global_id FROM string_content WHERE content = 'string_content');
    # 获取全局ID
    SET insert_content_gid = insert_global_data_record(
        description_uuid,
        string_content_table_name_gid,
        normal_text_description_gid);
    # 插入 内容 到 字符串表
    INSERT INTO string_content (global_id, content)
    VALUES (insert_content_gid, description);

    RETURN insert_content_gid;
END $$



DROP FUNCTION IF EXISTS insert_string_content_with_description_gid $$
CREATE FUNCTION insert_string_content_with_description_gid(
    insert_content_uuid char(36),
    insert_content varchar(768),
    description_gid bigint unsigned)
    RETURNS bigint unsigned

BEGIN
    DECLARE string_content_table_name_gid bigint unsigned;
    DECLARE insert_content_gid bigint unsigned;
    SET string_content_table_name_gid = (SELECT global_id FROM string_content WHERE content = 'string_content');
    # 获取全局ID
    SET insert_content_gid = insert_global_data_record(
        insert_content_uuid,
        string_content_table_name_gid,
        description_gid);
    # 插入 内容 到 字符串表
    INSERT INTO string_content (global_id, content)
    VALUES (insert_content_gid, insert_content);

    RETURN insert_content_gid;
END $$



DROP FUNCTION IF EXISTS insert_string_content $$
CREATE FUNCTION insert_string_content(
    description_uuid char(36),
    description varchar(768),
    insert_content_uuid char(36),
    insert_content varchar(768))
    RETURNS bigint unsigned

BEGIN
    DECLARE description_gid bigint unsigned;
    SET description_gid = (SELECT insert_description_to_string_content(description_uuid, description));
    RETURN (SELECT insert_string_content_with_description_gid(
                       insert_content_uuid,
                       insert_content,
                       description_gid));
END $$



DROP FUNCTION IF EXISTS insert_table_schema_record $$
CREATE FUNCTION insert_table_schema_record(
    description_uuid char(36),
    description varchar(768),
    schema_name_uuid char(36),
    schema_name varchar(768),
    json_schema_content_uuid char(36),
    json_schema_content varchar(16000))
    RETURNS bigint unsigned

BEGIN
    DECLARE normal_schema_description_gid bigint unsigned;
    DECLARE table_schema_record_table_name_gid bigint unsigned;
    DECLARE schema_name_gid bigint unsigned;
    DECLARE insert_row_gid bigint unsigned;
    DECLARE description_gid bigint unsigned;
    SET normal_schema_description_gid = (SELECT global_id FROM string_content WHERE content = '插表模型名称');
    SET table_schema_record_table_name_gid =
        (SELECT global_id FROM string_content WHERE content = 'table_schema_record');
    # 插入 插表模型 的 描述
    SET description_gid = (SELECT insert_string_content_with_description_gid(
                                      description_uuid,
                                      description,
                                      normal_schema_description_gid));
    # 插入 插表模型 的 名称
    SET schema_name_gid = (SELECT insert_string_content_with_description_gid(
                                      schema_name_uuid,
                                      schema_name,
                                      description_gid));
    # 新增 一行全局ID
    SET insert_row_gid = (SELECT insert_global_data_record(
                                     json_schema_content_uuid,
                                     table_schema_record_table_name_gid,
                                     description_gid));
    # table_schema_record 表 新增一行数据
    INSERT INTO table_schema_record (global_id, schema_name, json_schema)
    VALUES (insert_row_gid, schema_name_gid, json_schema_content);

    RETURN insert_row_gid;
END $$

