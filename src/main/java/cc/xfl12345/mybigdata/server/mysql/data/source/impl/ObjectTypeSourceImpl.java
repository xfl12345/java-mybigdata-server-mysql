package cc.xfl12345.mybigdata.server.mysql.data.source.impl;

import cc.xfl12345.mybigdata.server.common.appconst.TableCurdResult;
import cc.xfl12345.mybigdata.server.common.data.source.JsonSchemaSource;
import cc.xfl12345.mybigdata.server.common.data.source.ObjectTypeSource;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.*;
import cc.xfl12345.mybigdata.server.common.pojo.ReactiveMode;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.AbstractBeeTripleLayerTableDataSource;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.ObjectContent;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.ObjectRecord;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.IteratorUtils;
import org.teasoft.bee.osql.api.Condition;
import org.teasoft.bee.osql.Op;
import org.teasoft.honey.osql.core.ConditionImpl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ObjectTypeSourceImpl
    extends AbstractBeeTripleLayerTableDataSource<MbdObject, ObjectRecord, ObjectContent>
    implements ObjectTypeSource {

    @Getter
    @Setter
    protected JsonSchemaSource jsonSchemaSource;

    @Override
    public void init() throws Exception {
        fieldNotNullChecker.check(jsonSchemaSource, MbdJsonSchema.class);
        super.init();
    }

    @Override
    public MbdId selectId(MbdObject mbdObject) {
        // TODO support this feature
        throw new UnsupportedOperationException();
        // return null;
    }

    @Override
    public MbdObject selectById(MbdId globalId) {
        ObjectRecord firstPojo = firstMapper.selectById(globalId);
        List<ObjectContent> secondPojoList = secondMapper.selectByCondition(
            getEqualIdCondition(globalId)
        );

        return getValue(firstPojo, secondPojoList);
    }

    @Override
    public LinkedHashMap<MbdObject, MbdId> selectBatchId(List<MbdObject> mbdObjects) {
        // TODO support this feature
        throw new UnsupportedOperationException();
        // return null;
    }

    // @Override
    // public long insert(MbdObject mbdObject) {
    //     checkMbdObject(mbdObject);
    //     return super.insert(mbdObject);
    // }
    //
    // @Override
    // public MysqlMbdId insertAndReturnId(MbdObject mbdObject) {
    //     checkMbdObject(mbdObject);
    //     return super.insertAndReturnId(mbdObject);
    // }
    //
    // @Override
    // public long insertBatch(List<MbdObject> mbdObjects) {
    //     mbdObjects.parallelStream().forEach(this::checkMbdObject);
    //     return super.insertBatch(mbdObjects);
    // }

    @Override
    public Class<MbdObject> getValueType() {
        return MbdObject.class;
    }

    @Override
    protected ObjectRecord getFirstPojo(MbdId globalId, MbdObject mbdObject) {
        MbdId idOfSchemaPath;

        // 优先查询（因为这部分逻辑，肯定预先存在的情况更多点）
        try {
            idOfSchemaPath = stringTypeSource.selectId(mbdObject.getSchemaPath());
        } catch (RuntimeException e) {
            if (sqlErrorAnalyst.getTableCurdResult(e).equals(TableCurdResult.FAILED_NOT_FOUND)) {
                idOfSchemaPath = stringTypeSource.selectIdOrInsert4Id(mbdObject.getSchemaPath());
            } else {
                throw e;
            }
        }

        return ObjectRecord.builder()
            .globalId(MysqlMbdId.getValue(globalId))
            .objectSchema(MysqlMbdId.getValue(jsonSchemaSource.selectIdOrInsert4Id(mbdObject.getSchema())))
            .schemaPath(MysqlMbdId.getValue(idOfSchemaPath))
            .objectName(MysqlMbdId.getValue(stringTypeSource.selectIdOrInsert4Id(mbdObject.getName())))
            .build();
    }

    @Override
    protected List<ObjectContent> getSecondPojo(MbdId globalId, MbdObject mbdObject) {
        Map<String, MbdId> contents = mbdObject.getMap();

        // 先获取 键 对应的 ID
        List<String> keysList = contents.keySet().parallelStream().toList();
        LinkedHashMap<String, MbdId> keysIdMap = stringTypeSource.selectBatchId(keysList);

        return keysList.parallelStream().map(key -> {
            ObjectContent objectContent = new ObjectContent();
            objectContent.setGlobalId(MysqlMbdId.getValue(globalId));
            objectContent.setTheKey(MysqlMbdId.getValue(keysIdMap.get(key)));
            objectContent.setTheValue(MysqlMbdId.getValue(contents.get(key)));
            return objectContent;
        }).toList();
    }

    @Override
    protected MbdObject getValue(ObjectRecord objectRecord, List<ObjectContent> objectContents) {
        MbdJsonSchema mbdJsonSchema = jsonSchemaSource.selectById(new MysqlMbdId(objectRecord.getObjectSchema()));
        // 获取所有字段各自对应的 id
        JsonNode properties = mbdJsonSchema.getJsonSchema().getSchemaNode().at(
            stringTypeSource.selectById(new MysqlMbdId(objectRecord.getSchemaPath()))
        );

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
        objectContents.parallelStream().forEach(item -> {
            contents.put(
                keysIdCache.get(new MysqlMbdId(item.getTheKey())), // 获取字段名
                new MysqlMbdId(item.getTheValue()) // 获取字段引用 id
            );
        });

        // 生成最终结果
        PlainMbdObject mbdObject = new PlainMbdObject();
        mbdObject.setGlobalId(new MysqlMbdId(objectRecord.getGlobalId()));
        mbdObject.setName(stringTypeSource.selectById(new MysqlMbdId(objectRecord.getObjectName())));
        mbdObject.setSchema(mbdJsonSchema);
        mbdObject.setMap(contents);

        return mbdObject;
    }

    @Override
    protected LinkedHashMap<MbdId, MbdObject> getValue(LinkedHashMap<MbdId, ObjectRecord> firstPojoCollection, List<ObjectContent> secondPojoList) {
        int arrayLength = firstPojoCollection.size();

        ConcurrentHashMap<Long, String> stringCache = new ConcurrentHashMap<>(firstPojoCollection.size() * 2);
        ConcurrentHashMap<Long, MbdJsonSchema> jsonSchemaCache = new ConcurrentHashMap<>(firstPojoCollection.size());

        // 先给 ObjectContent 根据 id 分开来
        // globalId -> Map<fieldNameId, valueId>
        ConcurrentHashMap<Long, ConcurrentHashMap<Long, Long>> categorizedContent = new ConcurrentHashMap<>(firstPojoCollection.size());
        {
            // 临时记录需要批量查询的 ID
            ConcurrentHashMap<Long, Object> tmpFieldNameSet = new ConcurrentHashMap<>(secondPojoList.size() * 2);
            Object emptyObject = new Object();
            secondPojoList.parallelStream().forEach(objectContent -> {
                Long id = objectContent.getGlobalId();
                ConcurrentHashMap<Long, Long> fieldAndValue =
                    categorizedContent.putIfAbsent(id, new ConcurrentHashMap<>());
                if (fieldAndValue == null) {
                    fieldAndValue = categorizedContent.get(id);
                }
                fieldAndValue.put(objectContent.getTheKey(), objectContent.getTheValue());
                tmpFieldNameSet.put(objectContent.getTheKey(), emptyObject);
            });

            firstPojoCollection.values().parallelStream().forEach(objectRecord -> {
                jsonSchemaCache.put(objectRecord.getObjectSchema(), new PlainMbdJsonSchema());
                tmpFieldNameSet.put(objectRecord.getObjectName(), emptyObject);
                tmpFieldNameSet.put(objectRecord.getSchemaPath(), emptyObject);
            });

            // 批量查询 ID 对应的 字符串，并且缓存起来
            stringTypeSource
                .selectBatchById(tmpFieldNameSet.keySet().parallelStream().map(MbdId::new).toList())
                .entrySet()
                .parallelStream()
                .forEach(item -> stringCache.put(
                    MysqlMbdId.getValue(item.getKey()),
                    item.getValue()
                ));
        }

        jsonSchemaSource
            .selectBatchById(jsonSchemaCache.keySet().parallelStream().map(MbdId::new).toList())
            .entrySet()
            .parallelStream()
            .forEach(kv -> jsonSchemaCache.put(MysqlMbdId.getValue(kv.getKey()), kv.getValue()));

        return firstPojoCollection.entrySet().parallelStream().collect(Collectors.toMap(
            Map.Entry::getKey,
            kv -> {
                MbdId id = kv.getKey();
                ObjectRecord objectRecord = kv.getValue();

                PlainMbdObject mbdObject = new PlainMbdObject();
                mbdObject.setGlobalId(id);
                mbdObject.setSchema(jsonSchemaCache.get(objectRecord.getObjectSchema()));
                mbdObject.setSchemaPath(stringCache.get(objectRecord.getSchemaPath()));
                mbdObject.setName(stringCache.get(objectRecord.getObjectName()));

                mbdObject.setMap(categorizedContent.get(MysqlMbdId.getValue(id))
                    .entrySet()
                    .parallelStream()
                    .collect(Collectors.toMap(
                        item -> stringCache.get(item.getKey()),
                        item -> new MysqlMbdId(item.getValue()),
                        (key1, key2) -> key2,
                        LinkedHashMap::new
                    ))
                );

                return mbdObject;
            },
            (key1, key2) -> key2,
            LinkedHashMap::new
        ));
    }


    @Override
    protected Condition getEqualIdCondition(MbdId id) {
        return new ConditionImpl().op(ObjectContent.Fields.globalId, Op.eq, MysqlMbdId.getValue(id));
    }

    @Override
    protected Condition getEqualIdCondition(List<MbdId> idList) {
        return new ConditionImpl().op(
            ObjectContent.Fields.globalId,
            Op.in,
            idList.parallelStream().map(MysqlMbdId::getValue).toList()
        );
    }

    @Override
    protected MbdId getTableNameId(Class<?> pojoClass) {
        return coreTableCache.getTableNameId(pojoClass);
    }

    @Override
    public MbdObject getReactiveMbdObject(MbdId globalId, ReactiveMode mode) {
        return null;
    }
}
