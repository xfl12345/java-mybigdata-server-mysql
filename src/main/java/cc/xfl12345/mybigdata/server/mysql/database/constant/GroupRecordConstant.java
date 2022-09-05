package cc.xfl12345.mybigdata.server.mysql.database.constant;

/**
 * 表名：group_record
*/
@lombok.Data
@javax.persistence.Table(name = "group_record")
public class GroupRecordConstant {
    /**
     * 当前表所在数据库实例里的全局ID
     */
    @javax.persistence.Column(name = "global_id", nullable = false)
    @javax.persistence.GeneratedValue(generator = "JDBC")
    private Long globalId;

    /**
     * 组名
     */
    @javax.persistence.Column(name = "group_name", nullable = false)
    private Long groupName;

    public static final String GLOBAL_ID = "globalId";

    public static final String DB_GLOBAL_ID = "global_id";

    public static final String GROUP_NAME = "groupName";

    public static final String DB_GROUP_NAME = "group_name";
}
