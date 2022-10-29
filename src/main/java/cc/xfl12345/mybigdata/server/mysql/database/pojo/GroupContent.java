package cc.xfl12345.mybigdata.server.mysql.database.pojo;

import java.io.Serializable;

/**
 * 表名：group_content
 */
@lombok.Data
@lombok.experimental.SuperBuilder
@lombok.NoArgsConstructor
@lombok.experimental.FieldNameConstants
@io.swagger.annotations.ApiModel("")
@javax.persistence.Table(name = "group_content")
@javax.persistence.Entity
@org.teasoft.bee.osql.annotation.Table("group_content")
public class GroupContent implements Cloneable, Serializable {
    /**
     * 组id
     */
    @javax.persistence.Column(name = "global_id", nullable = false)
    @javax.persistence.GeneratedValue(generator = "JDBC")
    @io.swagger.annotations.ApiModelProperty("组id")
    @javax.persistence.Id
    @org.teasoft.bee.osql.annotation.Column("global_id")
    @org.teasoft.bee.osql.annotation.PrimaryKey
    private Long globalId;

    /**
     * 组内对象的下标
     */
    @javax.persistence.Column(name = "item_index", nullable = false)
    @io.swagger.annotations.ApiModelProperty("组内对象的下标")
    @org.teasoft.bee.osql.annotation.Column("item_index")
    private Long itemIndex;

    /**
     * 组内对象
     */
    @javax.persistence.Column(name = "item", nullable = false)
    @io.swagger.annotations.ApiModelProperty("组内对象")
    @org.teasoft.bee.osql.annotation.Column("item")
    private Long item;

    private static final long serialVersionUID = 1L;

    @Override
    public GroupContent clone() throws CloneNotSupportedException {
        return (GroupContent) super.clone();
    }
}
