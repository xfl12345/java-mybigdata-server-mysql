package cc.xfl12345.mybigdata.server;

import cc.xfl12345.mybigdata.server.common.appconst.CommonConst;
import cc.xfl12345.mybigdata.server.mysql.util.MysqlJdbcUrlBean;
import com.mysql.cj.conf.ConnectionUrl;

public class TestMysqlJdbcUrlBean {
    public static void main(String[] args) {
        String jdbcURL = "jdbc:mysql://address=(host=myhost1)(port=1111)(key1=value1),address=(host=myhost2)(port=2222)(key2=value2)/db?useUnicode=true&characterEncoding=utf8&characterSetResults=utf8&serverTimezone=Asia/Shanghai&nullCatalogMeansCurrent=true&allowMultiQueries=true";

        MysqlJdbcUrlBean bean = new MysqlJdbcUrlBean(ConnectionUrl.getConnectionUrlInstance(jdbcURL, null));

        bean.setAuthority("localhost");
        bean.setDatabaseName(CommonConst.INFORMATION_SCHEMA_TABLE_NAME);

        System.out.println(bean.buildURL());
    }
}
