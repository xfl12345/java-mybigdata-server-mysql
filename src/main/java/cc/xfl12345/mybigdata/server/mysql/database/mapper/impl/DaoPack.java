package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl;

import cc.xfl12345.mybigdata.server.common.appconst.AppConst;
import cc.xfl12345.mybigdata.server.common.appconst.AppDataType;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableBasicMapper;
import cc.xfl12345.mybigdata.server.common.utility.MyReflectUtils;
import cc.xfl12345.mybigdata.server.mysql.appconst.EnumCoreTable;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.TableMapperProperties;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.BeeTableMapperImpl;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import cc.xfl12345.mybigdata.server.mysql.pojo.MapperPack;
import com.alibaba.fastjson2.PropertyNamingStrategy;
import com.alibaba.fastjson2.util.BeanUtils;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.persistence.Table;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DaoPack {
    @Getter
    @Setter
    protected String fieldCanNotBeNullMessageTemplate = AppConst.FIELD_CAN_NOT_BE_NULL_MESSAGE_TEMPLATE;

    @Getter
    @Setter
    protected TableMapperProperties tableMapperProperties;

    protected Collection<Class<?>> pojoClasses;

    protected CopyOnWriteArrayList<MapperPack<?>> mapperPacks;

    protected ConcurrentHashMap<Class<?>, TableBasicMapper<?>> mappers;

    // MapperPack 字段名称 -> MapperPack 该字段的值 -> MapperPack
    protected Map<String, ConcurrentHashMap<Object, MapperPack<?>>> mapperPackMap;

    protected Map<Object, MapperPack<?>> cache4ClassMap;

    protected Map<Object, MapperPack<?>> cache4TableNameMap;

    @PostConstruct
    public void init() throws Exception {
        pojoClasses = MyReflectUtils.getClasses(
            GlobalDataRecord.class.getPackageName(),
            false,
            false,
            true
        );

        Map<String, Method> mapperPackGetterMap = new HashMap<>(8);
        mapperPackMap = new HashMap<>(8);
        // 获取 MapperPack 的 Getter
        BeanUtils.getters(MapperPack.class, method -> {
            // 获取 MapperPack 字段名称
            String fieldName = BeanUtils.getterName(
                method,
                PropertyNamingStrategy.CamelCase.name()
            );

            synchronized (mapperPackGetterMap) {
                mapperPackGetterMap.put(fieldName, method);
                mapperPackMap.put(
                    fieldName,
                    // 初始化容量为 pojo 个数
                    new ConcurrentHashMap<>(pojoClasses.size())
                );
            }
        });

        mappers = new ConcurrentHashMap<>(pojoClasses.size());
        mapperPacks = new CopyOnWriteArrayList<>();
        for (Class<?> pojoClass : pojoClasses) {
            BeeTableMapperImpl<?> mapper = new BeeTableMapperImpl<>(pojoClass);
            mapper.setTableMapperProperties(tableMapperProperties);
            mapper.init();

            mappers.put(pojoClass, mapper);

            MapperPack<?> mapperPack = generateMapperPack(mapper);
            mapperPacks.add(mapperPack);
            for (String fieldName : mapperPackGetterMap.keySet()) {
                // 获取 MapperPack 字段 对应的 Getter
                Method getter = mapperPackGetterMap.get(fieldName);
                // 按 字段 获取 值， 作为 二级映射表 的 键
                Object key = getter.invoke(mapperPack);
                // 我觉得非空检查有必要，而且既然是空的，就不应该有对应实体。
                // 按空取值，本应得空。
                if (key != null) {
                    // 按 字段名 获取 二级映射表
                    Map<Object, MapperPack<?>> map = mapperPackMap.get(fieldName);
                    map.put(key, mapperPack);
                }
            }
        }

        cache4ClassMap = mapperPackMap.get(MapperPack.Fields.pojoClass);
        cache4TableNameMap = mapperPackMap.get(MapperPack.Fields.tableName);
    }

    protected <T> MapperPack<T> generateMapperPack(TableBasicMapper<T> mapper) {
        Class<T> pojoClass = mapper.getPojoType();
        String databaseTableName = pojoClass.getAnnotation(Table.class).name();
        EnumCoreTable enumCoreTable = EnumCoreTable.getByName(databaseTableName);
        AppDataType dataType = null;

        if (enumCoreTable != null) {
            switch (enumCoreTable) {
                case TABLE_SCHEMA_RECORD -> dataType = AppDataType.JsonSchema;
                case STRING_CONTENT -> dataType = AppDataType.String;
                case BOOLEAN_CONTENT -> dataType = AppDataType.Boolean;
                case NUMBER_CONTENT -> dataType = AppDataType.Number;
                case GROUP_RECORD -> dataType = AppDataType.Array;
                case OBJECT_RECORD -> dataType = AppDataType.Object;
            }
        }

        return MapperPack.<T>builder()
            .pojoClass(pojoClass)
            .dataType(dataType)
            .mapper(mapper)
            .coreTable(enumCoreTable)
            .tableName(databaseTableName)
            .build();
    }

    public MapperPack<?> getMapperPack(String fieldName, Object fieldValue) {
        return mapperPackMap.get(fieldName).get(fieldValue);
    }

    @SuppressWarnings("unchecked")
    public <T> MapperPack<T> getMapperPackByPojoClass(Class<T> pojoClass) {
        return (MapperPack<T>) cache4ClassMap.get(pojoClass);
    }

    @SuppressWarnings("unchecked")
    public <T> MapperPack<T> getMapperPackByTableName(String tableName) {
        return (MapperPack<T>) cache4TableNameMap.get(tableName);
    }

    @SuppressWarnings("unchecked")
    public <Pojo> TableBasicMapper<Pojo> getMapper(Class<Pojo> cls) {
        return (TableBasicMapper<Pojo>) mappers.get(cls);
    }

    public Collection<Class<?>> getPojoClasses() {
        return pojoClasses;
    }
}
