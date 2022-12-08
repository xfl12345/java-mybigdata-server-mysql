package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl;

import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableBasicMapper;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableMapper;
import cc.xfl12345.mybigdata.server.common.pojo.FieldNotNullChecker;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.AbstractTypedTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.MapperProperties;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.BeeTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.BeeTableMapperImpl;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DaoPack {
    @Getter
    @Setter
    protected FieldNotNullChecker fieldNotNullChecker = DefaultSingleton.FIELD_NOT_NULL_CHECKER;

    @Getter
    @Setter
    protected MapperProperties mapperProperties;

    @Getter
    protected Map<Class<?>, BeeTableMapper<?>> mapperMap;

    @PostConstruct
    public void init() throws Exception {
        fieldNotNullChecker.check(mapperProperties, "mapperProperties");

        Collection<Class<?>> pojoClasses = mapperProperties.getCoreTableCache().getPojoClass2PojoInfoMap().keySet();
        mapperMap = new HashMap<>(pojoClasses.size());
        for (Class<?> pojoClass : pojoClasses) {
            BeeTableMapperImpl<?> mapper = new BeeTableMapperImpl<>(pojoClass);
            mapper.setMapperProperties(mapperProperties);
            mapper.init();

            mapperMap.put(pojoClass, mapper);
        }
    }

    @SuppressWarnings("unchecked")
    public <Pojo> TableBasicMapper<Pojo> getTableBasicMapper(Class<Pojo> cls) {
        return (TableBasicMapper<Pojo>) mapperMap.get(cls);
    }

    @SuppressWarnings("unchecked")
    public <Pojo> AbstractTypedTableMapper<Pojo>  getAbstractTypedTableMapper(Class<Pojo> cls) {
        return (AbstractTypedTableMapper<Pojo>) mapperMap.get(cls);
    }

    @SuppressWarnings("unchecked")
    public <Pojo> BeeTableMapper<Pojo> getBeeTableMapper(Class<Pojo> cls) {
        return (BeeTableMapper<Pojo>) mapperMap.get(cls);
    }
}
