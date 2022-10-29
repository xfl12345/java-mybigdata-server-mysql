package cc.xfl12345.mybigdata.server.mysql.database.pojo;

import java.io.Serializable;

/**
 * 表名：object_record
 */
@lombok.Data
@lombok.experimental.SuperBuilder
@lombok.NoArgsConstructor
@lombok.experimental.FieldNameConstants
@io.swagger.annotations.ApiModel("")
@javax.persistence.Table(name = "object_record")
@javax.persistence.Entity
@org.teasoft.bee.osql.annotation.Table("object_record")
public class ObjectRecord implements Cloneable, Serializable {
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
     * 用于检验该对象的JSON Schema
     */
    @javax.persistence.Column(name = "object_schema", nullable = true)
    @io.swagger.annotations.ApiModelProperty("用于检验该对象的JSON Schema")
    @org.teasoft.bee.osql.annotation.Column("object_schema")
    private Long objectSchema;

    /**
     * 对象名称
     */
    @javax.persistence.Column(name = "object_name", nullable = false)
    @io.swagger.annotations.ApiModelProperty("对象名称")
    @org.teasoft.bee.osql.annotation.Column("object_name")
    private Long objectName;

    private static final long serialVersionUID = 1L;

    @Override
    public ObjectRecord clone() throws CloneNotSupportedException {
        return (ObjectRecord) super.clone();
    }
}
