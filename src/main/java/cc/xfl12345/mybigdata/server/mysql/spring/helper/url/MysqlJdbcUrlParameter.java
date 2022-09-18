package cc.xfl12345.mybigdata.server.mysql.spring.helper.url;

import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.HostInfo;
import lombok.Getter;
import lombok.Setter;

public class MysqlJdbcUrlParameter {
    @Getter
    @Setter
    protected String driverName = com.mysql.cj.jdbc.Driver.class.getCanonicalName();

    @Getter
    @Setter
    protected String protocal = "jdbc";

    @Getter
    @Setter
    protected String subProtocal = "mysql";

    @Getter
    @Setter
    protected String host;

    @Getter
    @Setter
    protected Integer port;

    @Getter
    @Setter
    protected String databaseName;

    public MysqlJdbcUrlParameter() {
    }

    public MysqlJdbcUrlParameter(ConnectionUrl connectionUrl) {
        HostInfo hostInfo = connectionUrl.getMainHost();
        if (hostInfo == null) {
            host = connectionUrl.getDefaultHost();
            port = connectionUrl.getDefaultPort();
            databaseName = "information_schema";
        } else {
            host = hostInfo.getHost();
            port = hostInfo.getPort();
            databaseName = hostInfo.getDatabase();
        }
    }

    public static MysqlJdbcUrlParameter getInstance(ConnectionUrl connectionUrl) {
        MysqlJdbcUrlParameter mysqlJdbcUrlParameter = new MysqlJdbcUrlParameter();
        HostInfo hostInfo = connectionUrl.getMainHost();
        if (hostInfo == null) {
            mysqlJdbcUrlParameter.setHost(connectionUrl.getDefaultHost());
            mysqlJdbcUrlParameter.setPort(connectionUrl.getDefaultPort());
            mysqlJdbcUrlParameter.setDatabaseName("information_schema");
        } else {
            mysqlJdbcUrlParameter.setHost(hostInfo.getHost());
            mysqlJdbcUrlParameter.setPort(hostInfo.getPort());
            mysqlJdbcUrlParameter.setDatabaseName(hostInfo.getDatabase());
        }

        return mysqlJdbcUrlParameter;
    }

    /**
     * 通过克隆来构造对象
     *
     * @param base 同一个类的对象
     */
    public MysqlJdbcUrlParameter(MysqlJdbcUrlParameter base) {
        this.driverName = base.getDriverName();
        this.protocal = base.getProtocal();
        this.subProtocal = base.getSubProtocal();
        this.host = base.getHost();
        this.port = base.getPort();
        this.databaseName = base.getDatabaseName();
    }

}
