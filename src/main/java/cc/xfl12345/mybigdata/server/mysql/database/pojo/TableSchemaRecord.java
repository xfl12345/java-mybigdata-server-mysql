package cc.xfl12345.mybigdata.server.mysql.database.pojo;

import cc.xfl12345.mybigdata.server.common.pojo.OpenCloneable;

import java.io.Serializable;

/**
 * 表名：table_schema_record
 * 表注释：MyBigData 表模型
*/
@lombok.Data
@lombok.experimental.SuperBuilder
@lombok.NoArgsConstructor
@lombok.experimental.FieldNameConstants
@io.swagger.annotations.ApiModel("MyBigData 表模型")
@jakarta.persistence.Table(name = "table_schema_record")
@jakarta.persistence.Entity
public class TableSchemaRecord implements OpenCloneable, Serializable {
    /**
     * 当前表所在数据库实例里的全局ID
     */
    @jakarta.persistence.Column(name = "global_id", nullable = false)
    @jakarta.persistence.GeneratedValue(generator = "JDBC")
    @io.swagger.annotations.ApiModelProperty("当前表所在数据库实例里的全局ID")
    @jakarta.persistence.Id
    private Long globalId;

    /**
     * 插表模型名称
     */
    @jakarta.persistence.Column(name = "schema_name", nullable = false)
    @io.swagger.annotations.ApiModelProperty("插表模型名称")
    private Long schemaName;

    /**
     * 插表模型
     */
    @jakarta.persistence.Column(name = "json_schema", nullable = false, length = 16000)
    @io.swagger.annotations.ApiModelProperty("插表模型")
    private String jsonSchema;

    private static final long serialVersionUID = 1L;

    @Override
    public TableSchemaRecord clone() throws CloneNotSupportedException {
        return (TableSchemaRecord) super.clone();
    }
}
