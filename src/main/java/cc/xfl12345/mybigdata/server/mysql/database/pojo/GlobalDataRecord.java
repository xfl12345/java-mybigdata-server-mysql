package cc.xfl12345.mybigdata.server.mysql.database.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 表名：global_data_record
 */
@lombok.Data
@lombok.experimental.SuperBuilder
@lombok.NoArgsConstructor
@lombok.experimental.FieldNameConstants
@io.swagger.annotations.ApiModel("")
@javax.persistence.Table(name = "global_data_record")
@javax.persistence.Entity
@org.teasoft.bee.osql.annotation.Table("global_data_record")
public class GlobalDataRecord implements Cloneable, Serializable {
    /**
     * 当前表所在数据库实例里的全局ID
     */
    @javax.persistence.Id
    @javax.persistence.Column(name = "id", nullable = false)
    @javax.persistence.GeneratedValue(generator = "JDBC")
    @io.swagger.annotations.ApiModelProperty("当前表所在数据库实例里的全局ID")
    @org.teasoft.bee.osql.annotation.Column("id")
    @org.teasoft.bee.osql.annotation.PrimaryKey
    private Long id;

    /**
     * 关于某行数据的，整个MySQL数据库乃至全球唯一的真正的全局ID
     */
    @javax.persistence.Column(name = "uuid", nullable = false, length = 36)
    @io.swagger.annotations.ApiModelProperty("关于某行数据的，整个MySQL数据库乃至全球唯一的真正的全局ID")
    @org.teasoft.bee.osql.annotation.Column("uuid")
    private String uuid;

    /**
     * 创建时间
     */
    @javax.persistence.Column(name = "create_time", nullable = true)
    @io.swagger.annotations.ApiModelProperty("创建时间")
    @org.teasoft.bee.osql.annotation.Column("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @javax.persistence.Column(name = "update_time", nullable = true)
    @io.swagger.annotations.ApiModelProperty("修改时间")
    @org.teasoft.bee.osql.annotation.Column("update_time")
    private Date updateTime;

    /**
     * 修改次数（版本迭代）
     */
    @javax.persistence.Column(name = "modified_count", nullable = true)
    @io.swagger.annotations.ApiModelProperty("修改次数（版本迭代）")
    @org.teasoft.bee.osql.annotation.Column("modified_count")
    private Long modifiedCount;

    /**
     * 该行数据所在的表名
     */
    @javax.persistence.Column(name = "table_name", nullable = true)
    @io.swagger.annotations.ApiModelProperty("该行数据所在的表名")
    @org.teasoft.bee.osql.annotation.Column("table_name")
    private Long tableName;

    /**
     * 该行数据的附加简述
     */
    @javax.persistence.Column(name = "description", nullable = true)
    @io.swagger.annotations.ApiModelProperty("该行数据的附加简述")
    @org.teasoft.bee.osql.annotation.Column("description")
    private Long description;

    private static final long serialVersionUID = 1L;

    @Override
    public GlobalDataRecord clone() throws CloneNotSupportedException {
        return (GlobalDataRecord) super.clone();
    }
}
