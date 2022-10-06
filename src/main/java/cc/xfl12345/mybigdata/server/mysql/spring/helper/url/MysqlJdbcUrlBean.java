package cc.xfl12345.mybigdata.server.mysql.spring.helper.url;

import com.mysql.cj.conf.*;

import java.util.*;

public class MysqlJdbcUrlBean {
    private final ConnectionUrl originURL;

    public MysqlJdbcUrlBean(ConnectionUrl jdbcURL) {
        originURL = jdbcURL;

        type = originURL.getType();
        ConnectionUrlParser connectionUrlParser = ConnectionUrlParser.parseConnectionString(jdbcURL.getDatabaseUrl());
        authority = connectionUrlParser.getAuthority();
        databaseName = connectionUrlParser.getPath();

        Properties originConnectionArguments = originURL.getConnectionArgumentsAsProperties();
        connectionArguments = new HashMap<>(originConnectionArguments.size());
        originConnectionArguments.entrySet().parallelStream().forEach(
            item -> connectionArguments.put(PropertyKey.fromValue((String) item.getKey()), (String) item.getValue())
        );

        connectionArguments.remove(null);
    }

    public ConnectionUrl getOriginURL() {
        return originURL;
    }


    protected ConnectionUrl.Type type;

    protected String authority;

    protected String databaseName;

    protected final Map<PropertyKey, String> connectionArguments;

    public void setType(ConnectionUrl.Type type) {
        this.type = type;
    }

    public ConnectionUrl.Type getType() {
        return type;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public Map<PropertyKey, String> getReadOnlyConnectionArguments() {
        return Collections.unmodifiableMap(connectionArguments);
    }

    public Map<PropertyKey, String> getConnectionArguments() {
        return connectionArguments;
    }

    public String buildURL() {
        StringBuffer queryBuffer = new StringBuffer();
        connectionArguments.entrySet().parallelStream().forEach(
            item -> {
                String tmp = item.getKey().getKeyName() + "=" + item.getValue() + '&';
                queryBuffer.append(tmp);
            }
        );

        if (queryBuffer.length() > 0) {
            queryBuffer.deleteCharAt(queryBuffer.length() - 1);
        }

        return type.getScheme() + "//" + authority + "/" + databaseName + "?" + queryBuffer;
    }
}
