package cc.xfl12345.mybigdata.server.mysql.api;

import cc.xfl12345.mybigdata.server.common.api.DatabaseViewer;
import cc.xfl12345.mybigdata.server.common.pojo.DbDataSourceInfo;
import cc.xfl12345.mybigdata.server.common.utility.MyReflectUtils;
import cc.xfl12345.mybigdata.server.mysql.appconst.CoreTables;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.bee.osql.transaction.Transaction;
import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.SessionFactory;

import javax.persistence.Table;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DatabaseViewerImpl implements DatabaseViewer {
    private static final DruidStatManagerFacade statManagerFacade = DruidStatManagerFacade.getInstance();
    private final List<String> allTableName = Arrays.stream(CoreTables.values()).parallel().map(CoreTables::getName).toList();

    private final HashMap<String, List<String>> tableFieldNames;

    private final HashMap<String, Object> tableName2PojoInstance;

    public DatabaseViewerImpl() throws Exception {
        int coreTableCount = CoreTables.values().length;
        tableFieldNames = new HashMap<>(coreTableCount + 2);
        tableName2PojoInstance = new HashMap<>(coreTableCount + 2);

        List<Class<?>> pojoClasses = MyReflectUtils.getClasses(
            GlobalDataRecord.class.getPackage().getName(),
            false
        );

        for (Class<?> cls : pojoClasses) {
            String tableName = cls.getAnnotation(Table.class).name();
            Object pojoInstance = cls.getDeclaredConstructor().newInstance();

            JSONObject jsonObject = (JSONObject) JSON.toJSON(
                pojoInstance,
                JSONWriter.Feature.WriteNulls
            );

            tableFieldNames.put(
                tableName,
                jsonObject.keySet().stream().toList()
            );

            tableName2PojoInstance.put(tableName, pojoInstance);
        }
    }


    @Override
    public List<String> getAllTableName() {
        return allTableName;
    }

    @Override
    public List<String> getTableFieldNames(String tableName) {
        return tableFieldNames.get(tableName);
    }

    @Override
    public long getTableRecordCount(String tableName) {
        long result = 0;
        Object pojo = tableName2PojoInstance.get(tableName);

        Transaction transaction = SessionFactory.getTransaction();
        try {
            transaction.begin();
            SuidRich suid = BeeFactory.getHoneyFactory().getSuidRich();

            result = suid.count(pojo);

            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }

        return result;
    }

    @Override
    public List<Object> getTableContent(String tableName, long offset, long limit) {
        List<Object> result = new ArrayList<>();
        Object pojo = tableName2PojoInstance.get(tableName);

        Transaction transaction = SessionFactory.getTransaction();
        try {
            transaction.begin();
            SuidRich suid = BeeFactory.getHoneyFactory().getSuidRich();

            // TODO 解决 long 转 int 精度丢失问题
            result = suid.select(pojo, (int) offset, (int) limit);

            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }

        return result;
    }

    @Override
    public List<DbDataSourceInfo> getAllDataSourceInfos() {
        return statManagerFacade.getDataSourceStatDataList()
            .parallelStream()
            .filter(item -> item instanceof DataSource)
            .map(item -> {
                    DbDataSourceInfo info = new DbDataSourceInfo();
                    DataSource dataSource = (DataSource) item;
                    if (dataSource instanceof DruidDataSource druidDataSource) {
                        info.setName(druidDataSource.getName());
                        info.setDbType(druidDataSource.getDbType());
                        info.setDriverName(druidDataSource.getDriverClassName());
                        info.setUrl(druidDataSource.getUrl());
                    } else {
                        info.setName(dataSource.getClass().getName());

                        try (Connection connection = dataSource.getConnection()) {
                            DatabaseMetaData databaseMetaData = connection.getMetaData();

                            info.setDbType(databaseMetaData.getDatabaseProductName());
                            info.setDriverName(databaseMetaData.getDriverName());
                            info.setUrl(databaseMetaData.getURL());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    return info;
                }
            ).toList();
    }
}
