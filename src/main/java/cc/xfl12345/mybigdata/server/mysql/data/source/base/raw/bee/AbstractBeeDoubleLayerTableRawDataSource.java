package cc.xfl12345.mybigdata.server.mysql.data.source.base.raw.bee;


import cc.xfl12345.mybigdata.server.common.data.source.GlobalDataRecordDataSource;
import cc.xfl12345.mybigdata.server.common.database.pojo.CommonGlobalDataRecord;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.raw.AbstractDoubleLayerTableRawDataSource;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.BeeTableMapper;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.transaction.Transaction;
import org.teasoft.honey.osql.core.SessionFactory;

import java.util.Date;

public abstract class AbstractBeeDoubleLayerTableRawDataSource<Value, Pojo>
    extends AbstractDoubleLayerTableRawDataSource<Value, Pojo, Condition> {
    public AbstractBeeDoubleLayerTableRawDataSource(GlobalDataRecordDataSource globalDataRecordDataSource, BeeTableMapper<Pojo> mapper) {
        super(globalDataRecordDataSource, mapper);
    }

    @Override
    protected Object insertAndReturnIdImpl(Value value) {
        Transaction transaction = SessionFactory.getTransaction();
        try {
            transaction.begin();
            CommonGlobalDataRecord globalDataRecord = globalDataRecordDataSource
                .getNewRegisteredDataInstance(new Date(), mapper.getPojoType());
            Object id = globalDataRecord.getId();
            mapper.insert(getPojo(id, value));
            transaction.commit();
            return id;
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }
    }

    protected abstract String getPojoContentFieldName();
}
