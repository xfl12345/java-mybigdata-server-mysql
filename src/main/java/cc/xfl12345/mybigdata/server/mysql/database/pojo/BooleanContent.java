package cc.xfl12345.mybigdata.server.mysql.database.pojo;

import cc.xfl12345.mybigdata.server.common.pojo.OpenCloneable;

import java.io.Serializable;

/**
 * 表名：boolean_content
 * 表注释：专门记录 "JSON Boolean" 的表
*/
@lombok.Data
@lombok.experimental.SuperBuilder
@lombok.NoArgsConstructor
@lombok.experimental.FieldNameConstants
@io.swagger.annotations.ApiModel("专门记录 \"JSON Boolean\" 的表")
@jakarta.persistence.Table(name = "boolean_content")
@jakarta.persistence.Entity
public class BooleanContent implements OpenCloneable, Serializable {
    /**
     * 当前表所在数据库实例里的全局ID
     */
    @jakarta.persistence.Column(name = "global_id", nullable = false)
    @jakarta.persistence.GeneratedValue(generator = "JDBC")
    @io.swagger.annotations.ApiModelProperty("当前表所在数据库实例里的全局ID")
    @jakarta.persistence.Id
    private Long globalId;

    /**
     * 布尔值
     */
    @jakarta.persistence.Column(name = "content", nullable = false)
    @io.swagger.annotations.ApiModelProperty("布尔值")
    private Boolean content;

    private static final long serialVersionUID = 1L;

    @Override
    public BooleanContent clone() throws CloneNotSupportedException {
        return (BooleanContent) super.clone();
    }
}
