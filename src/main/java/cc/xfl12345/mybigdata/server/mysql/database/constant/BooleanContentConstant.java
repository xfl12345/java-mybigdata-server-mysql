package cc.xfl12345.mybigdata.server.mysql.database.constant;

/**
 * 表名：boolean_content
*/
@lombok.Data
@javax.persistence.Table(name = "boolean_content")
public class BooleanContentConstant {
    /**
     * 当前表所在数据库实例里的全局ID
     */
    @javax.persistence.Column(name = "global_id", nullable = false)
    @javax.persistence.GeneratedValue(generator = "JDBC")
    private Long globalId;

    /**
     * 布尔值
     */
    private Boolean content;

    public static final String GLOBAL_ID = "globalId";

    public static final String DB_GLOBAL_ID = "global_id";

    public static final String CONTENT = "content";

    public static final String DB_CONTENT = "content";
}
