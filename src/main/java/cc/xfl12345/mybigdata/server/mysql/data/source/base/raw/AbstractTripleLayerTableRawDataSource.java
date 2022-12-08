package cc.xfl12345.mybigdata.server.mysql.data.source.base.raw;


import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.data.source.DataSource;
import cc.xfl12345.mybigdata.server.common.data.source.GlobalDataRecordDataSource;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableMapper;
import cc.xfl12345.mybigdata.server.common.database.pojo.CommonGlobalDataRecord;
import cc.xfl12345.mybigdata.server.common.pojo.AffectedRowsCountChecker;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractTripleLayerTableRawDataSource<Value, FirstPojo, SecondPojo, Condition> implements DataSource<Value> {
    @Getter
    @Setter
    protected AffectedRowsCountChecker affectedRowsCountChecker = DefaultSingleton.AFFECTED_ROWS_COUNT_CHECKER;

    protected GlobalDataRecordDataSource globalDataRecordDataSource;

    protected TableMapper<FirstPojo, Condition> firstMapper;

    protected TableMapper<SecondPojo, Condition> secondMapper;

    public AbstractTripleLayerTableRawDataSource(
        GlobalDataRecordDataSource globalDataRecordDataSource,
        TableMapper<FirstPojo, Condition> firstMapper,
        TableMapper<SecondPojo, Condition> secondMapper) {
        this.globalDataRecordDataSource = globalDataRecordDataSource;
        this.firstMapper = firstMapper;
        this.secondMapper = secondMapper;
    }

    protected abstract FirstPojo getFirstPojo(Object globalId, Value value);

    protected abstract List<SecondPojo> getSecondPojo(Object globalId, Value value);

    protected abstract Value getValue(FirstPojo firstPojo, List<SecondPojo> secondPojoList);

    protected abstract List<Value> getValue(List<FirstPojo> firstPojoList, List<SecondPojo> secondPojoList);

    protected abstract Condition getEqualIdCondition(Object id);

    protected abstract Condition getEqualIdAndSortCondition(Object id);

    protected abstract Condition getEqualIdCondition(List<Object> idList);


    @Override
    public Object insert4IdOrGetId(Value value) {
        // 由于不是原子操作，所以理应禁止使用。
        throw new UnsupportedOperationException();
    }

    @Override
    public Object insertAndReturnId(Value value) {
        CommonGlobalDataRecord globalDataRecord = globalDataRecordDataSource
            .getNewRegisteredDataInstance(new Date(), firstMapper.getPojoType());
        Object id = globalDataRecord.getId();
        firstMapper.insert(getFirstPojo(id, value));
        secondMapper.insertBatch(getSecondPojo(id, value));
        return id;
    }

    @Override
    public long insert(Value value) {
        long affectedRowsCount = 0;
        CommonGlobalDataRecord globalDataRecord = globalDataRecordDataSource
            .getNewRegisteredDataInstance(new Date(), firstMapper.getPojoType());
        Object id = globalDataRecord.getId();
        // 已影响到 全局记录表 的一行
        affectedRowsCount += 1;
        affectedRowsCount += firstMapper.insert(getFirstPojo(id, value));
        affectedRowsCount += secondMapper.insertBatch(getSecondPojo(id, value));
        return affectedRowsCount;
    }

    @Override
    public long insertBatch(List<Value> values) {
        long affectedRowsCount = 0;
        List<CommonGlobalDataRecord> globalDataRecords = globalDataRecordDataSource
            .getNewRegisteredDataInstances(new Date(), firstMapper.getPojoType(), values.size());
        affectedRowsCount += globalDataRecords.size();

        List<FirstPojo> firstPojoList = new ArrayList<>(values.size());
        List<SecondPojo> secondPojoList = new ArrayList<>(values.size());
        for (int i = 0; i < values.size(); i++) {
            Object id = globalDataRecords.get(i).getId();
            Value value = values.get(i);
            firstPojoList.add(getFirstPojo(id, value));
            secondPojoList.addAll(getSecondPojo(id, value));
        }

        affectedRowsCount += firstMapper.insertBatch(firstPojoList);
        affectedRowsCount += secondMapper.insertBatch(secondPojoList);
        return affectedRowsCount;
    }

    @Override
    public Value selectById(Object globalId) {
        FirstPojo firstPojo = firstMapper.selectById(globalId);
        List<SecondPojo> secondPojoList = secondMapper.selectByCondition(getEqualIdAndSortCondition(globalId));
        return getValue(firstPojo, secondPojoList);
    }

    @Override
    public List<Value> selectBatchById(List<Object> globalIdList) {
        List<FirstPojo> firstPojoList = firstMapper.selectBatchById(globalIdList);
        List<SecondPojo> secondPojoList = secondMapper.selectByCondition(getEqualIdCondition(globalIdList));
        return getValue(firstPojoList, secondPojoList);
    }

    @Override
    public void updateById(Value value, Object globalId) {
        Date date = new Date();
        CommonGlobalDataRecord globalDataRecord = globalDataRecordDataSource.selectById(globalId);
        FirstPojo firstPojo = getFirstPojo(globalId, value);
        List<SecondPojo> secondPojoList = getSecondPojo(globalId, value);
        secondMapper.deleteByCondition(getEqualIdCondition(globalId));
        firstMapper.updateById(firstPojo, globalId);
        secondMapper.insertBatch(secondPojoList);
        globalDataRecordDataSource.updateOneRow(globalDataRecord, date);
    }

    @Override
    public void deleteById(Object globalId) {
        // 更新类型的操作，先上锁
        globalDataRecordDataSource.selectById(globalId);
        // 删除从表记录
        secondMapper.deleteByCondition(getEqualIdCondition(globalId));
        firstMapper.deleteById(globalId);
        // 删除主表记录
        globalDataRecordDataSource.deleteById(globalId);
    }

    @Override
    public void deleteBatchById(List<Object> globalIdList) {
        globalDataRecordDataSource.selectBatchById(globalIdList);
        secondMapper.deleteBatchById(globalIdList);
        firstMapper.deleteBatchById(globalIdList);
        globalDataRecordDataSource.deleteBatchById(globalIdList);
    }
}
