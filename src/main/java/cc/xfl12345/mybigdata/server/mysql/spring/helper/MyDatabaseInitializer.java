package cc.xfl12345.mybigdata.server.mysql.spring.helper;

import cc.xfl12345.mybigdata.server.common.appconst.CommonConst;
import cc.xfl12345.mybigdata.server.mysql.sql.PackageLandmark;
import cc.xfl12345.mybigdata.server.mysql.util.MysqlJdbcUrlBean;
import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.cj.PreparedQuery;
import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.jdbc.ClientPreparedStatement;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class MyDatabaseInitializer {
    protected String username;
    protected String password;
    protected String url;
    protected String driverClassName;

    public String getUsername() {
        return username;
    }

    @Value("${spring.datasource.username}")
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    @Value("${spring.datasource.password}")
    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    @Value("${spring.datasource.url}")
    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    @Value("${spring.datasource.driver-class-name}")
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    @PostConstruct
    public void init() throws Exception {
        String mysqlJdbcProtocolHeader = "jdbc:mysql://";
        if (url != null && url.startsWith(mysqlJdbcProtocolHeader)) {
            initMySQL();
        }
    }

    public static String getSql(PreparedStatement preparedStatement) {
        String sql;
        ClientPreparedStatement unwarpedPreparedStatement;
        try {
            unwarpedPreparedStatement = preparedStatement.unwrap(ClientPreparedStatement.class);
            sql = ((PreparedQuery) unwarpedPreparedStatement.getQuery()).asSql();
        } catch (Exception e) {
            try {
                Method method = preparedStatement.getClass().getMethod("asSql");
                sql = (String) method.invoke(preparedStatement);
            } catch (Exception exception) {
                sql = preparedStatement.toString();
            }
        }

        return sql;
    }

    protected void logExecutingSQL(String sql) {
        log.info("Executing SQL: [" + sql + ']');
    }

    protected String getFormattedLogURL(String url) {
        return '[' + url + ']';
    }

    public void initMySQL() throws SQLException, IOException {
        ConnectionUrl originURL = ConnectionUrl.getConnectionUrlInstance(url, null);
        MysqlJdbcUrlBean mysqlJdbcUrlBean = new MysqlJdbcUrlBean(originURL);

        String targetDatabaseName = mysqlJdbcUrlBean.getDatabaseName();
        mysqlJdbcUrlBean.setDatabaseName(CommonConst.INFORMATION_SCHEMA_TABLE_NAME);
        mysqlJdbcUrlBean.getConnectionArguments().remove(PropertyKey.USER);
        mysqlJdbcUrlBean.getConnectionArguments().remove(PropertyKey.PASSWORD);

        url = mysqlJdbcUrlBean.buildURL();
        log.info("Temporary JDBC URL=" + getFormattedLogURL(url));

        DruidDataSource mysqlTableSchemaDataSource = new DruidDataSource();
        mysqlTableSchemaDataSource.setUsername(username);
        mysqlTableSchemaDataSource.setPassword(password);
        mysqlTableSchemaDataSource.setDriverClassName(driverClassName);
        mysqlTableSchemaDataSource.setUrl(url);

        // ???????????????????????????????????????MySQL?????????
        Connection connection = mysqlTableSchemaDataSource.getConnection();
        connection.setAutoCommit(false);
        log.info("Database server connected.Checking database.");
        // ?????? MySQL??? ???????????????????????????????????????????????????????????????????????? dataSource ???????????????
        PreparedStatement ps = connection.prepareStatement("select * from information_schema.SCHEMATA where SCHEMA_NAME = ?");
        ps.setString(1, targetDatabaseName);
        logExecutingSQL(getSql(ps));
        ResultSet rs = ps.executeQuery();
        try {
            if (rs.next()) {
                log.info("Database is exist!");
                tryExecuteResourceSqlFile(connection, "db_restart_init.sql", ";");
            } else {
                log.info("Database is not exist!");
                initDatabaseSchema(connection, targetDatabaseName);
            }
            connection.commit();
            log.info("Database initiated!");
        } catch (SQLException | IOException exception) {
            log.error("Database initiation failed.");
            log.error(exception.getMessage());
            throw exception;
        } finally {
            connection.close();
            mysqlTableSchemaDataSource.close();
        }
    }

    protected void initDatabaseSchema(Connection connection, String targetDatabaseName) throws SQLException, IOException {
        String dropDatabaseIfExists = "drop database if exists " + targetDatabaseName;
        String createDatabase = "create database " + targetDatabaseName + " DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
        String switchDatabase = "use " + targetDatabaseName;

        logExecutingSQL(dropDatabaseIfExists);
        connection.createStatement().execute(dropDatabaseIfExists);

        logExecutingSQL(createDatabase);
        connection.createStatement().execute(createDatabase);

        logExecutingSQL(switchDatabase);
        connection.createStatement().execute(switchDatabase);

        tryExecuteResourceSqlFile(connection, "db_init_create_schema.sql", ";");
        tryExecuteResourceSqlFile(connection, "db_init_create_procedure.sql", "$$");
        tryExecuteResourceSqlFile(connection, "db_init_insert_pre_data.sql", ";");
    }

    protected void tryExecuteResourceSqlFile(Connection connection, String resourceFileName, String delimiter) throws SQLException, IOException {
        URL fileURL = PackageLandmark.class.getResource(resourceFileName);
        if (fileURL != null) {
            log.info("Executing SQL file URL=" + getFormattedLogURL(fileURL.toString()));
            executeSqlFile(connection, fileURL, delimiter);
            log.info("Execution done. SQL file URL=" + getFormattedLogURL(fileURL.toString()));
        } else {
            log.info("Execution will not process. Because file is not found. SQL file resource path=[" + resourceFileName + ']');
        }
    }

    public static void executeSqlFile(Connection connection, URL sqlFileURL, String delimiter) throws IOException, SQLException {
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        Resources.setCharset(StandardCharsets.UTF_8); //???????????????,??????????????????????????????

        InputStream inputStream = sqlFileURL.openStream();
        Reader read = new InputStreamReader(inputStream);
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        // scriptRunner.setFullLineDelimiter(true);
        scriptRunner.setDelimiter(delimiter);
        scriptRunner.setLogWriter(null);//????????????????????????
        scriptRunner.runScript(read);

        connection.commit();
        read.close();
        inputStream.close();
        // connection.close();
    }
}
