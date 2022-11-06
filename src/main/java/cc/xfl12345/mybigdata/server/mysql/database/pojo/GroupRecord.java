package cc.xfl12345.mybigdata.server.mysql.database.pojo;

import cc.xfl12345.mybigdata.server.common.api.OpenCloneable;

import java.io.Serializable;

/**
 * 表名：group_record
*/
@lombok.Data
@lombok.experimental.SuperBuilder
@lombok.NoArgsConstructor
@lombok.experimental.FieldNameConstants
@io.swagger.annotations.ApiModel("")
@javax.persistence.Table(name = "group_record")
@javax.persistence.Entity
@org.teasoft.bee.osql.annotation.Table("group_record")
public class GroupRecord implements OpenCloneable, Serializable {
    /**
     * 当前表所在数据库实例里的全局ID
     */
    @javax.persistence.Column(name = "global_id", nullable = false)
    @javax.persistence.GeneratedValue(generator = "JDBC")
    @io.swagger.annotations.ApiModelProperty("当前表所在数据库实例里的全局ID")
    @javax.persistence.Id
    @org.teasoft.bee.osql.annotation.Column("global_id")
    @org.teasoft.bee.osql.annotation.PrimaryKey
    private Long globalId;

    /**
     * 组名
     */
    @javax.persistence.Column(name = "group_name", nullable = false)
    @io.swagger.annotations.ApiModelProperty("组名")
    @org.teasoft.bee.osql.annotation.Column("group_name")
    private Long groupName;

    /**
     * 元素是否都是唯一的（默认否）
     */
    @javax.persistence.Column(name = "unique_items", nullable = false)
    @io.swagger.annotations.ApiModelProperty("元素是否都是唯一的（默认否）")
    @org.teasoft.bee.osql.annotation.Column("unique_items")
    private Boolean uniqueItems;

    private static final long serialVersionUID = 1L;

    @Override
    public GroupRecord clone() throws CloneNotSupportedException {
        return (GroupRecord) super.clone();
    }
}
