package cc.xfl12345.mybigdata.server.mysql.spring.helper;

import cc.xfl12345.mybigdata.server.common.appconst.CommonConst;
import cc.xfl12345.mybigdata.server.mysql.spring.helper.url.MysqlJdbcUrlHelper;
import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.cj.PreparedQuery;
import com.mysql.cj.conf.ConnectionUrl;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

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
import java.util.Properties;

@Slf4j
public class MyDatabaseInitializer implements InitializingBean {

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

    //@ConditionalOnProperty(prefix = "spring.datasource",name = "url",havingValue = "true")
    @Value("${spring.datasource.url}")
    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    //@ConditionalOnProperty(prefix = "spring.datasource",name = "driver-class-name",havingValue = "true")
    @Value("${spring.datasource.driver-class-name}")
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String mysqlJdbcProtocolHeader = "jdbc:mysql://";
        if (url != null && url.startsWith(mysqlJdbcProtocolHeader)) {
            initMySQL();
        }
    }

    public static String getSql(PreparedStatement preparedStatement) {
        String sql;
        com.mysql.cj.jdbc.ClientPreparedStatement unwarpedPreparedStatement;
        try {
            unwarpedPreparedStatement = preparedStatement.unwrap(com.mysql.cj.jdbc.ClientPreparedStatement.class);
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

    public void initMySQL() throws SQLException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        MysqlJdbcUrlHelper mysqlJdbcUrlHelper = new MysqlJdbcUrlHelper(ConnectionUrl.getConnectionUrlInstance(url, null));
        String targetDatabaseName = mysqlJdbcUrlHelper.getDatabaseName();
        mysqlJdbcUrlHelper.setDatabaseName("information_schema");
        url = mysqlJdbcUrlHelper.getSqlConnUrlWithConfigProp();

        DruidDataSource mysqlTableSchemaDataSource = new DruidDataSource();
        mysqlTableSchemaDataSource.setUsername(username);
        mysqlTableSchemaDataSource.setPassword(password);
        mysqlTableSchemaDataSource.setDriverClassName(driverClassName);
        mysqlTableSchemaDataSource.setUrl(url);

        // 加载 sql URL附加属性
        Properties confProp = mysqlJdbcUrlHelper.getAdditionalParameters();
        // 浅拷贝一个Properties对象，删除其中的敏感数据，用以展示完整的JDBC URL
        Properties tmpConfProp = (Properties) confProp.clone();
        tmpConfProp.remove("user");
        tmpConfProp.remove("password");
        // 创建一个临时的数据库连接，完成数据库初始化
        // 构建一个不带附加参数的JDBC URL
        String tmpJdbcConnectionURL = mysqlJdbcUrlHelper.getSqlConnUrlWithConfigProp(CommonConst.INFORMATION_SCHEMA_TABLE_NAME, tmpConfProp);
        log.info("Temporary JDBC URL=" + tmpJdbcConnectionURL);
        // 创建一个临时连接，用于试探MySQL数据库
        Connection conn2 = mysqlTableSchemaDataSource.getConnection();
        conn2.setAutoCommit(false);
        log.info("Database server connected.Checking database.");
        // 检查 MySQL中 某个数据库是否存在（其它数据库暂未适配，所以这个 dataSource 并非万能）
        PreparedStatement ps = conn2.prepareStatement("select * from information_schema.SCHEMATA where SCHEMA_NAME = ?");
        ps.setString(1, targetDatabaseName);
        log.info(getSql(ps));
        ResultSet rs = ps.executeQuery();


        try {
            if (rs.next()) {
                log.info("Database is exist!");
                tryExecuteResourceSqlFile(conn2, classLoader, "database/db_restart_init.sql", ";");
            } else {
                log.info("Database is not exist!");
                tryExecuteResourceSqlFile(conn2, classLoader, "database/db_init_create_schema.sql", ";");
                tryExecuteResourceSqlFile(conn2, classLoader, "database/db_init_create_procedure.sql", "$$");
                tryExecuteResourceSqlFile(conn2, classLoader, "database/db_init_insert_pre_data.sql", ";");
            }
            log.info("Database initiated!");
        } catch (SQLException | IOException exception) {
            log.error("Database initiation failed.");
            log.error(exception.getMessage());
            throw exception;
        } finally {
            conn2.close();
            mysqlTableSchemaDataSource.close();
        }
    }

    protected void tryExecuteResourceSqlFile(Connection conn, ClassLoader classLoader, String fileResourcePath, String delimiter) throws SQLException, IOException {
        URL fileURL = classLoader.getResource(fileResourcePath);
        if (fileURL != null) {
            log.info("Executing SQL file URL=" + fileURL.toString());
            executeSqlFile(conn, fileURL, delimiter);
            log.info("Execution done. SQL file URL=" + fileURL.toString());
        } else  {
            log.info("Execution will not process. Because file is not found. SQL file resource path=" + fileResourcePath);
        }
    }

    public static void executeSqlFile(Connection connection, URL sqlFileURL, String delimiter) throws IOException, SQLException {
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        Resources.setCharset(StandardCharsets.UTF_8); //设置字符集,不然中文乱码插入错误


        InputStream inputStream = sqlFileURL.openStream();
        Reader read = new InputStreamReader(inputStream);
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        // scriptRunner.setFullLineDelimiter(true);
        scriptRunner.setDelimiter(delimiter);
        scriptRunner.setLogWriter(null);//设置是否输出日志
        scriptRunner.runScript(read);

        connection.commit();
        read.close();
        inputStream.close();
        // connection.close();
    }
}
