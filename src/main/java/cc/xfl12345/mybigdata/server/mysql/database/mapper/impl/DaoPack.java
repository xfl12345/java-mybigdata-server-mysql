package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl;

import cc.xfl12345.mybigdata.server.common.appconst.AppConst;
import cc.xfl12345.mybigdata.server.common.appconst.AppDataType;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableBasicMapper;
import cc.xfl12345.mybigdata.server.common.utility.MyReflectUtils;
import cc.xfl12345.mybigdata.server.mysql.appconst.EnumCoreTable;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.TableMapperProperties;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.BeeTableMapperImpl;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import cc.xfl12345.mybigdata.server.mysql.pojo.ClassDeclaredInfo;
import cc.xfl12345.mybigdata.server.mysql.pojo.MapperPack;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.persistence.Table;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
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

        Map<String, PropertyDescriptor> mapperPackPropertiesMap;

        // 临时内存，及时释放
        {
            BeanInfo beanInfo = Introspector.getBeanInfo(MapperPack.class, MapperPack.class.getSuperclass());
            mapperPackPropertiesMap = new HashMap<>(beanInfo.getPropertyDescriptors().length);
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                mapperPackPropertiesMap.put(descriptor.getDisplayName(), descriptor);
            }
        }

        // 初始化容量为 MapperPack 属性个数
        mapperPackMap = new HashMap<>(mapperPackPropertiesMap.keySet().size());
        mapperPackPropertiesMap.keySet().forEach(propertyName -> mapperPackMap.put(
            propertyName,
            // 初始化容量为 pojo 个数
            new ConcurrentHashMap<>(pojoClasses.size())
        ));

        mappers = new ConcurrentHashMap<>(pojoClasses.size());
        mapperPacks = new CopyOnWriteArrayList<>();
        for (Class<?> pojoClass : pojoClasses) {
            BeeTableMapperImpl<?> mapper = new BeeTableMapperImpl<>(pojoClass);
            mapper.setTableMapperProperties(tableMapperProperties);
            mapper.init();

            mappers.put(pojoClass, mapper);

            MapperPack<?> mapperPack = mapper.getMapperPack();
            mapperPacks.add(mapperPack);
            for (String fieldName : mapperPackPropertiesMap.keySet()) {
                // 获取 MapperPack 字段 对应的 Getter
                Method getter = mapperPackPropertiesMap.get(fieldName).getReadMethod();
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
