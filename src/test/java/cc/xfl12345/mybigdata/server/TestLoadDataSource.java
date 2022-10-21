package cc.xfl12345.mybigdata.server;

import com.alibaba.druid.pool.DruidDataSource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestLoadDataSource {
    public static void main(String[] args) throws SQLException, IOException {
        DruidDataSource druidDataSource = getDataSource();
        Connection connection = ((DataSource) druidDataSource).getConnection();

        PreparedStatement ps = connection.prepareStatement("select true");
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            System.out.println(rs.getObject(1));
        }

        connection.close();

        druidDataSource.close();
    }

    public static DruidDataSource getDataSource() throws IOException, SQLException {
        return getDataSource(Thread.currentThread().getContextClassLoader().getResource("application.yml"));
    }

    public static DruidDataSource getDataSource(URL fileURL) throws IOException, SQLException {
        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        JsonNode jsonObject = yamlReader.readTree(fileURL);

        JsonNode datasourceConfig = jsonObject.get("spring").get("datasource");
        String dbLoginUserName = datasourceConfig.get("username").asText();
        String dbLoginPassword = datasourceConfig.get("password").asText();
        String jdbcURLinString = datasourceConfig.get("url").asText();
        String driverClassName = datasourceConfig.get("driver-class-name").asText();

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUsername(dbLoginUserName);
        dataSource.setPassword(dbLoginPassword);
        dataSource.setUrl(jdbcURLinString);
        dataSource.setDriverClassName(driverClassName);
        dataSource.init();
        return dataSource;
    }
}
