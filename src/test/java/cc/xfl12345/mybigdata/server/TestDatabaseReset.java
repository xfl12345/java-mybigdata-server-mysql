package cc.xfl12345.mybigdata.server;

import cc.xfl12345.mybigdata.server.mysql.spring.helper.DriverHelper;
import cc.xfl12345.mybigdata.server.mysql.spring.helper.MyDatabaseInitializer;
import com.alibaba.druid.pool.DruidDataSource;

public class TestDatabaseReset {
    public static void main(String[] args) throws Exception {
        DruidDataSource dataSource = TestLoadDataSource.getDataSource();

        MyDatabaseInitializer databaseInitializer = new MyDatabaseInitializer();
        databaseInitializer.setUrl(dataSource.getUrl());
        databaseInitializer.setDriverClassName(dataSource.getDriverClassName());
        databaseInitializer.setUsername(dataSource.getUsername());
        databaseInitializer.setPassword(dataSource.getPassword());

        dataSource.getConnection().createStatement()
            .execute("drop database if exists xfl_mybigdata");

        databaseInitializer.afterPropertiesSet();
        DriverHelper.deregister(null);
    }

}
