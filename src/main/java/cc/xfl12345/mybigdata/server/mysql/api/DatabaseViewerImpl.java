package cc.xfl12345.mybigdata.server.mysql.api;

import cc.xfl12345.mybigdata.server.common.api.DatabaseViewer;
import cc.xfl12345.mybigdata.server.common.pojo.DatabaseDataSourceInfo;
import cc.xfl12345.mybigdata.server.mysql.appconst.EnumCoreTable;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.DaoPack;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidStatManagerFacade;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.bee.osql.transaction.Transaction;
import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.SessionFactory;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DatabaseViewerImpl implements DatabaseViewer {
    protected static DruidStatManagerFacade statManagerFacade = DruidStatManagerFacade.getInstance();
    protected List<String> allTableName = Arrays.stream(EnumCoreTable.values()).parallel().map(EnumCoreTable::getName).toList();

    protected HashMap<String, List<String>> tableFieldNames;

    protected HashMap<String, Object> tableName2PojoInstance;

    protected DaoPack daoPack;

    public DaoPack getDaoManager() {
        return daoPack;
    }

    public void setDaoManager(DaoPack daoPack) {
        this.daoPack = daoPack;
    }

    @PostConstruct
    public void init() throws Exception {
        int coreTableCount = EnumCoreTable.values().length;
        tableFieldNames = new HashMap<>(coreTableCount + 2);
        tableName2PojoInstance = new HashMap<>(coreTableCount + 2);

        for (Class<?> cls : daoPack.getPojoClasses()) {
            String tableName = daoPack.getMapperPackByPojoClass(cls).getTableName();
            Object pojoInstance = cls.getDeclaredConstructor().newInstance();

            tableFieldNames.put(
                tableName,
                daoPack.getMapperPackByPojoClass(cls).getClassDeclaredInfo()
                    .getPropertiesMap().keySet().stream().toList()
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
        return statManagerFacade.getDataSourceStatDataList()
            .parallelStream()
            .filter(item -> item instanceof DataSource)
            .map(item -> {
                    DatabaseDataSourceInfo info = new DatabaseDataSourceInfo();
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
