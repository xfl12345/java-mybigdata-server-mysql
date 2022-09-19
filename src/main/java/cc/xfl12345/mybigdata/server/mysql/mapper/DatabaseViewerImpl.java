package cc.xfl12345.mybigdata.server.mysql.mapper;

import cc.xfl12345.mybigdata.server.common.utility.MyReflectUtils;
import cc.xfl12345.mybigdata.server.common.web.mapper.DatabaseViewer;
import cc.xfl12345.mybigdata.server.mysql.appconst.CoreTables;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.bee.osql.transaction.Transaction;
import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.SessionFactory;

import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DatabaseViewerImpl implements DatabaseViewer {
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
        } catch (Exception e) {
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
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }

        return result;
    }

}
