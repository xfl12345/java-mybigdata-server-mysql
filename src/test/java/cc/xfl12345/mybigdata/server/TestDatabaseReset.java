package cc.xfl12345.mybigdata.server;

import cc.xfl12345.mybigdata.server.mysql.spring.helper.JdbcContextFinalizer;
import cc.xfl12345.mybigdata.server.mysql.spring.helper.MyDatabaseInitializer;
import cc.xfl12345.mybigdata.server.mysql.util.MysqlJdbcUrlBean;
import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.cj.conf.ConnectionUrl;

import java.sql.Connection;

public class TestDatabaseReset {
    public static void main(String[] args) throws Exception {
        DruidDataSource dataSource = TestLoadDataSource.getDataSource();

        MyDatabaseInitializer databaseInitializer = new MyDatabaseInitializer();
        databaseInitializer.setUrl(dataSource.getUrl());
        databaseInitializer.setDriverClassName(dataSource.getDriverClassName());
        databaseInitializer.setUsername(dataSource.getUsername());
        databaseInitializer.setPassword(dataSource.getPassword());

        ConnectionUrl originURL = ConnectionUrl.getConnectionUrlInstance(dataSource.getUrl(), null);
        MysqlJdbcUrlBean mysqlJdbcUrlBean = new MysqlJdbcUrlBean(originURL);
        String targetDatabaseName = mysqlJdbcUrlBean.getDatabaseName();

        Connection connection = dataSource.getConnection();
        connection.createStatement().execute("drop database if exists " + targetDatabaseName);
        connection.close();

        databaseInitializer.init();
        JdbcContextFinalizer.deregister(null);
    }

}
