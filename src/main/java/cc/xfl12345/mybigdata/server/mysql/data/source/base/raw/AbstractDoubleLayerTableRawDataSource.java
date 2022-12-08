package cc.xfl12345.mybigdata.server.mysql.data.source.base.raw;


import cc.xfl12345.mybigdata.server.common.data.source.DataSource;
import cc.xfl12345.mybigdata.server.common.data.source.GlobalDataRecordDataSource;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableMapper;
import cc.xfl12345.mybigdata.server.common.database.pojo.CommonGlobalDataRecord;
import org.teasoft.bee.osql.transaction.Transaction;
import org.teasoft.honey.osql.core.SessionFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.FutureTask;

public abstract class AbstractDoubleLayerTableRawDataSource<Value, Pojo, Condition>
    extends AbstractIndependentTableRawDataSource<Value, Pojo, Condition>
    implements DataSource<Value> {
    protected GlobalDataRecordDataSource globalDataRecordDataSource;

    public AbstractDoubleLayerTableRawDataSource(
        GlobalDataRecordDataSource globalDataRecordDataSource,
        TableMapper<Pojo, Condition> mapper) {
        super(mapper);
        this.globalDataRecordDataSource = globalDataRecordDataSource;
    }

    protected abstract Pojo getPojo(Object globalId, Value value);

    @Override
    public Object insert4IdOrGetId(Value value) {
        // 由于不是原子操作，所以理应禁止使用。
        throw new UnsupportedOperationException();
    }

    protected <T> T runTask(FutureTask<T> futureTask) {
        try {
            new Thread(futureTask).start();
            return futureTask.get();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Object insertAndReturnIdImpl(Value value);

    @Override
    public Object insertAndReturnId(Value value) {
        return runTask(new FutureTask<>(() -> insertAndReturnIdImpl(value)));
    }

    @Override
    public long insert(Value value) {
        return runTask(new FutureTask<>(() -> insertAndReturnIdImpl(value) == null ? 0 :1));
    }

    @Override
    public long insertBatch(List<Value> values) {
        return runTask(new FutureTask<>(() -> {
            long affectedRowCount = 0;
            Transaction transaction = SessionFactory.getTransaction();
            try {
                transaction.begin();
                List<CommonGlobalDataRecord> globalDataRecords = globalDataRecordDataSource
                    .getNewRegisteredDataInstances(new Date(), mapper.getPojoType(), values.size());
                affectedRowCount += globalDataRecords.size();
                List<Pojo> pojoList = new ArrayList<>(values.size());
                for (int i = 0; i < values.size(); i++) {
                    pojoList.add(getPojo(globalDataRecords.get(i).getId(), values.get(i)));
                }
                affectedRowCount += mapper.insertBatch(pojoList);
                return affectedRowCount;
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            }
        }));
    }

    @Override
    public void updateById(Value value, Object globalId) {
        Date updateTime = new Date();
        CommonGlobalDataRecord globalDataRecord = globalDataRecordDataSource.selectById(globalId);
        super.updateById(value, globalId);
        globalDataRecordDataSource.updateOneRow(globalDataRecord, updateTime);
    }
}
