package cc.xfl12345.mybigdata.server.mysql.database.error;

import cc.xfl12345.mybigdata.server.common.appconst.SimpleCoreTableCurdResult;
import cc.xfl12345.mybigdata.server.common.database.error.SqlErrorAnalyst;
import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.teasoft.honey.osql.core.BeeFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
public class SqlErrorAnalystImpl implements SqlErrorAnalyst, InitializingBean {
    @Getter
    @Setter
    protected Map<String, Map<Integer, SimpleCoreTableCurdResult>> coreTableResultMap = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (coreTableResultMap == null) {
            coreTableResultMap = new HashMap<>();
            HashMap<Integer, SimpleCoreTableCurdResult> mysql = new HashMap<>();
            // ER_TOO_LONG_IDENT -- Identifier name '%s' is too long
            mysql.put(1059, SimpleCoreTableCurdResult.FAILED_OVER_FLOW);
            // ER_DUP_ENTRY -- Duplicate entry '%s' for key %d
            mysql.put(1062, SimpleCoreTableCurdResult.DUPLICATE);
            // ER_DATA_TOO_LONG -- Data too long for column '%s' at row %ld
            mysql.put(1406, SimpleCoreTableCurdResult.FAILED_OVER_FLOW);
            // ER_ROW_IS_REFERENCED_2 -- Cannot delete or update a parent row: a foreign key constraint fails (%s)
            mysql.put(1451, SimpleCoreTableCurdResult.FAILED_OPERATION_REJECTED);
            coreTableResultMap.put("mysql", mysql);
        }
    }

    @Override
    public SimpleCoreTableCurdResult getSimpleCoreTableCurdResult(String dbType, int vendorCode) {
        Map<Integer, SimpleCoreTableCurdResult> codeMapper = coreTableResultMap.get(dbType);
        if (codeMapper == null) {
            return SimpleCoreTableCurdResult.UNKNOWN_FAILED;
        }

        return codeMapper.getOrDefault(vendorCode, SimpleCoreTableCurdResult.UNKNOWN_FAILED);
    }

    @Override
    public SimpleCoreTableCurdResult getSimpleCoreTableCurdResult(@NonNull DataSource dataSource, int vendorCode) throws SQLException {
        Connection connection = dataSource.getConnection();
        SimpleCoreTableCurdResult result = getSimpleCoreTableCurdResult(connection, vendorCode);
        connection.close();
        return result;
    }

    @Override
    public SimpleCoreTableCurdResult getSimpleCoreTableCurdResult(@NonNull Connection connection, int vendorCode) throws SQLException {
        return getSimpleCoreTableCurdResult(connection.getMetaData().getDatabaseProductName().toLowerCase(Locale.ROOT), vendorCode);
    }

    @Override
    public SimpleCoreTableCurdResult getSimpleCoreTableCurdResult(@NonNull Exception exception) {
        Throwable cause = exception.getCause();
        if (cause instanceof SQLException sqlException) {
            int errorCode = sqlException.getErrorCode();
            BeeFactory beeFactory = BeeFactory.getInstance();
            DataSource dataSource = beeFactory.getDataSource();
            if (dataSource instanceof DruidDataSource druidDataSource) {
                return getSimpleCoreTableCurdResult(druidDataSource.getDbType(), errorCode);
            } else {
                try {
                    return getSimpleCoreTableCurdResult(dataSource, errorCode);
                } catch (SQLException e) {
                    log.error(e.getMessage());
                    return SimpleCoreTableCurdResult.UNKNOWN_FAILED;
                }
            }
        }
        return SimpleCoreTableCurdResult.UNKNOWN_FAILED;
    }
}
