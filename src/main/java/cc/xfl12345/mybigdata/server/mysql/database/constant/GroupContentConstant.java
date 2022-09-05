package cc.xfl12345.mybigdata.server.mysql.database.constant;

/**
 * 表名：group_content
*/
@lombok.Data
@javax.persistence.Table(name = "group_content")
public class GroupContentConstant {
    /**
     * 组id
     */
    @javax.persistence.Column(name = "global_id", nullable = false)
    @javax.persistence.GeneratedValue(generator = "JDBC")
    private Long globalId;

    /**
     * 组内对象的下标
     */
    @javax.persistence.Column(name = "item_index", nullable = true)
    private Long itemIndex;

    /**
     * 组内对象
     */
    private Long item;

    public static final String GLOBAL_ID = "globalId";

    public static final String DB_GLOBAL_ID = "global_id";

    public static final String ITEM_INDEX = "itemIndex";

    public static final String DB_ITEM_INDEX = "item_index";

    public static final String ITEM = "item";

    public static final String DB_ITEM = "item";
}
