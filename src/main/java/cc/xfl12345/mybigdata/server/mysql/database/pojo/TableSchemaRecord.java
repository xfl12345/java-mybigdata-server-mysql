package cc.xfl12345.mybigdata.server.mysql.database.pojo;

import cc.xfl12345.mybigdata.server.common.api.OpenCloneable;

import java.io.Serializable;

/**
 * 表名：table_schema_record
*/
@lombok.Data
@lombok.experimental.SuperBuilder
@lombok.NoArgsConstructor
@lombok.experimental.FieldNameConstants
@io.swagger.annotations.ApiModel("")
@javax.persistence.Table(name = "table_schema_record")
@javax.persistence.Entity
public class TableSchemaRecord implements OpenCloneable, Serializable {
    /**
     * 当前表所在数据库实例里的全局ID
     */
    @javax.persistence.Column(name = "global_id", nullable = false)
    @javax.persistence.GeneratedValue(generator = "JDBC")
    @io.swagger.annotations.ApiModelProperty("当前表所在数据库实例里的全局ID")
    @javax.persistence.Id
    private Long globalId;

    /**
     * 插表模型名称
     */
    @javax.persistence.Column(name = "schema_name", nullable = false)
    @io.swagger.annotations.ApiModelProperty("插表模型名称")
    private Long schemaName;

    /**
     * json_schema 字段的长度
     */
    @javax.persistence.Column(name = "content_length", nullable = false)
    @io.swagger.annotations.ApiModelProperty("json_schema 字段的长度")
    private Short contentLength;

    /**
     * 插表模型
     */
    @javax.persistence.Column(name = "json_schema", nullable = false, length = 16000)
    @io.swagger.annotations.ApiModelProperty("插表模型")
    private String jsonSchema;

    private static final long serialVersionUID = 1L;

    @Override
    public TableSchemaRecord clone() throws CloneNotSupportedException {
        return (TableSchemaRecord) super.clone();
    }
}
