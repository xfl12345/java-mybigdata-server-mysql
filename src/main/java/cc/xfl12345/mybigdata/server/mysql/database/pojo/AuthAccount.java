package cc.xfl12345.mybigdata.server.mysql.database.pojo;

import java.io.Serializable;

/**
 * 表名：auth_account
*/
@lombok.Data
@io.swagger.annotations.ApiModel("")
@javax.persistence.Table(name = "auth_account")
@javax.persistence.Entity
@org.teasoft.bee.osql.annotation.Table("auth_account")
public class AuthAccount implements Cloneable, Serializable {
    /**
     * 账号ID
     */
    @javax.persistence.Id
    @javax.persistence.Column(name = "account_id", nullable = false)
    @javax.persistence.GeneratedValue(generator = "JDBC")
    @io.swagger.annotations.ApiModelProperty("账号ID")
    @org.teasoft.bee.osql.annotation.Column("account_id")
    @org.teasoft.bee.osql.annotation.PrimaryKey
    private Long accountId;

    /**
     * 账号密码的哈希值
     */
    @javax.persistence.Column(name = "password_hash", nullable = false, length = 128)
    @io.swagger.annotations.ApiModelProperty("账号密码的哈希值")
    @org.teasoft.bee.osql.annotation.Column("password_hash")
    private String passwordHash;

    /**
     * 账号密码的哈希值计算的佐料
     */
    @javax.persistence.Column(name = "password_salt", nullable = false, length = 128)
    @io.swagger.annotations.ApiModelProperty("账号密码的哈希值计算的佐料")
    @org.teasoft.bee.osql.annotation.Column("password_salt")
    private String passwordSalt;

    /**
     * 账号额外信息
     */
    @javax.persistence.Column(name = "extra_info_id", nullable = true)
    @io.swagger.annotations.ApiModelProperty("账号额外信息")
    @org.teasoft.bee.osql.annotation.Column("extra_info_id")
    private Long extraInfoId;

    private static final long serialVersionUID = 1L;

    @Override
    public AuthAccount clone() throws CloneNotSupportedException {
        return (AuthAccount) super.clone();
    }
}
