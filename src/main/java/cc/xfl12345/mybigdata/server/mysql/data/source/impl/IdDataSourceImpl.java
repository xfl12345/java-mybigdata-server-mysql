package cc.xfl12345.mybigdata.server.mysql.data.source.impl;

import cc.xfl12345.mybigdata.server.common.appconst.AppDataType;
import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.data.source.IdDataSource;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableMapper;
import cc.xfl12345.mybigdata.server.common.database.pojo.CommonGlobalDataRecord;
import cc.xfl12345.mybigdata.server.common.pojo.FieldNotNullChecker;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.AbstractIndependentTableDataSource;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.CoreTableCache;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.GlobalDataRecordBeeTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;
import lombok.Getter;
import lombok.Setter;
import org.teasoft.bee.osql.api.Condition;
import org.teasoft.bee.osql.transaction.Transaction;
import org.teasoft.honey.osql.core.SessionFactory;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class IdDataSourceImpl extends AbstractIndependentTableDataSource<MbdId, GlobalDataRecord, Condition> implements IdDataSource {
    @Getter
    @Setter
    protected FieldNotNullChecker fieldNotNullChecker = DefaultSingleton.FIELD_NOT_NULL_CHECKER;

    @Getter
    @Setter
    protected GlobalDataRecordBeeTableMapper globalDataRecordMapper;

    @Getter
    @Setter
    protected CoreTableCache coreTableCache;

    public void init() throws Exception {
        fieldNotNullChecker.check(globalDataRecordMapper, CommonGlobalDataRecord.class);
        fieldNotNullChecker.check(coreTableCache, "coreTableCache");
        super.init();
    }

    @Override
    protected TableMapper<GlobalDataRecord, Condition> getTableMapper() {
        return globalDataRecordMapper.getRawTableMapper();
    }

    @Override
    public MbdId getNewRegisteredId(Date createTime, MbdId tableNameId) {
        return globalDataRecordMapper.getNewRegisteredDataInstance(createTime, tableNameId).getId();
    }


    @Override
    public List<MbdId> getNewRegisteredIds(Date createTime, MbdId tableNameId, int batchSize) {
        return globalDataRecordMapper
            .getNewRegisteredDataInstances(createTime, tableNameId, batchSize)
            .parallelStream()
            .map(CommonGlobalDataRecord::getId)
            .toList();
    }

    @Override
    public void updateOneRow(MbdId id, Date updateTime) {
        globalDataRecordMapper.updateOneRow(id, updateTime);
    }

    @Override
    public AppDataType getDataEnumType(MbdId id) {
        MysqlMbdId tableNameId;
        Transaction transaction = SessionFactory.getTransaction();
        try {
            transaction.begin();
            CommonGlobalDataRecord globalDataRecord = globalDataRecordMapper.selectById(
                id, GlobalDataRecord.Fields.id, GlobalDataRecord.Fields.tableName
            );
            tableNameId = new MysqlMbdId(globalDataRecord.getTableName());
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }

        return coreTableCache.getPoInfo(coreTableCache.getPojoClassByTableNameId(tableNameId)).getDataType();
    }

    private final String[] fieldNames4Select = new String[]{CommonGlobalDataRecord.Fields.id};

    @Override
    protected String[] getFieldNames4Select() {
        return fieldNames4Select;
    }

    @Override
    protected MbdId getValue(GlobalDataRecord globalDataRecord) {
        return new MbdId(globalDataRecord.getId());
    }

    @Override
    protected GlobalDataRecord getPojo(MbdId MbdId) {
        GlobalDataRecord record = new GlobalDataRecord();
        record.setId(MbdId.getLongValue());
        return record;
    }

    @Override
    public LinkedHashMap<MbdId, MbdId> selectBatchId(List<MbdId> mbdIds) {
        // 有必要这么写。因为 select 操作默认加行级锁。
        return globalDataRecordMapper
            .selectBatchById(mbdIds, CommonGlobalDataRecord.Fields.id)
            .values()
            .parallelStream()
            .collect(Collectors.toMap(
                CommonGlobalDataRecord::getId,
                CommonGlobalDataRecord::getId,
                (key1, key2) -> key2,
                LinkedHashMap::new
            ));
    }
}
