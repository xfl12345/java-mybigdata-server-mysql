package cc.xfl12345.mybigdata.server.mysql.database.pojo;

import java.io.Serializable;

/**
 * 表名：object_content
*/
@lombok.Data
@lombok.experimental.SuperBuilder
@lombok.NoArgsConstructor
@lombok.experimental.FieldNameConstants
@io.swagger.annotations.ApiModel("")
@javax.persistence.Table(name = "object_content")
@javax.persistence.Entity
@org.teasoft.bee.osql.annotation.Table("object_content")
public class ObjectContent implements Cloneable, Serializable {
    /**
     * 对象id
     */
    @javax.persistence.Column(name = "global_id", nullable = false)
    @javax.persistence.GeneratedValue(generator = "JDBC")
    @io.swagger.annotations.ApiModelProperty("对象id")
    @javax.persistence.Id
    @org.teasoft.bee.osql.annotation.Column("global_id")
    @org.teasoft.bee.osql.annotation.PrimaryKey
    private Long globalId;

    /**
     * 属性名称
     */
    @javax.persistence.Column(name = "the_key", nullable = false)
    @io.swagger.annotations.ApiModelProperty("属性名称")
    @org.teasoft.bee.osql.annotation.Column("the_key")
    private Long theKey;

    /**
     * 属性值
     */
    @javax.persistence.Column(name = "the_value", nullable = true)
    @io.swagger.annotations.ApiModelProperty("属性值")
    @org.teasoft.bee.osql.annotation.Column("the_value")
    private Long theValue;

    private static final long serialVersionUID = 1L;

    @Override
    public ObjectContent clone() throws CloneNotSupportedException {
        return (ObjectContent) super.clone();
    }
}
