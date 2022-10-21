package cc.xfl12345.mybigdata.server;

import cc.xfl12345.mybigdata.server.mysql.spring.helper.JdbcContextFinalizer;
import cc.xfl12345.mybigdata.server.mysql.spring.helper.MyDatabaseInitializer;
import com.alibaba.druid.pool.DruidDataSource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.net.URL;

public class TestDatabaseInitializer {
    public static void main(String[] args) throws Exception {
        URL configFileURL = Thread.currentThread().getContextClassLoader().getResource("application.yml");

        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        JsonNode jsonObject = yamlReader.readTree(configFileURL);

        JsonNode datasourceConfig = jsonObject.get("spring").get("datasource");
        String dbLoginUserName = datasourceConfig.get("username").asText();
        String dbLoginPassword = datasourceConfig.get("password").asText();
        String jdbcURLinString = datasourceConfig.get("url").asText();
        String driverClassName = datasourceConfig.get("driver-class-name").asText();

        MyDatabaseInitializer databaseInitializer = new MyDatabaseInitializer();
        databaseInitializer.setUrl(jdbcURLinString);
        databaseInitializer.setDriverClassName(driverClassName);
        databaseInitializer.setUsername(dbLoginUserName);
        databaseInitializer.setPassword(dbLoginPassword);

        databaseInitializer.init();

        DruidDataSource dataSource = TestLoadDataSource.getDataSource();
        System.out.println(((java.sql.Connection) dataSource.getConnection()).getMetaData().getDatabaseProductName());

        JdbcContextFinalizer.deregister(null);
    }

}
