package cc.xfl12345.mybigdata.server.mysql.spring.helper.url;

import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.HostInfo;
import lombok.Getter;
import lombok.Setter;

public class MysqlJdbcUrlParameter {
    @Getter
    @Setter
    protected String sqlServerDriverName = com.mysql.cj.jdbc.Driver.class.getCanonicalName();

    @Getter
    @Setter
    protected String sqlProtocal = "jdbc";

    @Getter
    @Setter
    protected String sqlSubProtocal = "mysql";

    @Getter
    @Setter
    protected String sqlServerAddress;

    @Getter
    @Setter
    protected Integer sqlServerPort;

    @Getter
    @Setter
    protected String databaseName;

    public MysqlJdbcUrlParameter() {
    }

    public MysqlJdbcUrlParameter(ConnectionUrl connectionUrl) {
        HostInfo hostInfo = connectionUrl.getMainHost();
        if (hostInfo == null) {
            sqlServerAddress = connectionUrl.getDefaultHost();
            sqlServerPort = connectionUrl.getDefaultPort();
            databaseName = "information_schema";
        } else {
            sqlServerAddress = hostInfo.getHost();
            sqlServerPort = hostInfo.getPort();
            databaseName = hostInfo.getDatabase();
        }
    }

    /**
     * 通过克隆来构造对象
     *
     * @param base 同一个类的对象
     */
    public MysqlJdbcUrlParameter(MysqlJdbcUrlParameter base) {
        this.sqlServerDriverName = base.getSqlServerDriverName();
        this.sqlProtocal = base.getSqlProtocal();
        this.sqlSubProtocal = base.getSqlSubProtocal();
        this.sqlServerAddress = base.getSqlServerAddress();
        this.sqlServerPort = base.getSqlServerPort();
        this.databaseName = base.getDatabaseName();
    }

}
