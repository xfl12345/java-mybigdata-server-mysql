package cc.xfl12345.mybigdata.server.mysql.data.source.base;


import cc.xfl12345.mybigdata.server.common.data.source.IdDataSource;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractDoubleLayerTableDataSource<Value, Pojo, Condition>
    extends AbstractIndependentTableDataSource<Value, Pojo, Condition> {

    protected abstract Pojo getPojo(MbdId globalId, Value value);

    protected abstract MbdId getTableNameId(Class<?> pojoClass);

    protected abstract IdDataSource getIdDataSource();

    @Override
    public long insertBatch(List<Value> values) {
        long affectedRowCount = 0;
        List<MbdId> globalDataRecords = getIdDataSource().getNewRegisteredIds(
            new Date(),
            getTableNameId(getTableMapper().getPojoType()),
            values.size()
        );
        affectedRowCount += globalDataRecords.size();
        List<Pojo> pojoList = new ArrayList<>(values.size());
        for (int i = 0; i < values.size(); i++) {
            pojoList.add(getPojo(globalDataRecords.get(i), values.get(i)));
        }
        affectedRowCount += getTableMapper().insertBatch(pojoList);
        return affectedRowCount;
    }

    @Override
    public void updateById(Value value, MbdId globalId) {
        Date updateTime = new Date();
        MbdId mbdId = getIdDataSource().selectById(globalId);
        super.updateById(value, globalId);
        getIdDataSource().updateOneRow(mbdId, updateTime);
    }
}
