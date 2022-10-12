package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee;

import cc.xfl12345.mybigdata.server.common.appconst.CURD;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.AbstractTypedTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.config.BeeTableMapperConfig;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.config.BeeTableMapperConfigGenerator;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import lombok.Getter;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.ConditionImpl;

import java.util.Date;
import java.util.List;

public class BeeTableMapperImpl<Pojo>
    extends AbstractTypedTableMapper<Pojo> implements BeeTableMapper<Pojo> {
    protected Class<Pojo> pojoClass;

    @Override
    public Class<Pojo> getGenericType() {
        return pojoClass;
    }

    public BeeTableMapperImpl(Class<Pojo> pojoClass) {
        this.pojoClass = pojoClass;
    }

    @Getter
    protected BeeTableMapperConfig<Pojo> mapperConfig;

    public void setMapperConfig(BeeTableMapperConfig<Pojo> mapperConfig) {
        this.mapperConfig = mapperConfig;
    }

    protected String[] selectIdFieldOnly;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (mapperConfig == null) {
            mapperConfig = BeeTableMapperConfigGenerator.getConfig(pojoClass);
        }

        selectIdFieldOnly = new String[]{ mapperConfig.getIdFieldName() };
    }

    @Override
    public long insert(Pojo value) {
        return getSuidRich().insert(value);
    }

    @Override
    public long insertBatch(List<Pojo> values) {
        return getSuidRich().insert(values);
    }

    @Override
    public Object insertAndReturnId(Pojo value) {
        return getSuidRich().insertAndReturnId(value);
    }

    @Override
    public List<Pojo> select(Condition condition) {
        return getSuidRich().select(mapperConfig.getNewPojoInstance(), condition);
    }

    @Override
    public Pojo selectOne(Pojo value, String[] fields) {
        List<Pojo> items = getSuidRich().select(value, getConditionWithSelectedFields(fields));
        if (items.size() != 1) {
            throw getAffectedRowShouldBe1Exception(items.size(), CURD.RETRIEVE, mapperConfig.getTableName());
        }

        return items.get(0);
    }

    @Override
    public Pojo selectById(Object globalId, String[] fields) {
        Condition condition = getConditionWithSelectedFields(fields);
        addId2Condition(condition, globalId);
        List<Pojo> items = getSuidRich().select(mapperConfig.getNewPojoInstance(), condition);
        if (items.size() != 1) {
            throw getAffectedRowShouldBe1Exception(items.size(), CURD.RETRIEVE, mapperConfig.getTableName());
        }

        return items.get(0);
    }

    @Override
    public Object selectId(Pojo value) {
        Pojo item = selectOne(value, selectIdFieldOnly);
        return mapperConfig.getId(item);
    }

    @Override
    public long update(Pojo value, Condition condition) {
        return getSuidRich().update(value, condition);
    }

    @Override
    public void updateById(Pojo value, Object globalId) {
        long affectedRowCount = 0;
        affectedRowCount = getSuidRich().update(value, getConditionWithId(globalId));
        if (affectedRowCount != 1) {
            throw getUpdateShouldBe1Exception(affectedRowCount, mapperConfig.getTableName());
        }
    }

    @Override
    public long delete(Condition condition) {
        return getSuidRich().delete(condition);
    }

    @Override
    public void deleteById(Object globalId) {
        long affectedRowCount = 0;
        affectedRowCount = getSuidRich().delete(mapperConfig.getNewPojoInstance(), getConditionWithId(globalId));
        if (affectedRowCount != 1) {
            throw getAffectedRowShouldBe1Exception(affectedRowCount, CURD.DELETE, mapperConfig.getTableName());
        }
    }

    @Override
    public GlobalDataRecord getNewRegisteredGlobalDataRecord(Date createTime, Object tableNameId) {
        GlobalDataRecord globalDataRecord = getNewGlobalDataRecord(createTime, tableNameId);
        SuidRich suid = BeeFactory.getHoneyFactory().getSuidRich();
        Object id = suid.insertAndReturnId(globalDataRecord);
        idTypeConverter.injectId2Object(tableNameId, globalDataRecord::setId);
        return globalDataRecord;
    }

    public SuidRich getSuidRich() {
        return BeeFactory.getHoneyFactory().getSuidRich();
    }

    @Override
    public Condition getConditionWithSelectedFields(String... fields) {
        Condition condition = new ConditionImpl();
        addFields2Condition(condition, fields);
        return condition;
    }

    @Override
    public void addFields2Condition(Condition condition, String... fields) {
        if (fields != null) {
            condition.selectField(fields);
        }
    }

    @Override
    public Condition getConditionWithId(Object id) {
        Condition condition = new ConditionImpl();
        addId2Condition(condition, id);
        return condition;
    }

    @Override
    public void addId2Condition(Condition condition, Object id) {
        condition.op(mapperConfig.getIdFieldName(), Op.eq, id);
    }
}
