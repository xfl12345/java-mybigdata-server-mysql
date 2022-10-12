package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl;

import cc.xfl12345.mybigdata.server.common.database.mapper.TableBasicMapper;
import cc.xfl12345.mybigdata.server.common.utility.MyReflectUtils;
import cc.xfl12345.mybigdata.server.mysql.database.converter.AppIdTypeConverter;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.AbstractTypedTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.CoreTableCache;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.BeeTableMapperImpl;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import com.fasterxml.uuid.NoArgGenerator;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class DaoManager {
    @Getter
    @Setter
    protected AppIdTypeConverter idTypeConverter;

    @Getter
    @Setter
    protected CoreTableCache coreTableCache;

    @Getter
    @Setter
    protected NoArgGenerator uuidGenerator;

    protected ConcurrentHashMap<Class<?>, TableBasicMapper<?>> mappers;

    @PostConstruct
    public void init() throws Exception {
        Collection<Class<?>> pojoClasses = MyReflectUtils.getClasses(
            GlobalDataRecord.class.getPackageName(),
            false,
            false,
            true
        );

        mappers = new ConcurrentHashMap<>(pojoClasses.size());

        for (Class<?> pojoClass : pojoClasses) {
            AbstractTypedTableMapper<?> mapper = new BeeTableMapperImpl<>(pojoClass);
            mapper.setIdTypeConverter(idTypeConverter);
            mapper.setCoreTableCache(coreTableCache);
            mapper.setUuidGenerator(uuidGenerator);
            mapper.afterPropertiesSet();

            mappers.put(pojoClass, mapper);
        }
    }

    @SuppressWarnings("unchecked")
    public <Pojo> TableBasicMapper<Pojo> getMapper(Class<Pojo> cls) {
        return (TableBasicMapper<Pojo>) mappers.get(cls);
    }
}
