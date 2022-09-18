package cc.xfl12345.mybigdata.server.mysql.spring.helper.url;

import com.mysql.cj.conf.ConnectionUrl;

import java.util.Properties;

public class MysqlJdbcUrlHelper extends MysqlJdbcUrlParameter {
    protected Properties additionalParameters;

    public MysqlJdbcUrlHelper() {
        super();
    }

    public MysqlJdbcUrlHelper(ConnectionUrl connectionUrl) {
        super(connectionUrl);
        this.additionalParameters = connectionUrl.getConnectionArgumentsAsProperties();
    }

    public MysqlJdbcUrlHelper(MysqlJdbcUrlParameter base) {
        super(base);
    }

    /**
     * 是否更新了部分属性的值？
     */
    protected boolean is_updated = false;

    /**
     * JDBC URL
     */
    private String sqlConnectionUrl = null;

    /**
     * 构造并返回JDBC URL的基础URL（不包含数据库名称、不带附加参数）
     *
     * @return JDBC URL的基础URL
     */
    public String getSqlConnectionBaseUrl() {
        if (is_updated || sqlConnectionUrl == null) {
            sqlConnectionUrl = (protocal + ":" +
                subProtocal + "://" +
                host + ":" +
                port + "/");
            is_updated = false;
        }
        return sqlConnectionUrl;
    }

    /**
     * 构造并返回默认的不带附加参数JDBC URL
     *
     * @return JDBC URL
     */
    public String getSqlConnectionUrl() {
        return getSqlConnectionUrl(null);
    }

    /**
     * 指定数据库名称，构造并返回完整的不带附加参数JDBC URL
     *
     * @param dbName 数据库名称
     * @return JDBC URL
     */
    public String getSqlConnectionUrl(String dbName) {
        if (dbName == null)
            return getSqlConnectionBaseUrl() + databaseName;
        return getSqlConnectionBaseUrl() + dbName;
    }

    /**
     * 构造并返回JDBC URL里的附加参数
     *
     * @param confProp JDBC URL的附加参数
     * @return 以字符串的形式返回JDBC URL里的附加参数
     */
    public static String getAdditionalParametersAsString(Properties confProp) {
        StringBuilder sqlUrlParamBuilder = new StringBuilder("");
        if (!(confProp == null || confProp.stringPropertyNames().isEmpty())) {
            for (String propName : confProp.stringPropertyNames()) {
                sqlUrlParamBuilder.append(propName).append('=').append(confProp.getProperty(propName)).append('&');
            }
            int len = sqlUrlParamBuilder.length();
            if (sqlUrlParamBuilder.charAt(len - 1) == '&')
                sqlUrlParamBuilder.deleteCharAt(len - 1);
        }
        return sqlUrlParamBuilder.toString();
    }

    /**
     * 使用内部变量，构造并返回JDBC URL里的附加参数
     *
     * @return 以字符串的形式返回JDBC URL里的附加参数
     */
    public String getAdditionalParametersAsString() {
        return getAdditionalParametersAsString(this.additionalParameters);
    }

    /**
     * 构造并返回完整的带附加参数的JDBC URL
     *
     * @return JDBC URL
     */
    public String getSqlConnUrlWithConfigProp() {
        return getSqlConnUrlWithConfigProp(null);
    }

    /**
     * 构造并返回完整的带附加参数的JDBC URL
     *
     * @param confProp JDBC URL的附加参数
     * @return JDBC URL
     */
    public String getSqlConnUrlWithConfigProp(Properties confProp) {
        return getSqlConnUrlWithConfigProp(null, confProp == null ? getAdditionalParameters() : confProp);
    }

    /**
     * 指定数据库名称，构造并返回完整的带附加参数的JDBC URL
     *
     * @param confProp JDBC URL的附加参数
     * @return JDBC URL
     */
    public String getSqlConnUrlWithConfigProp(String dbName, Properties confProp) {
        return getSqlConnectionUrl(dbName) + "?" + getAdditionalParametersAsString(confProp);
    }

    @Override
    public void setDriverName(String driverName) {
        super.setDriverName(driverName);
        is_updated = true;
    }

    @Override
    public void setProtocal(String protocal) {
        super.setProtocal(protocal);
        is_updated = true;
    }

    @Override
    public void setSubProtocal(String subProtocal) {
        super.setSubProtocal(subProtocal);
        is_updated = true;
    }

    @Override
    public void setHost(String host) {
        super.setHost(host);
        is_updated = true;
    }

    @Override
    public void setPort(Integer port) {
        super.setPort(port);
        is_updated = true;
    }

    @Override
    public void setDatabaseName(String databaseName) {
        super.setDatabaseName(databaseName);
        is_updated = true;
    }

    public Properties getAdditionalParameters() {
        return additionalParameters;
    }

    public void setAdditionalParameters(Properties additionalParameters) {
        this.additionalParameters = additionalParameters;
    }
}
