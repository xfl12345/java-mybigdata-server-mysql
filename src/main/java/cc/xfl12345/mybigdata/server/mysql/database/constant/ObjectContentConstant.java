package cc.xfl12345.mybigdata.server.mysql.database.constant;

/**
 * 表名：object_content
*/
@lombok.Data
@javax.persistence.Table(name = "object_content")
public class ObjectContentConstant {
    /**
     * 对象id
     */
    @javax.persistence.Column(name = "global_id", nullable = false)
    @javax.persistence.GeneratedValue(generator = "JDBC")
    private Long globalId;

    /**
     * 属性名称
     */
    @javax.persistence.Column(name = "the_key", nullable = false)
    private Long theKey;

    /**
     * 属性值
     */
    @javax.persistence.Column(name = "the_value", nullable = true)
    private Long theValue;

    public static final String GLOBAL_ID = "globalId";

    public static final String DB_GLOBAL_ID = "global_id";

    public static final String THE_KEY = "theKey";

    public static final String DB_THE_KEY = "the_key";

    public static final String THE_VALUE = "theValue";

    public static final String DB_THE_VALUE = "the_value";
}
