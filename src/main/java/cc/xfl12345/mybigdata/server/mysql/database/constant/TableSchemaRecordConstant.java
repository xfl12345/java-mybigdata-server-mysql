package cc.xfl12345.mybigdata.server.mysql.database.constant;

/**
 * 表名：table_schema_record
*/
@lombok.Data
@javax.persistence.Table(name = "table_schema_record")
public class TableSchemaRecordConstant {
    /**
     * 当前表所在数据库实例里的全局ID
     */
    @javax.persistence.Column(name = "global_id", nullable = false)
    @javax.persistence.GeneratedValue(generator = "JDBC")
    private Long globalId;

    /**
     * 插表模型名称
     */
    @javax.persistence.Column(name = "schema_name", nullable = false)
    private Long schemaName;

    /**
     * json_schema 字段的长度
     */
    @javax.persistence.Column(name = "content_length", nullable = false)
    private Short contentLength;

    /**
     * 插表模型
     */
    @javax.persistence.Column(name = "json_schema", nullable = false, length = 16000)
    private String jsonSchema;

    public static final String GLOBAL_ID = "globalId";

    public static final String DB_GLOBAL_ID = "global_id";

    public static final String SCHEMA_NAME = "schemaName";

    public static final String DB_SCHEMA_NAME = "schema_name";

    public static final String CONTENT_LENGTH = "contentLength";

    public static final String DB_CONTENT_LENGTH = "content_length";

    public static final String JSON_SCHEMA = "jsonSchema";

    public static final String DB_JSON_SCHEMA = "json_schema";
}
