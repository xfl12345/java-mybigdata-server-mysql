package cc.xfl12345.mybigdata.server;

import cc.xfl12345.mybigdata.server.mysql.spring.helper.JdbcContextFinalizer;
import cc.xfl12345.mybigdata.server.mysql.spring.helper.MyDatabaseInitializer;
import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;

public class TestDatabaseReset {
    public static void main(String[] args) throws Exception {
        DruidDataSource dataSource = TestLoadDataSource.getDataSource();

        MyDatabaseInitializer databaseInitializer = new MyDatabaseInitializer();
        databaseInitializer.setUrl(dataSource.getUrl());
        databaseInitializer.setDriverClassName(dataSource.getDriverClassName());
        databaseInitializer.setUsername(dataSource.getUsername());
        databaseInitializer.setPassword(dataSource.getPassword());

        Connection connection = dataSource.getConnection();
        connection.createStatement().execute("drop database if exists mybigdata");
        connection.close();

        databaseInitializer.afterPropertiesSet();
        JdbcContextFinalizer.deregister(null);
    }

}
