package cc.xfl12345.mybigdata.server.mysql.database.constant;

/**
 * 表名：tree_struct_record
*/
@lombok.Data
@javax.persistence.Table(name = "tree_struct_record")
public class TreeStructRecordConstant {
    /**
     * 当前表所在数据库实例里的全局ID
     */
    @javax.persistence.Column(name = "global_id", nullable = false)
    @javax.persistence.GeneratedValue(generator = "JDBC")
    private Long globalId;

    /**
     * 根节点对象
     */
    @javax.persistence.Column(name = "root_id", nullable = false)
    private Long rootId;

    /**
     * 整个树的节点个数
     */
    @javax.persistence.Column(name = "item_count", nullable = false)
    private Integer itemCount;

    /**
     * 整个树的深度（有几层）
     */
    @javax.persistence.Column(name = "tree_deep", nullable = false)
    private Integer treeDeep;

    /**
     * JSON文本长度
     */
    @javax.persistence.Column(name = "content_length", nullable = false)
    private Short contentLength;

    /**
     * 以JSON字符串形式记载树形结构
     */
    @javax.persistence.Column(name = "struct_data", nullable = false, length = 16000)
    private String structData;

    public static final String GLOBAL_ID = "globalId";

    public static final String DB_GLOBAL_ID = "global_id";

    public static final String ROOT_ID = "rootId";

    public static final String DB_ROOT_ID = "root_id";

    public static final String ITEM_COUNT = "itemCount";

    public static final String DB_ITEM_COUNT = "item_count";

    public static final String TREE_DEEP = "treeDeep";

    public static final String DB_TREE_DEEP = "tree_deep";

    public static final String CONTENT_LENGTH = "contentLength";

    public static final String DB_CONTENT_LENGTH = "content_length";

    public static final String STRUCT_DATA = "structData";

    public static final String DB_STRUCT_DATA = "struct_data";
}
