package cc.xfl12345.mybigdata.server.mysql.data.pojo;

import cc.xfl12345.mybigdata.server.common.appconst.CURD;
import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.data.source.JsonSchemaSource;
import cc.xfl12345.mybigdata.server.common.data.source.StringTypeSource;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdJsonSchema;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdObject;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableMapper;
import cc.xfl12345.mybigdata.server.common.pojo.AffectedRowsCountChecker;
import cc.xfl12345.mybigdata.server.common.pojo.FieldNotNullChecker;
import cc.xfl12345.mybigdata.server.mysql.appconst.CoreTableNames;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.ObjectContent;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.ObjectRecord;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.IteratorUtils;
import org.teasoft.bee.osql.api.Condition;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.api.SuidRich;
import org.teasoft.honey.osql.core.BeeFactoryHelper;
import org.teasoft.honey.osql.core.ConditionImpl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ReactiveNoCacheMbdObject implements MbdObject {
    protected final MysqlMbdId globalId;

    protected boolean lockFlag = false;

    @Getter
    @Setter
    protected FieldNotNullChecker fieldNotNullChecker = DefaultSingleton.FIELD_NOT_NULL_CHECKER;

    @Getter
    @Setter
    protected AffectedRowsCountChecker affectedRowsCountChecker = DefaultSingleton.AFFECTED_ROWS_COUNT_CHECKER;

    @Getter
    @Setter
    protected JsonSchemaSource jsonSchemaSource;

    @Getter
    @Setter
    protected StringTypeSource stringTypeSource;

    @Getter
    @Setter
    protected TableMapper<ObjectContent, Condition> tableMapper;

    public ReactiveNoCacheMbdObject(MysqlMbdId globalId, boolean lockFlag) {
        this.globalId = globalId;
        this.lockFlag = lockFlag;

        if (lockFlag) {
            lockRow();
        }
    }

    protected void lockRow() {
        SuidRich suidRich = BeeFactoryHelper.getSuidRich();
        Condition condition = new ConditionImpl();
        condition.forUpdate();
        GlobalDataRecord globalDataRecord = new GlobalDataRecord();
        globalDataRecord.setId(globalId.getLongValue());
        suidRich.selectOne(globalDataRecord);
    }

    protected MysqlMbdId getJsonSchemaId() {
        SuidRich suidRich = BeeFactoryHelper.getSuidRich();
        Condition condition = new ConditionImpl();
        condition.op(ObjectRecord.Fields.globalId, Op.eq, globalId.getLongValue());
        condition.selectField(ObjectRecord.Fields.objectSchema);

        List<ObjectRecord> recordList = suidRich.select(new ObjectRecord(), condition);
        affectedRowsCountChecker.checkAffectedRowShouldBeOne(recordList.size(), CURD.RETRIEVE, CoreTableNames.OBJECT_RECORD);

        return new MysqlMbdId(recordList.get(0).getObjectSchema());
    }

    @Override
    public MbdJsonSchema getSchema() {
        return jsonSchemaSource.selectById(getJsonSchemaId());
    }

    @Override
    public void setSchema(MbdJsonSchema reactiveJsonSchema) {
        jsonSchemaSource.updateById(reactiveJsonSchema, getJsonSchemaId());
    }

    protected MysqlMbdId getJsonSchemaPathId() {
        SuidRich suidRich = BeeFactoryHelper.getSuidRich();
        Condition condition = new ConditionImpl();
        condition.op(ObjectRecord.Fields.globalId, Op.eq, globalId.getLongValue());
        condition.selectField(ObjectRecord.Fields.schemaPath);

        List<ObjectRecord> recordList = suidRich.select(new ObjectRecord(), condition);
        affectedRowsCountChecker.checkAffectedRowShouldBeOne(recordList.size(), CURD.RETRIEVE, CoreTableNames.OBJECT_RECORD);

        return new MysqlMbdId(recordList.get(0).getSchemaPath());
    }

    @Override
    public String getSchemaPath() {
        return stringTypeSource.selectById(getJsonSchemaPathId());
    }

    @Override
    public void setSchemaPath(String schemaPath) {
        stringTypeSource.updateById(schemaPath, getJsonSchemaPathId());
    }

    @Override
    public Map<String, MbdId> getMap() {
        // 获取所有字段各自对应的 id
        JsonNode properties = getSchema().getJsonSchema().getSchemaNode().at(getSchemaPath());

        // 制作 小型 id->字符串 映射表
        LinkedHashMap<MbdId, String> keysIdCache = stringTypeSource
            .selectBatchId(IteratorUtils.toList(properties.fieldNames(), properties.size()))
            .entrySet()
            .parallelStream()
            .collect(Collectors.toMap(
                Map.Entry::getValue,
                Map.Entry::getKey,
                (key1, key2) -> key2,
                LinkedHashMap::new
            ));

        // 生成 MbdObject 的 Map 部分
        int contentsMaxSize = properties.size();
        ConcurrentHashMap<String, MbdId> contents = new ConcurrentHashMap<>(contentsMaxSize);
        BeeFactoryHelper.getSuidRich().select(
            new ObjectContent(),
            new ConditionImpl().op(ObjectContent.Fields.globalId, Op.eq, globalId.getLongValue())
        ).parallelStream().forEach(item -> {
            contents.put(
                keysIdCache.get(new MysqlMbdId(item.getTheKey())), // 获取字段名
                new MysqlMbdId(item.getTheValue()) // 获取字段引用 id
            );
        });

        return contents;
    }

    @Override
    public void setMap(Map<String, MbdId> map) {
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public MbdId getGlobalId() {
        return globalId;
    }

    public class ReactiveMap extends AbstractMap<String, MbdId> {
        @Override
        public Set<Entry<String, MbdId>> entrySet() {
            return getMap().entrySet();
        }

        @Override
        public MbdId put(String key, MbdId value) {

            return super.put(key, value);
        }

        @Override
        public MbdId remove(Object key) {
            if (key == null) {
                return null;
            }

            if (key instanceof String keyString) {
                MbdId keysId = stringTypeSource.selectId(keyString);
                SuidRich suidRich = BeeFactoryHelper.getSuidRich();
                ObjectContent objectContent = ObjectContent.builder()
                    .globalId(globalId.getLongValue())
                    .theKey(keysId.getLongValue())
                    .build();
                List<ObjectContent> objectContents = suidRich.select(objectContent);
                if (objectContents.size() == 0) {
                    return null;
                }

                affectedRowsCountChecker.checkAffectedRowShouldBeOne(objectContents.size(), CURD.RETRIEVE, CoreTableNames.OBJECT_CONTENT);

                objectContent = objectContents.get(0);
                int rows = suidRich.delete(objectContent);

                affectedRowsCountChecker.checkAffectedRowShouldBeOne(rows, CURD.DELETE, CoreTableNames.OBJECT_CONTENT);
                return new MysqlMbdId(objectContent.getTheValue());
            }

            throw new IllegalArgumentException("Expect argument type is [" +
                String.class.getCanonicalName() + "] but get [" +
                key.getClass().getCanonicalName() + "]."
            );
        }

        @Override
        public boolean replace(String key, MbdId oldValue, MbdId newValue) {
            return super.replace(key, oldValue, newValue);
        }
    }
}
