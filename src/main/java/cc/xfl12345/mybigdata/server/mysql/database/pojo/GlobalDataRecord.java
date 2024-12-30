package cc.xfl12345.mybigdata.server.mysql.database.pojo;

import cc.xfl12345.mybigdata.server.common.pojo.OpenCloneable;

import java.io.Serializable;
import java.util.Date;

/**
 * 表名：global_data_record
 * 表注释：全局ID记录表
*/
@lombok.Data
@lombok.experimental.SuperBuilder
@lombok.NoArgsConstructor
@lombok.experimental.FieldNameConstants
@io.swagger.annotations.ApiModel("全局ID记录表")
@jakarta.persistence.Table(name = "global_data_record")
@jakarta.persistence.Entity
public class GlobalDataRecord implements OpenCloneable, Serializable {
    /**
     * 当前表所在数据库实例里的全局ID
     */
    @jakarta.persistence.Id
    @jakarta.persistence.Column(name = "id", nullable = false)
    @jakarta.persistence.GeneratedValue(generator = "JDBC")
    @io.swagger.annotations.ApiModelProperty("当前表所在数据库实例里的全局ID")
    private Long id;

    /**
     * 关于某行数据的，整个MySQL数据库乃至全球唯一的真正的全局ID
     */
    @jakarta.persistence.Column(name = "uuid", nullable = false, length = 36)
    @io.swagger.annotations.ApiModelProperty("关于某行数据的，整个MySQL数据库乃至全球唯一的真正的全局ID")
    private String uuid;

    /**
     * 创建时间
     */
    @jakarta.persistence.Column(name = "create_time", nullable = true)
    @io.swagger.annotations.ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 修改时间
     */
    @jakarta.persistence.Column(name = "update_time", nullable = true)
    @io.swagger.annotations.ApiModelProperty("修改时间")
    private Date updateTime;

    /**
     * 修改次数（版本迭代）
     */
    @jakarta.persistence.Column(name = "modified_count", nullable = true)
    @io.swagger.annotations.ApiModelProperty("修改次数（版本迭代）")
    private Long modifiedCount;

    /**
     * 该行数据所在的表名
     */
    @jakarta.persistence.Column(name = "table_name", nullable = true)
    @io.swagger.annotations.ApiModelProperty("该行数据所在的表名")
    private Long tableName;

    /**
     * 该行数据的附加简述
     */
    @jakarta.persistence.Column(name = "description", nullable = true)
    @io.swagger.annotations.ApiModelProperty("该行数据的附加简述")
    private Long description;

    private static final long serialVersionUID = 1L;

    @Override
    public GlobalDataRecord clone() throws CloneNotSupportedException {
        return (GlobalDataRecord) super.clone();
    }
}
