package cc.xfl12345.mybigdata.server.mysql.database.pojo;

import cc.xfl12345.mybigdata.server.common.pojo.OpenCloneable;

import java.io.Serializable;

/**
 * 表名：group_record
 * 表注释：专门记录 "JSON Array" 的表。不过这 group_record 表只记 组号
*/
@lombok.Data
@lombok.experimental.SuperBuilder
@lombok.NoArgsConstructor
@lombok.experimental.FieldNameConstants
@io.swagger.annotations.ApiModel("专门记录 \"JSON Array\" 的表。不过这 group_record 表只记 组号")
@jakarta.persistence.Table(name = "group_record")
@jakarta.persistence.Entity
public class GroupRecord implements OpenCloneable, Serializable {
    /**
     * 当前表所在数据库实例里的全局ID
     */
    @jakarta.persistence.Column(name = "global_id", nullable = false)
    @jakarta.persistence.GeneratedValue(generator = "JDBC")
    @io.swagger.annotations.ApiModelProperty("当前表所在数据库实例里的全局ID")
    @jakarta.persistence.Id
    private Long globalId;

    /**
     * 组名
     */
    @jakarta.persistence.Column(name = "group_name", nullable = false)
    @io.swagger.annotations.ApiModelProperty("组名")
    private Long groupName;

    /**
     * 元素是否都是唯一的（默认否）
     */
    @jakarta.persistence.Column(name = "unique_items", nullable = false)
    @io.swagger.annotations.ApiModelProperty("元素是否都是唯一的（默认否）")
    private Boolean uniqueItems;

    private static final long serialVersionUID = 1L;

    @Override
    public GroupRecord clone() throws CloneNotSupportedException {
        return (GroupRecord) super.clone();
    }
}
