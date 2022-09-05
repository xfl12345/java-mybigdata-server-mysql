package cc.xfl12345.mybigdata.server.mysql.database.constant;

import java.util.Date;

/**
 * 表名：global_data_record
*/
@lombok.Data
@javax.persistence.Table(name = "global_data_record")
public class GlobalDataRecordConstant {
    /**
     * 当前表所在数据库实例里的全局ID
     */
    @javax.persistence.Id
    @javax.persistence.GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 关于某行数据的，整个MySQL数据库乃至全球唯一的真正的全局ID
     */
    private String uuid;

    /**
     * 创建时间
     */
    @javax.persistence.Column(name = "create_time", nullable = true)
    private Date createTime;

    /**
     * 修改时间
     */
    @javax.persistence.Column(name = "update_time", nullable = true)
    private Date updateTime;

    /**
     * 修改次数（版本迭代）
     */
    @javax.persistence.Column(name = "modified_count", nullable = true)
    private Long modifiedCount;

    /**
     * 该行数据所在的表名
     */
    @javax.persistence.Column(name = "table_name", nullable = true)
    private Long tableName;

    /**
     * 该行数据的附加简述
     */
    private Long description;

    public static final String ID = "id";

    public static final String DB_ID = "id";

    public static final String UUID = "uuid";

    public static final String DB_UUID = "uuid";

    public static final String CREATE_TIME = "createTime";

    public static final String DB_CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "updateTime";

    public static final String DB_UPDATE_TIME = "update_time";

    public static final String MODIFIED_COUNT = "modifiedCount";

    public static final String DB_MODIFIED_COUNT = "modified_count";

    public static final String TABLE_NAME = "tableName";

    public static final String DB_TABLE_NAME = "table_name";

    public static final String DESCRIPTION = "description";

    public static final String DB_DESCRIPTION = "description";
}
