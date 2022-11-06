package cc.xfl12345.mybigdata.server.mysql.database.pojo;

import cc.xfl12345.mybigdata.server.common.api.OpenCloneable;

import java.io.Serializable;

/**
 * 表名：number_content
*/
@lombok.Data
@lombok.experimental.SuperBuilder
@lombok.NoArgsConstructor
@lombok.experimental.FieldNameConstants
@io.swagger.annotations.ApiModel("")
@javax.persistence.Table(name = "number_content")
@javax.persistence.Entity
public class NumberContent implements OpenCloneable, Serializable {
    /**
     * 当前表所在数据库实例里的全局ID
     */
    @javax.persistence.Column(name = "global_id", nullable = false)
    @javax.persistence.GeneratedValue(generator = "JDBC")
    @io.swagger.annotations.ApiModelProperty("当前表所在数据库实例里的全局ID")
    @javax.persistence.Id
    private Long globalId;

    /**
     * 是否为整数（无论长度）
     */
    @javax.persistence.Column(name = "numberIsInteger", nullable = false)
    @io.swagger.annotations.ApiModelProperty("是否为整数（无论长度）")
    private Boolean numberisinteger;

    /**
     * 是否为64bit整数
     */
    @javax.persistence.Column(name = "numberIs64bit", nullable = false)
    @io.swagger.annotations.ApiModelProperty("是否为64bit整数")
    private Boolean numberis64bit;

    /**
     * 字符串形式的十进制数字（最多760个字符）
     */
    @javax.persistence.Column(name = "content", nullable = false, length = 760)
    @io.swagger.annotations.ApiModelProperty("字符串形式的十进制数字（最多760个字符）")
    private String content;

    private static final long serialVersionUID = 1L;

    @Override
    public NumberContent clone() throws CloneNotSupportedException {
        return (NumberContent) super.clone();
    }
}
