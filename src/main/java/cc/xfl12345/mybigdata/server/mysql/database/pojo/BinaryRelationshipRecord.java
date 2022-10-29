package cc.xfl12345.mybigdata.server.mysql.database.pojo;

import java.io.Serializable;

/**
 * 表名：binary_relationship_record
 */
@lombok.Data
@lombok.experimental.SuperBuilder
@lombok.NoArgsConstructor
@lombok.experimental.FieldNameConstants
@io.swagger.annotations.ApiModel("")
@javax.persistence.Table(name = "binary_relationship_record")
@javax.persistence.Entity
@org.teasoft.bee.osql.annotation.Table("binary_relationship_record")
public class BinaryRelationshipRecord implements Cloneable, Serializable {
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
     * 对象A
     */
    @javax.persistence.Column(name = "item_a", nullable = false)
    @io.swagger.annotations.ApiModelProperty("对象A")
    @org.teasoft.bee.osql.annotation.Column("item_a")
    private Long itemA;

    /**
     * 对象B
     */
    @javax.persistence.Column(name = "item_b", nullable = false)
    @io.swagger.annotations.ApiModelProperty("对象B")
    @org.teasoft.bee.osql.annotation.Column("item_b")
    private Long itemB;

    private static final long serialVersionUID = 1L;

    @Override
    public BinaryRelationshipRecord clone() throws CloneNotSupportedException {
        return (BinaryRelationshipRecord) super.clone();
    }
}
