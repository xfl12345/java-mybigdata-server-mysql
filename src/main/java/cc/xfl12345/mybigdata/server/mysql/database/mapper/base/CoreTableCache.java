package cc.xfl12345.mybigdata.server.mysql.database.mapper.base;

import cc.xfl12345.mybigdata.server.common.api.OpenCloneable;
import cc.xfl12345.mybigdata.server.common.database.AbstractCoreTableCache;
import cc.xfl12345.mybigdata.server.common.pojo.MbdId;
import cc.xfl12345.mybigdata.server.common.pojo.SuperObjectDatabase;
import cc.xfl12345.mybigdata.server.common.pojo.TwoWayMap;
import cc.xfl12345.mybigdata.server.common.utility.MyReflectUtils;
import cc.xfl12345.mybigdata.server.mysql.appconst.CoreTableNames;
import cc.xfl12345.mybigdata.server.mysql.appconst.EnumCoreTable;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.BooleanContent;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.StringContent;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;
import cc.xfl12345.mybigdata.server.mysql.pojo.PojoInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.teasoft.bee.osql.BeeException;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.bee.osql.transaction.Transaction;
import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.ConditionImpl;
import org.teasoft.honey.osql.core.SessionFactory;

import java.util.*;

@Slf4j
public class CoreTableCache extends AbstractCoreTableCache<Long, String> {
    @Getter
    @Setter
    protected ObjectMapper jacksonObjectMapper;

    @Getter
    protected SuperObjectDatabase<PojoInfo> pojoInfoDatabase;

    @Getter
    protected Map<Class<?>, PojoInfo> pojoClass2PojoInfoMap;

    protected TwoWayMap<MbdId<Long>, Class<?>> tableNameId2ClassCache;

    @Getter
    protected Map<Class<?>, OpenCloneable> emptyPoEntites;

    public CoreTableCache() {
        tableNameCache = new TwoWayMap<>(EnumCoreTable.values().length + 1);
    }

    @Override
    public void init() throws Exception {
        if (jacksonObjectMapper == null) {
            jacksonObjectMapper = new ObjectMapper();
        }
        pojoInfoDatabase = new SuperObjectDatabase<>(
            new PojoInfo(),
            Set.of(PojoInfo.Fields.classDeclaredInfo, PojoInfo.Fields.emptyObject)
        );

        try {
            Collection<Class<?>> pojoClasses = MyReflectUtils.getClasses(
                GlobalDataRecord.class.getPackageName(),
                false,
                false,
                true
            );

            emptyPoEntites = new HashMap<>(pojoClasses.size());
            pojoClass2PojoInfoMap = new HashMap<>(pojoClasses.size());
            Object hashMapThreadSafeLock = new Object();

            pojoClasses.parallelStream().forEach(pojoClass -> {
                OpenCloneable poInstance;
                try {
                    @SuppressWarnings("unchecked")
                    PojoInfo pojoInfo = generatePojoInfo((Class<OpenCloneable>) pojoClass);
                    poInstance = pojoInfo.getNewPoInstance();
                    pojoInfoDatabase.add(pojoInfo);
                    synchronized (hashMapThreadSafeLock) {
                        emptyPoEntites.put(pojoClass, poInstance);
                        pojoClass2PojoInfoMap.put(pojoClass, pojoInfo);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException runtimeException) {
            if (runtimeException.getCause() instanceof Exception exception) {
                throw exception;
            } else {
                throw runtimeException;
            }
        }

        super.init();

        tableNameId2ClassCache = new TwoWayMap<>(EnumCoreTable.values().length);
        tableNameCache.getValue2KeyMap().keySet().forEach(tableNameId -> tableNameId2ClassCache.put(
            tableNameId,
            pojoInfoDatabase.getObject(PojoInfo.Fields.tableName, tableNameCache.getKey(tableNameId)).getPojoClass()
        ));
    }

    @Override
    public Class<Long> getIdType() {
        return Long.class;
    }

    protected PojoInfo generatePojoInfo(Class<OpenCloneable> pojoClass) throws Exception {
        return new PojoInfo(pojoClass);
    }

    @Override
    public void refreshBooleanCache() throws Exception {
        // 开启事务
        Transaction transaction = SessionFactory.getTransaction();
        try {
            transaction.begin();
            SuidRich suid = BeeFactory.getHoneyFactory().getSuidRich();

            // 查询数据
            List<BooleanContent> booleanContents = suid.select(new BooleanContent());
            for (BooleanContent booleanContent : booleanContents) {
                if (booleanContent.getContent()) {
                    idOfTrue = new MysqlMbdId(booleanContent.getGlobalId());
                } else {
                    idOfFalse = new MysqlMbdId(booleanContent.getGlobalId());
                }
            }

            transaction.commit();
        } catch (BeeException e) {
            log.error(e.getMessage());
            transaction.rollback();
            throw e;
        }
        log.info("Cache \"global_id\" for JSON constant - boolean value: true <---> " + idOfTrue);
        log.info("Cache \"global_id\" for JSON constant - boolean value: false <---> " + idOfFalse);
    }

    protected void refreshCoreTableNameCache(List<String> values) throws Exception {
        Condition condition = new ConditionImpl();
        condition.selectField(StringContent.Fields.globalId, StringContent.Fields.content)
            .op(StringContent.Fields.content, Op.in, values);

        // 开启事务
        Transaction transaction = SessionFactory.getTransaction();
        try {
            transaction.begin();
            SuidRich suid = BeeFactory.getHoneyFactory().getSuidRich();

            // 查询数据
            List<StringContent> contents = suid.select(new StringContent(), condition);
            if (contents.size() != values.size()) {
                HashSet<String> actuallyGet = new HashSet<>(contents.stream().map(StringContent::getContent).toList());
                HashSet<String> missing = new HashSet<>(values);
                missing.removeAll(actuallyGet);
                throw new IllegalArgumentException("We are looking for " + values.size() + " records. " +
                    "We get table name data in follow: " + jacksonObjectMapper.valueToTree(contents).toPrettyString() +
                    ". It is missing " + jacksonObjectMapper.valueToTree(missing).toPrettyString() + "."
                );
            }

            for (StringContent content : contents) {
                tableNameCache.put(content.getContent(), new MysqlMbdId(content.getGlobalId()));
            }

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public void refreshCoreTableNameCache() throws Exception {
        refreshCoreTableNameCache(Arrays.stream(EnumCoreTable.values()).map(EnumCoreTable::getName).toList());
        log.info("Cache \"global_id\" for core table name: " +
            jacksonObjectMapper.valueToTree(tableNameCache.getKey2ValueMap()).toPrettyString()
        );
    }

    @Override
    protected String tableNameOfBoolean() {
        return CoreTableNames.BOOLEAN_CONTENT;
    }

    @Override
    public MbdId<Long> getTableNameId(Class<?> pojoClass) {
        return tableNameId2ClassCache.getKey(pojoClass);
    }

    @Override
    public <ID2 extends MbdId<Long>> Class<?> getPojoClassByTableNameId(ID2 id) {
        return tableNameId2ClassCache.getValue(id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getEmptyPoEntity(Class<T> pojoClass) {
        return (T) pojoClass2PojoInfoMap.get(pojoClass).getNewPoInstance();
    }
    public PojoInfo getPoInfo(Class<?> pojoClass) {
        return pojoClass2PojoInfoMap.get(pojoClass);
    }
}
