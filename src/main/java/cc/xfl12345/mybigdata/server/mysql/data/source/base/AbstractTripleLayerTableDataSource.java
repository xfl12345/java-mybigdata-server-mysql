package cc.xfl12345.mybigdata.server.mysql.data.source.base;


import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.data.source.DataSource;
import cc.xfl12345.mybigdata.server.common.data.source.IdDataSource;
import cc.xfl12345.mybigdata.server.common.data.source.impl.AbstractDataSource;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableBasicMapper;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableMapper;
import cc.xfl12345.mybigdata.server.common.pojo.AffectedRowsCountChecker;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class AbstractTripleLayerTableDataSource<Value, FirstPojo, SecondPojo, Condition>
    extends AbstractDataSource<Value>
    implements DataSource<Value> {

    @Getter
    @Setter
    protected AffectedRowsCountChecker affectedRowsCountChecker = DefaultSingleton.AFFECTED_ROWS_COUNT_CHECKER;

    @Getter
    @Setter
    protected IdDataSource idDataSource;

    @Getter
    @Setter
    protected TableMapper<FirstPojo, Condition> firstMapper;

    @Getter
    @Setter
    protected TableMapper<SecondPojo, Condition> secondMapper;

    @Override
    public void init() throws Exception {
        fieldNotNullChecker.check(idDataSource, MbdId.class);
        fieldNotNullChecker.check(firstMapper, getFirstPojoType());
        fieldNotNullChecker.check(secondMapper, getSecondPojoType());
        super.init();
    }

    protected abstract FirstPojo getFirstPojo(MbdId globalId, Value value);

    protected abstract List<SecondPojo> getSecondPojo(MbdId globalId, Value value);

    protected abstract Value getValue(FirstPojo firstPojo, List<SecondPojo> secondPojoList);

    protected abstract LinkedHashMap<MbdId, Value> getValue(LinkedHashMap<MbdId, FirstPojo> firstPojoCollection, List<SecondPojo> secondPojoList);

    protected abstract Condition getEqualIdCondition(MbdId id);

    protected abstract Condition getEqualIdCondition(List<MbdId> idList);

    protected abstract MbdId getTableNameId(Class<?> pojoClass);

    @Override
    public MbdId selectIdOrInsert4Id(Value value) {
        // 由于不是原子操作，所以理应禁止使用。
        throw new UnsupportedOperationException();
    }

    @Override
    public MysqlMbdId insertAndReturnId(Value value) {
        MysqlMbdId id = new MysqlMbdId(
            idDataSource.getNewRegisteredId(new Date(), getTableNameId(firstMapper.getPojoType()))
        );
        firstMapper.insert(getFirstPojo(id, value));
        secondMapper.insertBatch(getSecondPojo(id, value));
        return id;
    }

    @Override
    public long insert(Value value) {
        long affectedRowsCount = 0;
        MbdId id = idDataSource
            .getNewRegisteredId(new Date(), getTableNameId(firstMapper.getPojoType()));
        // 已影响到 全局记录表 的一行
        affectedRowsCount += 1;
        affectedRowsCount += firstMapper.insert(getFirstPojo(id, value));
        affectedRowsCount += secondMapper.insertBatch(getSecondPojo(id, value));
        return affectedRowsCount;
    }

    @Override
    public long insertBatch(List<Value> values) {
        long affectedRowsCount = 0;
        List<MbdId> mbdIds = idDataSource
            .getNewRegisteredIds(new Date(), getTableNameId(firstMapper.getPojoType()), values.size());
        affectedRowsCount += mbdIds.size();

        List<FirstPojo> firstPojoList = new ArrayList<>(values.size());
        List<SecondPojo> secondPojoList = new ArrayList<>(values.size());
        for (int i = 0; i < values.size(); i++) {
            MysqlMbdId id = new MysqlMbdId(mbdIds.get(i));
            Value value = values.get(i);
            firstPojoList.add(getFirstPojo(id, value));
            secondPojoList.addAll(getSecondPojo(id, value));
        }

        affectedRowsCount += firstMapper.insertBatch(firstPojoList);
        affectedRowsCount += secondMapper.insertBatch(secondPojoList);
        return affectedRowsCount;
    }

    @Override
    public LinkedHashMap<MbdId, Value> selectBatchById(List<MbdId> globalIdList) {
        LinkedHashMap<MbdId, FirstPojo> firstPojoList = firstMapper.selectBatchById(globalIdList);
        List<SecondPojo> secondPojoList = secondMapper.selectByCondition(getEqualIdCondition(globalIdList));
        return getValue(firstPojoList, secondPojoList);
    }

    @Override
    public void updateById(Value value, MbdId globalId) {
        Date date = new Date();
        MbdId mbdId = idDataSource.selectById(globalId);
        FirstPojo firstPojo = getFirstPojo(globalId, value);
        List<SecondPojo> secondPojoList = getSecondPojo(globalId, value);
        secondMapper.deleteByCondition(getEqualIdCondition(globalId));
        firstMapper.updateById(firstPojo, globalId);
        secondMapper.insertBatch(secondPojoList);
        idDataSource.updateOneRow(mbdId, date);
    }

    @Override
    public void deleteById(MbdId globalId) {
        // 更新类型的操作，先上锁
        idDataSource.selectById(globalId);
        // 删除从表记录
        secondMapper.deleteByCondition(getEqualIdCondition(globalId));
        firstMapper.deleteById(globalId);
        // 删除主表记录
        idDataSource.deleteById(globalId);
    }

    @Override
    public void deleteBatchById(List<MbdId> globalIdList) {
        idDataSource.selectBatchById(globalIdList);
        secondMapper.deleteBatchById(globalIdList);
        firstMapper.deleteBatchById(globalIdList);
        idDataSource.deleteBatchById(globalIdList);
    }

    @Override
    public Class<Value> getValueType() {
        return getTypeFromRuntime(0);
    }

    protected <T> Class<T> getMapperPojoType(TableBasicMapper<T> mapper, int genericTypeIndex) {
        return mapper == null ? getTypeFromRuntime(genericTypeIndex) : mapper.getPojoType();
    }

    public Class<FirstPojo> getFirstPojoType() {
        return getMapperPojoType(firstMapper, 1);
    }

    public Class<SecondPojo> getSecondPojoType() {
        return getMapperPojoType(secondMapper, 2);
    }
}
