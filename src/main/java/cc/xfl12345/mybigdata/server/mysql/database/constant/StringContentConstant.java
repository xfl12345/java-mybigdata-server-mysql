package cc.xfl12345.mybigdata.server.mysql.database.constant;

/**
 * 表名：string_content
*/
@lombok.Data
@javax.persistence.Table(name = "string_content")
public class StringContentConstant {
    /**
     * 当前表所在数据库实例里的全局ID
     */
    @javax.persistence.Column(name = "global_id", nullable = false)
    @javax.persistence.GeneratedValue(generator = "JDBC")
    private Long globalId;

    /**
     * 字符串结构格式
     */
    @javax.persistence.Column(name = "data_format", nullable = true)
    private Long dataFormat;

    /**
     * 字符串长度
     */
    @javax.persistence.Column(name = "content_length", nullable = false)
    private Short contentLength;

    /**
     * 字符串内容，最大长度为 768 个字符
     */
    private String content;

    public static final String GLOBAL_ID = "globalId";

    public static final String DB_GLOBAL_ID = "global_id";

    public static final String DATA_FORMAT = "dataFormat";

    public static final String DB_DATA_FORMAT = "data_format";

    public static final String CONTENT_LENGTH = "contentLength";

    public static final String DB_CONTENT_LENGTH = "content_length";

    public static final String CONTENT = "content";

    public static final String DB_CONTENT = "content";
}
