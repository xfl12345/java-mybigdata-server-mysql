package cc.xfl12345.mybigdata.server.mysql.api;

import cc.xfl12345.mybigdata.server.common.api.DatabaseViewer;
import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.pojo.DatabaseDataSourceInfo;
import cc.xfl12345.mybigdata.server.common.pojo.FieldNotNullChecker;
import cc.xfl12345.mybigdata.server.common.pojo.SuperObjectDatabase;
import cc.xfl12345.mybigdata.server.mysql.appconst.EnumCoreTable;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.CoreTableCache;
import cc.xfl12345.mybigdata.server.mysql.pojo.PojoInfo;
import com.alibaba.druid.pool.DruidDataSource;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.teasoft.bee.osql.api.SuidRich;
import org.teasoft.bee.osql.transaction.Transaction;
import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.SessionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;

public class DatabaseViewerImpl implements DatabaseViewer {
    @Getter
    @Setter
    protected FieldNotNullChecker fieldNotNullChecker = DefaultSingleton.FIELD_NOT_NULL_CHECKER;

    protected List<String> allTableNames;

    protected HashMap<String, List<String>> tableFieldNames;

    protected HashMap<String, Object> tableName2PojoInstance;

    @Getter
    @Setter
    protected CoreTableCache coreTableCache;

    @Getter
    @Setter
    protected List<DataSource> dataSources;

    @PostConstruct
    public void init() throws Exception {
        fieldNotNullChecker.check(coreTableCache, "coreTableCache");
        dataSources = Collections.emptyList();

        int coreTableCount = EnumCoreTable.values().length;
        allTableNames = new ArrayList<>(coreTableCount);
        tableFieldNames = new HashMap<>(coreTableCount);
        tableName2PojoInstance = new HashMap<>(coreTableCount);

        SuperObjectDatabase<PojoInfo> pojoInfoDatabase = coreTableCache.getPojoInfoDatabase();
        Map<Object, PojoInfo> pojoClassMap = pojoInfoDatabase.getSecondLevelMap(PojoInfo.Fields.pojoClass);

        pojoInfoDatabase.getSecondLevelMap(PojoInfo.Fields.pojoClass).keySet().parallelStream()
            .map(obj -> (Class<?>) obj)
            .toList()
            .forEach(pojoClass -> {
                String tableName = pojoClassMap.get(pojoClass).getTableName();
                allTableNames.add(tableName);

                tableFieldNames.put(
                    tableName,
                    pojoClassMap.get(pojoClass).getClassDeclaredInfo()
                        .getPropertiesMap().keySet().stream().toList()
                );

                tableName2PojoInstance.put(
                    tableName,
                    coreTableCache.getEmptyPoEntity(pojoClass)
                );
            });
    }


    @Override
    public List<String> getAllTableNames() {
        return allTableNames;
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

        if (pojo == null) {
            return result;
        }

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
    public List<DatabaseDataSourceInfo> getAllDataSourceInfos() {
        return dataSources
            .parallelStream()
            .map(dataSource -> {
                    DatabaseDataSourceInfo info = new DatabaseDataSourceInfo();
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
