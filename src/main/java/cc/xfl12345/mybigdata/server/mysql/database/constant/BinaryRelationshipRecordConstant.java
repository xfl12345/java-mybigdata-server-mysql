package cc.xfl12345.mybigdata.server.mysql.database.constant;

/**
 * 表名：binary_relationship_record
*/
@lombok.Data
@javax.persistence.Table(name = "binary_relationship_record")
public class BinaryRelationshipRecordConstant {
    /**
     * 当前表所在数据库实例里的全局ID
     */
    @javax.persistence.Column(name = "global_id", nullable = false)
    @javax.persistence.GeneratedValue(generator = "JDBC")
    private Long globalId;

    /**
     * 对象A
     */
    @javax.persistence.Column(name = "item_a", nullable = false)
    private Long itemA;

    /**
     * 对象B
     */
    @javax.persistence.Column(name = "item_b", nullable = false)
    private Long itemB;

    public static final String GLOBAL_ID = "globalId";

    public static final String DB_GLOBAL_ID = "global_id";

    public static final String ITEM_A = "itemA";

    public static final String DB_ITEM_A = "item_a";

    public static final String ITEM_B = "itemB";

    public static final String DB_ITEM_B = "item_b";
}
