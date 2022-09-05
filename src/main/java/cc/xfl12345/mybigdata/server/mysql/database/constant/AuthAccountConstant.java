package cc.xfl12345.mybigdata.server.mysql.database.constant;

/**
 * 表名：auth_account
*/
@lombok.Data
@javax.persistence.Table(name = "auth_account")
public class AuthAccountConstant {
    /**
     * 账号ID
     */
    @javax.persistence.Id
    @javax.persistence.Column(name = "account_id", nullable = false)
    @javax.persistence.GeneratedValue(generator = "JDBC")
    private Long accountId;

    /**
     * 账号密码的哈希值
     */
    @javax.persistence.Column(name = "password_hash", nullable = false, length = 128)
    private String passwordHash;

    /**
     * 账号密码的哈希值计算的佐料
     */
    @javax.persistence.Column(name = "password_salt", nullable = false, length = 128)
    private String passwordSalt;

    /**
     * 账号额外信息
     */
    @javax.persistence.Column(name = "extra_info_id", nullable = true)
    private Long extraInfoId;

    public static final String ACCOUNT_ID = "accountId";

    public static final String DB_ACCOUNT_ID = "account_id";

    public static final String PASSWORD_HASH = "passwordHash";

    public static final String DB_PASSWORD_HASH = "password_hash";

    public static final String PASSWORD_SALT = "passwordSalt";

    public static final String DB_PASSWORD_SALT = "password_salt";

    public static final String EXTRA_INFO_ID = "extraInfoId";

    public static final String DB_EXTRA_INFO_ID = "extra_info_id";
}
