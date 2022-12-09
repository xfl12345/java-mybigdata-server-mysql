package cc.xfl12345.mybigdata.server.mysql.data.source.base.raw;


import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.data.source.DataSource;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableMapper;
import cc.xfl12345.mybigdata.server.common.pojo.AffectedRowsCountChecker;
import cc.xfl12345.mybigdata.server.common.pojo.MbdId;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class AbstractIndependentTableRawDataSource<Value, Pojo, Condition> implements DataSource<Value> {
    @Getter
    @Setter
    protected AffectedRowsCountChecker affectedRowsCountChecker = DefaultSingleton.AFFECTED_ROWS_COUNT_CHECKER;

    protected TableMapper<Pojo, Condition> mapper;

    public AbstractIndependentTableRawDataSource(TableMapper<Pojo, Condition> mapper) {
        this.mapper = mapper;
    }

    protected abstract String[] getSelectContentFieldOnly();

    protected abstract Value getValue(Pojo pojo);

    protected abstract Pojo getPojo(Value value);

    protected abstract String getTableName();

    @Override
    public MbdId<?> insert4IdOrGetId(Value value) {
        // 由于不是原子操作，所以理应禁止使用。
        throw new UnsupportedOperationException();
    }

    @Override
    public MbdId<?> insertAndReturnId(Value value) {
        return mapper.insertAndReturnId(getPojo(value));
    }

    @Override
    public long insert(Value value) {
        return mapper.insert(getPojo(value));
    }

    @Override
    public long insertBatch(List<Value> values) {
        return mapper.insertBatch(values.parallelStream().map(this::getPojo).toList());
    }

    @Override
    public MbdId<?> selectId(Value value) {
        try {
            mapper.setForUpdate(true);
            return mapper.selectId(getPojo(value));
        } finally {
            mapper.clearForUpdateFlag();
        }
    }

    @Override
    public Value selectById(MbdId<?> globalId) {
        try {
            mapper.setForUpdate(true);
            return getValue(mapper.selectById(globalId, getSelectContentFieldOnly()));
        } finally {
            mapper.clearForUpdateFlag();
        }
    }

    @Override
    public List<Value> selectBatchById(List<MbdId<?>> globalIdList) {
        try {
            mapper.setForUpdate(true);
            return mapper.selectBatchById(globalIdList).parallelStream().map(this::getValue).toList();
        } finally {
            mapper.clearForUpdateFlag();
        }
    }

    @Override
    public void updateById(Value value, MbdId<?> globalId) {
        mapper.updateById(getPojo(value), globalId);
    }

    @Override
    public void deleteById(MbdId<?> globalId) {
        mapper.deleteById(globalId);
    }

    @Override
    public void deleteBatchById(List<MbdId<?>> globalIdList) {
        mapper.deleteBatchById(globalIdList);
    }


    @SuppressWarnings("unchecked")
    protected <T> Class<T> getTypeFromRuntime(int typeArgumentIndex) {
        return (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[typeArgumentIndex];
    }

    @Override
    public Class<Value> getValueType() {
        return getTypeFromRuntime(0);
    }

    public Class<Pojo> getPojoType() {
        return mapper == null ? getTypeFromRuntime(1) : mapper.getPojoType();
    }
}
