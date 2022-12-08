package cc.xfl12345.mybigdata.server.pojo;

import lombok.Data;

@Data
public class DataSourceConfig {
    protected String username;

    protected String password;

    protected String url;

    protected String driverClassName;
}
