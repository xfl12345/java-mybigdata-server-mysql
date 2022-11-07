package cc.xfl12345.mybigdata.server.mysql.database.pojo;

import cc.xfl12345.mybigdata.server.common.api.OpenCloneable;

import java.io.Serializable;

/**
 * 表名：auth_account
 * 表注释：账号表
*/
@lombok.Data
@lombok.experimental.SuperBuilder
@lombok.NoArgsConstructor
@lombok.experimental.FieldNameConstants
@io.swagger.annotations.ApiModel("账号表")
@javax.persistence.Table(name = "auth_account")
@javax.persistence.Entity
public class AuthAccount implements OpenCloneable, Serializable {
    /**
     * 账号ID
     */
    @javax.persistence.Id
    @javax.persistence.Column(name = "account_id", nullable = false)
    @javax.persistence.GeneratedValue(generator = "JDBC")
    @io.swagger.annotations.ApiModelProperty("账号ID")
    private Long accountId;

    /**
     * 账号密码的哈希值
     */
    @javax.persistence.Column(name = "password_hash", nullable = false, length = 128)
    @io.swagger.annotations.ApiModelProperty("账号密码的哈希值")
    private String passwordHash;

    /**
     * 账号密码的哈希值计算的佐料
     */
    @javax.persistence.Column(name = "password_salt", nullable = false, length = 128)
    @io.swagger.annotations.ApiModelProperty("账号密码的哈希值计算的佐料")
    private String passwordSalt;

    /**
     * 账号额外信息
     */
    @javax.persistence.Column(name = "extra_info_id", nullable = true)
    @io.swagger.annotations.ApiModelProperty("账号额外信息")
    private Long extraInfoId;

    private static final long serialVersionUID = 1L;

    @Override
    public AuthAccount clone() throws CloneNotSupportedException {
        return (AuthAccount) super.clone();
    }
}
