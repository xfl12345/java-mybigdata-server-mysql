package cc.xfl12345.mybigdata.server.mysql.database.error;

import cc.xfl12345.mybigdata.server.common.appconst.TableCurdResult;
import cc.xfl12345.mybigdata.server.common.database.error.SqlErrorAnalyst;
import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.teasoft.honey.osql.core.BeeFactory;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
public class SqlErrorAnalystImpl implements SqlErrorAnalyst {
    @Getter
    @Setter
    protected Map<String, Map<Integer, TableCurdResult>> coreTableResultMap = null;

    @PostConstruct
    public void init() throws Exception {
        if (coreTableResultMap == null) {
            coreTableResultMap = new HashMap<>();
            HashMap<Integer, TableCurdResult> mysql = new HashMap<>();
            // ER_TOO_LONG_IDENT -- Identifier name '%s' is too long
            mysql.put(1059, TableCurdResult.FAILED_OVER_FLOW);
            // ER_DUP_ENTRY -- Duplicate entry '%s' for key %d
            mysql.put(1062, TableCurdResult.DUPLICATE);
            // ER_DATA_TOO_LONG -- Data too long for column '%s' at row %ld
            mysql.put(1406, TableCurdResult.FAILED_OVER_FLOW);
            // ER_ROW_IS_REFERENCED_2 -- Cannot delete or update a parent row: a foreign key constraint fails (%s)
            mysql.put(1451, TableCurdResult.FAILED_OPERATION_REJECTED);
            coreTableResultMap.put("mysql", mysql);
        }
    }

    @Override
    public TableCurdResult getTableCurdResult(String dbType, int vendorCode) {
        Map<Integer, TableCurdResult> codeMapper = coreTableResultMap.get(dbType);
        if (codeMapper == null) {
            return TableCurdResult.UNKNOWN_FAILED;
        }

        return codeMapper.getOrDefault(vendorCode, TableCurdResult.UNKNOWN_FAILED);
    }

    @Override
    public TableCurdResult getTableCurdResult(@NonNull DataSource dataSource, int vendorCode) throws SQLException {
        Connection connection = dataSource.getConnection();
        TableCurdResult result = getTableCurdResult(connection, vendorCode);
        connection.close();
        return result;
    }

    @Override
    public TableCurdResult getTableCurdResult(@NonNull Connection connection, int vendorCode) throws SQLException {
        return getTableCurdResult(connection.getMetaData().getDatabaseProductName().toLowerCase(Locale.ROOT), vendorCode);
    }

    @Override
    public TableCurdResult getTableCurdResult(@NonNull Exception exception) {
        Throwable cause = exception;
        do {
            cause = cause.getCause();
        } while (cause != null && !(cause instanceof SQLException));

        if (cause instanceof SQLException sqlException) {
            return getTableCurdResult(sqlException);
        }
        return TableCurdResult.UNKNOWN_FAILED;
    }

    @Override
    public TableCurdResult getTableCurdResult(@NonNull SQLException exception) {
        int errorCode = exception.getErrorCode();
        BeeFactory beeFactory = BeeFactory.getInstance();
        DataSource dataSource = beeFactory.getDataSource();
        if (dataSource instanceof DruidDataSource druidDataSource) {
            return getTableCurdResult(druidDataSource.getDbType(), errorCode);
        } else {
            try {
                return getTableCurdResult(dataSource, errorCode);
            } catch (SQLException e) {
                log.error(e.getMessage());
                return TableCurdResult.UNKNOWN_FAILED;
            }
        }
    }
}
