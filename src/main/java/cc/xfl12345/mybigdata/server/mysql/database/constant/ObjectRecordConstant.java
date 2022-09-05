package cc.xfl12345.mybigdata.server.mysql.database.constant;

/**
 * 表名：object_record
*/
@lombok.Data
@javax.persistence.Table(name = "object_record")
public class ObjectRecordConstant {
    /**
     * 对象id
     */
    @javax.persistence.Column(name = "global_id", nullable = false)
    @javax.persistence.GeneratedValue(generator = "JDBC")
    private Long globalId;

    /**
     * 用于检验该对象的JSON Schema
     */
    @javax.persistence.Column(name = "object_schema", nullable = true)
    private Long objectSchema;

    /**
     * 对象名称
     */
    @javax.persistence.Column(name = "object_name", nullable = false)
    private Long objectName;

    public static final String GLOBAL_ID = "globalId";

    public static final String DB_GLOBAL_ID = "global_id";

    public static final String OBJECT_SCHEMA = "objectSchema";

    public static final String DB_OBJECT_SCHEMA = "object_schema";

    public static final String OBJECT_NAME = "objectName";

    public static final String DB_OBJECT_NAME = "object_name";
}
