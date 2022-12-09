package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee;

import cc.xfl12345.mybigdata.server.common.appconst.CURD;
import cc.xfl12345.mybigdata.server.common.pojo.MbdId;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.AbstractTypedTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.config.BeeTableMapperConfig;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.config.BeeTableMapperConfigGenerator;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.AuthAccount;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;
import lombok.Getter;
import lombok.Setter;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.ConditionImpl;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;

public class BeeTableMapperImpl<Pojo>
    extends AbstractTypedTableMapper<Pojo> implements BeeTableMapper<Pojo> {
    protected ThreadLocal<Boolean> forUpdate = new ThreadLocal<>();

    protected Class<Pojo> pojoClass;

    @Getter
    @Setter
    protected BeeTableMapperConfig<Pojo> mapperConfig;

    protected String[] selectIdFieldOnly;

    protected Function<Pojo, MysqlMbdId> insertAndReturnIdImpl = (pojo) -> {
        throw new UnsupportedOperationException();
    };

    public BeeTableMapperImpl(Class<Pojo> pojoClass) {
        this.pojoClass = pojoClass;
    }

    @PostConstruct
    public void init() throws Exception {
        Class<Pojo> pojoClass = getPojoType();
        // 仅仅支持有 自增主键 的表
        if (GlobalDataRecord.class.equals(pojoClass) || AuthAccount.class.equals(pojoClass)) {
            insertAndReturnIdImpl = (pojo) -> new MysqlMbdId(getSuidRich().insertAndReturnId(pojo));
        }

        if (mapperConfig == null) {
            mapperConfig = BeeTableMapperConfigGenerator.getConfig(
                mapperProperties.getCoreTableCache().getPoInfo(getPojoType())
            );
        }

        selectIdFieldOnly = new String[]{mapperConfig.getIdFieldName()};
        super.init();
    }

    @Override
    public String getTableName() {
        return mapperConfig.getTableName();
    }

    @Override
    public Class<Pojo> getPojoType() {
        return pojoClass;
    }

    @Override
    public long insert(Pojo pojo) {
        long affectedRowCount = getSuidRich().insert(pojo);
        checkAffectedRowShouldBeOne(affectedRowCount, CURD.CREATE);
        return affectedRowCount;
    }

    @Override
    public long insertBatch(List<Pojo> pojoList) {
        long affectedRowCount = getSuidRich().insert(pojoList);
        checkAffectedRowsCountDoesNotMatch(affectedRowCount, pojoList.size(), CURD.CREATE);
        return affectedRowCount;
    }

    @Override
    public MbdId<?> insertAndReturnId(Pojo pojo) {
        return insertAndReturnIdImpl.apply(pojo);
    }

    @Override
    public List<Pojo> selectByCondition(Condition condition) {
        intiConditionWithForUpdate(condition);
        return getSuidRich().select(mapperConfig.getNewPojoInstance(), condition);
    }

    @Override
    public Pojo selectOne(Pojo pojo, String... fields) {
        Condition condition = getConditionWithSelectedFields(fields);
        intiConditionWithForUpdate(condition);
        List<Pojo> items = getSuidRich().select(pojo, condition);
        checkAffectedRowShouldBeOne(items.size(), CURD.RETRIEVE);

        return items.get(0);
    }

    @Override
    public Pojo selectById(MbdId<?> globalId, String... fields) {
        Condition condition = getConditionWithSelectedFields(fields);
        intiConditionWithForUpdate(condition);
        addId2Condition(condition, globalId);
        List<Pojo> items = getSuidRich().select(mapperConfig.getNewPojoInstance(), condition);
        checkAffectedRowShouldBeOne(items.size(), CURD.RETRIEVE);

        return items.get(0);
    }

    @Override
    public List<Pojo> selectBatchById(List<MbdId<?>> globalIdList, String... fields) {
        Condition condition = getConditionWithSelectedFields(fields);
        intiConditionWithForUpdate(condition);
        addMbdIdList2Condition(globalIdList, condition);
        List<Pojo> result = getSuidRich().select(mapperConfig.getNewPojoInstance(), condition);
        checkAffectedRowsCountDoesNotMatch(result.size(), globalIdList.size(), CURD.RETRIEVE);
        return result;
    }

    @Override
    public MbdId<?> selectId(Pojo pojo) {
        Pojo item = selectOne(pojo, selectIdFieldOnly);
        return mapperConfig.getId(item);
    }

    @Override
    public long updateByCondition(Pojo pojo, Condition condition) {
        return getSuidRich().update(pojo, condition);
    }

    @Override
    public void updateById(Pojo pojo, MbdId<?> globalId) {
        mapperConfig.setId(pojo, globalId);
        long affectedRowCount = getSuidRich().updateBy(pojo, mapperConfig.getIdFieldName());
        checkAffectedRowShouldBeOne(affectedRowCount, CURD.UPDATE);
    }

    @Override
    public long deleteByCondition(Condition condition) {
        return getSuidRich().delete(mapperConfig.getNewPojoInstance(), condition);
    }

    @Override
    public void deleteById(MbdId<?> globalId) {
        long affectedRowCount = getSuidRich().delete(mapperConfig.getNewPojoInstance(), getConditionWithId(globalId));
        checkAffectedRowShouldBeOne(affectedRowCount, CURD.DELETE);
    }

    @Override
    public void deleteBatchById(List<MbdId<?>> globalIdList) {
        Condition condition = new ConditionImpl();
        addMbdIdList2Condition(globalIdList, condition);
        long affectedRowCount = getSuidRich().delete(mapperConfig.getNewPojoInstance(), condition);
        checkAffectedRowsCountDoesNotMatch(affectedRowCount, globalIdList.size(), CURD.DELETE);
    }

    @Override
    public boolean isForUpdate() {
        return Boolean.TRUE.equals(this.forUpdate.get());
    }

    @Override
    public void setForUpdate(boolean forUpdate) {
        this.forUpdate.set(forUpdate);
    }

    @Override
    public void clearForUpdateFlag() {
        this.forUpdate.remove();
    }

    protected void intiConditionWithForUpdate(Condition condition) {
        if (isForUpdate()) {
            condition.forUpdate();
        }
    }

    protected SuidRich getSuidRich() {
        return BeeFactory.getHoneyFactory().getSuidRich();
    }

    protected void addMbdIdList2Condition(List<MbdId<?>> idList, Condition condition) {
        condition.op(
            mapperConfig.getIdFieldName(),
            Op.in,
            idList.parallelStream().map(MysqlMbdId::getValue).toList()
        );
    }

    @Override
    public Condition getConditionWithSelectedFields(String... fields) {
        Condition condition = new ConditionImpl();
        addFields2Condition(condition, fields);
        return condition;
    }

    @Override
    public void addFields2Condition(Condition condition, String... fields) {
        if (fields != null && fields.length > 0) {
            condition.selectField(fields);
        }
    }

    @Override
    public Condition getConditionWithId(MbdId<?> id) {
        Condition condition = new ConditionImpl();
        addId2Condition(condition, id);
        return condition;
    }

    @Override
    public void addId2Condition(Condition condition, MbdId<?> id) {
        condition.op(mapperConfig.getIdFieldName(), Op.eq, id.getValue());
    }
}
