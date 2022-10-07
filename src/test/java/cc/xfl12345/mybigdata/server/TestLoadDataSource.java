package cc.xfl12345.mybigdata.server;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson2.JSONObject;
import org.yaml.snakeyaml.Yaml;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class TestLoadDataSource {
    public static void main(String[] args) throws SQLException, IOException {
        DruidDataSource druidDataSource = getDataSource();
        DataSource dataSource = druidDataSource;
        Connection connection = dataSource.getConnection();

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
        InputStream inputStream = Objects.requireNonNull(fileURL).openStream();
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

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUsername(dbLoginUserName);
        dataSource.setPassword(dbLoginPassword);
        dataSource.setUrl(jdbcURLinString);
        dataSource.setDriverClassName(driverClassName);
        dataSource.init();
        return dataSource;
    }
}
