package cc.xfl12345.mybigdata.server;

import cc.xfl12345.mybigdata.server.pojo.DataSourceConfig;
import com.alibaba.druid.pool.DruidDataSource;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
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

    public static URL getDefaultConfigFileURL() {
        return Thread.currentThread().getContextClassLoader().getResource("application.yml");
    }

    public static DruidDataSource getDataSource() throws IOException, SQLException {
        return getDataSource(getDefaultConfigFileURL());
    }

    public static DruidDataSource getDataSource(URL fileURL) throws IOException, SQLException {
        return getDataSource(getDataSourceConfig(fileURL));
    }

    public static DruidDataSource getDataSource(DataSourceConfig config) throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUsername(config.getUsername());
        dataSource.setPassword(config.getPassword());
        dataSource.setUrl(config.getUrl());
        dataSource.setDriverClassName(config.getDriverClassName());
        dataSource.init();
        return dataSource;
    }

    public static DataSourceConfig getDataSourceConfig() throws IOException {
        return getDataSourceConfig(getDefaultConfigFileURL());
    }

    public static DataSourceConfig getDataSourceConfig(URL fileURL) throws IOException {
        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        yamlReader.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
        yamlReader.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        JsonNode jsonObject = yamlReader.readTree(fileURL);
        JsonNode datasourceConfig = jsonObject.get("spring").get("datasource");
        return yamlReader.convertValue(datasourceConfig, DataSourceConfig.class);
    }
}
