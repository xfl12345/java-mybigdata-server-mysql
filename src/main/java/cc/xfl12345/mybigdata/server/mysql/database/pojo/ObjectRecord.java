package cc.xfl12345.mybigdata.server.mysql.database.pojo;

import cc.xfl12345.mybigdata.server.common.api.OpenCloneable;

import java.io.Serializable;

/**
 * 表名：object_record
 * 表注释：专门记录 “字典” 的表。不过这 object_record 表只记 对象号
*/
@lombok.Data
@lombok.experimental.SuperBuilder
@lombok.NoArgsConstructor
@lombok.experimental.FieldNameConstants
@io.swagger.annotations.ApiModel("专门记录 “字典” 的表。不过这 object_record 表只记 对象号")
@javax.persistence.Table(name = "object_record")
@javax.persistence.Entity
public class ObjectRecord implements OpenCloneable, Serializable {
    /**
     * 对象id
     */
    @javax.persistence.Column(name = "global_id", nullable = false)
    @javax.persistence.GeneratedValue(generator = "JDBC")
    @io.swagger.annotations.ApiModelProperty("对象id")
    @javax.persistence.Id
    private Long globalId;

    /**
     * 用于检验该对象的JSON Schema
     */
    @javax.persistence.Column(name = "object_schema", nullable = false)
    @io.swagger.annotations.ApiModelProperty("用于检验该对象的JSON Schema")
    private Long objectSchema;

    /**
     * JSON Schema 的 相对路径（用于检验子对象）
     */
    @javax.persistence.Column(name = "schema_path", nullable = false)
    @io.swagger.annotations.ApiModelProperty("JSON Schema 的 相对路径（用于检验子对象）")
    private Long schemaPath;

    /**
     * 对象名称
     */
    @javax.persistence.Column(name = "object_name", nullable = false)
    @io.swagger.annotations.ApiModelProperty("对象名称")
    private Long objectName;

    private static final long serialVersionUID = 1L;

    @Override
    public ObjectRecord clone() throws CloneNotSupportedException {
        return (ObjectRecord) super.clone();
    }
}
