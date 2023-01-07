package cc.xfl12345.mybigdata.server.mysql.data.source.base;


import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.data.source.DataSource;
import cc.xfl12345.mybigdata.server.common.data.source.impl.AbstractDataSource;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableMapper;
import cc.xfl12345.mybigdata.server.common.pojo.AffectedRowsCountChecker;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractIndependentTableDataSource<Value, Pojo, Condition>
    extends AbstractDataSource<Value>
    implements DataSource<Value> {

    @Getter
    @Setter
    protected AffectedRowsCountChecker affectedRowsCountChecker = DefaultSingleton.AFFECTED_ROWS_COUNT_CHECKER;

    protected abstract TableMapper<Pojo, Condition> getTableMapper();

    protected abstract String[] getFieldNames4Select();

    protected abstract Value getValue(Pojo pojo);

    protected abstract Pojo getPojo(Value value);

    @Override
    public MbdId insertAndReturnId(Value value) {
        return getTableMapper().insertAndReturnId(getPojo(value));
    }

    @Override
    public long insert(Value value) {
        return getTableMapper().insert(getPojo(value));
    }

    @Override
    public long insertBatch(List<Value> values) {
        return getTableMapper().insertBatch(values.parallelStream().map(this::getPojo).toList());
    }

    @Override
    public MbdId selectId(Value value) {
        try {
            getTableMapper().setForUpdate(true);
            return getTableMapper().selectId(getPojo(value));
        } finally {
            getTableMapper().clearForUpdateFlag();
        }
    }

    @Override
    public Value selectById(MbdId globalId) {
        try {
            getTableMapper().setForUpdate(true);
            return getValue(getTableMapper().selectById(globalId, getFieldNames4Select()));
        } finally {
            getTableMapper().clearForUpdateFlag();
        }
    }

    @Override
    public LinkedHashMap<MbdId, Value> selectBatchById(List<MbdId> globalIdList) {
        try {
            getTableMapper().setForUpdate(true);
            return getTableMapper().selectBatchById(globalIdList).entrySet().parallelStream().collect(Collectors.toMap(
                Map.Entry::getKey,
                kv -> getValue(kv.getValue()),
                (key1, key2) -> key2,
                LinkedHashMap::new
            ));
        } finally {
            getTableMapper().clearForUpdateFlag();
        }
    }

    @Override
    public void updateById(Value value, MbdId globalId) {
        getTableMapper().updateById(getPojo(value), globalId);
    }

    @Override
    public void deleteById(MbdId globalId) {
        getTableMapper().deleteById(globalId);
    }

    @Override
    public void deleteBatchById(List<MbdId> globalIdList) {
        getTableMapper().deleteBatchById(globalIdList);
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
        return getTableMapper() == null ? getTypeFromRuntime(1) : getTableMapper().getPojoType();
    }
}
