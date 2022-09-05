package cc.xfl12345.mybigdata.server.mysql.database.constant;

/**
 * 表名：number_content
 */
@lombok.Data
@javax.persistence.Table(name = "number_content")
public class NumberContentConstant {
    /**
     * 当前表所在数据库实例里的全局ID
     */
    @javax.persistence.Column(name = "global_id", nullable = false)
    @javax.persistence.GeneratedValue(generator = "JDBC")
    private Long globalId;

    /**
     * 是否为整数（无论长度）
     */
    @javax.persistence.Column(name = "numberIsInteger", nullable = false)
    private Boolean numberisinteger;

    /**
     * 是否为64bit整数
     */
    @javax.persistence.Column(name = "numberIs64bit", nullable = false)
    private Boolean numberis64bit;

    /**
     * 字符串形式的十进制数字（最多760个字符）
     */
    private String content;

    public static final String GLOBAL_ID = "globalId";

    public static final String DB_GLOBAL_ID = "global_id";

    public static final String NUMBERISINTEGER = "numberisinteger";

    public static final String DB_NUMBERISINTEGER = "numberIsInteger";

    public static final String NUMBERIS64BIT = "numberis64bit";

    public static final String DB_NUMBERIS64BIT = "numberIs64bit";

    public static final String CONTENT = "content";

    public static final String DB_CONTENT = "content";
}
