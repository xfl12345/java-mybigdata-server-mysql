package cc.xfl12345.mybigdata.server;

import cc.xfl12345.mybigdata.server.mysql.spring.helper.JdbcContextFinalizer;
import cc.xfl12345.mybigdata.server.mysql.spring.helper.MyDatabaseInitializer;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson2.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Objects;

public class TestDatabaseInitializer {
    public static void main(String[] args) throws Exception {
        InputStream inputStream = Objects.requireNonNull(
            Thread.currentThread().getContextClassLoader().getResource("application.yml")
        ).openStream();
        Yaml yaml = new Yaml();
        JSONObject jsonObject = yaml.loadAs(inputStream, JSONObject.class);
        inputStream.close();
        // System.out.println(jsonObject.toString(SerializerFeature.PrettyFormat));
        JSONObject datasourceConfig = jsonObject
            .getJSONObject("spring")
            .getJSONObject("datasource");
        String dbLoginUserName = datasourceConfig.getString("username");
        String dbLoginPassword = datasourceConfig.getString("password");
        String jdbcURLinString = datasourceConfig.getString("url");
        String driverClassName = datasourceConfig.getString("driver-class-name");

        MyDatabaseInitializer databaseInitializer = new MyDatabaseInitializer();
        databaseInitializer.setUrl(jdbcURLinString);
        databaseInitializer.setDriverClassName(driverClassName);
        databaseInitializer.setUsername(dbLoginUserName);
        databaseInitializer.setPassword(dbLoginPassword);

        databaseInitializer.afterPropertiesSet();

        DruidDataSource dataSource = TestLoadDataSource.getDataSource();
        System.out.println(((java.sql.Connection) dataSource.getConnection()).getMetaData().getDatabaseProductName());

        JdbcContextFinalizer.deregister(null);
    }

}
