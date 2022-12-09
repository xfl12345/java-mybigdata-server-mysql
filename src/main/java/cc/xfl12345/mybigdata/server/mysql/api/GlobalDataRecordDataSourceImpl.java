package cc.xfl12345.mybigdata.server.mysql.api;

import cc.xfl12345.mybigdata.server.common.appconst.CURD;
import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.data.source.DataSource;
import cc.xfl12345.mybigdata.server.common.data.source.GlobalDataRecordDataSource;
import cc.xfl12345.mybigdata.server.common.data.source.impl.AbstractDataSource;
import cc.xfl12345.mybigdata.server.common.database.pojo.CommonGlobalDataRecord;
import cc.xfl12345.mybigdata.server.common.pojo.AffectedRowsCountChecker;
import cc.xfl12345.mybigdata.server.common.pojo.MbdId;
import cc.xfl12345.mybigdata.server.mysql.appconst.CoreTableNames;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.raw.AbstractIndependentTableRawDataSource;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.CoreTableCache;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.BeeTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;
import com.fasterxml.uuid.NoArgGenerator;
import lombok.Getter;
import lombok.Setter;
import org.teasoft.bee.osql.Op;
import org.teasoft.honey.osql.core.ConditionImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GlobalDataRecordDataSourceImpl
    extends AbstractDataSource<CommonGlobalDataRecord>
    implements GlobalDataRecordDataSource {
    @Getter
    @Setter
    protected AffectedRowsCountChecker affectedRowsCountChecker = DefaultSingleton.AFFECTED_ROWS_COUNT_CHECKER;

    @Getter
    @Setter
    protected NoArgGenerator uuidGenerator = null;

    @Getter
    @Setter
    protected CoreTableCache coreTableCache = null;

    @Getter
    @Setter
    protected BeeTableMapper<GlobalDataRecord> tableMapper = null;

    @Getter
    @Setter
    protected SimpleDateFormat dateFormat = null;

    @Override
    public void init() throws Exception {
        fieldNotNullChecker.check(tableMapper, GlobalDataRecord.class);
        fieldNotNullChecker.check(coreTableCache, "coreTableCache");
        fieldNotNullChecker.check(uuidGenerator, "uuidGenerator");
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        }
        super.init();
    }

    @Override
    protected DataSource<CommonGlobalDataRecord> generateRawImpl() {
        GlobalDataRecordDataSourceImpl myself = this;
        return new AbstractIndependentTableRawDataSource<>(tableMapper) {
            // @Override
            // protected Condition generateUniqueMatchCondition(CommonGlobalDataRecord theOld, CURD operation) {
            //     Date date = new Date();
            //     Condition condition = new ConditionImpl();
            //     if (theOld.getId() != null) {
            //         condition.op(GlobalDataRecord.Fields.id, Op.eq, theOld.getId());
            //     } else if (theOld.getUuid() != null) {
            //         condition.op(GlobalDataRecord.Fields.uuid, Op.eq, theOld.getUuid());
            //     } else {
            //         throw new TableOperationException(
            //             "Unable to locate unique row for update.",
            //             0,
            //             1,
            //             operation,
            //             CoreTableNames.GLOBAL_DATA_RECORD
            //         );
            //     }
            //
            //     return condition;
            // }

            @Override
            protected String[] getSelectContentFieldOnly() {
                return new String[0];
            }

            @Override
            protected CommonGlobalDataRecord getValue(GlobalDataRecord globalDataRecord) {
                return myself.getValue(globalDataRecord);
            }

            @Override
            protected GlobalDataRecord getPojo(CommonGlobalDataRecord commonGlobalDataRecord) {
                return myself.getPojo(commonGlobalDataRecord);
            }

            @Override
            protected String getTableName() {
                return CoreTableNames.GLOBAL_DATA_RECORD;
            }
        };
    }


    @Override
    public Class<CommonGlobalDataRecord> getValueType() {
        return CommonGlobalDataRecord.class;
    }

    protected GlobalDataRecord getPojo(CommonGlobalDataRecord commonGlobalDataRecord) {
        GlobalDataRecord record = new GlobalDataRecord();
        record.setId(MysqlMbdId.getValue(commonGlobalDataRecord.getId()));
        record.setUuid(commonGlobalDataRecord.getUuid());
        record.setCreateTime(commonGlobalDataRecord.getCreateTime());
        record.setUpdateTime(commonGlobalDataRecord.getUpdateTime());
        record.setModifiedCount(commonGlobalDataRecord.getModifiedCount());
        record.setTableName(MysqlMbdId.getValue(commonGlobalDataRecord.getTableName()));
        record.setDescription(MysqlMbdId.getValue(commonGlobalDataRecord.getDescription()));

        return record;
    }

    protected CommonGlobalDataRecord getValue(GlobalDataRecord globalDataRecord) {
        CommonGlobalDataRecord record = new CommonGlobalDataRecord();
        record.setId(new MysqlMbdId(globalDataRecord.getId()));
        record.setUuid(globalDataRecord.getUuid());
        record.setCreateTime(globalDataRecord.getCreateTime());
        record.setUpdateTime(globalDataRecord.getUpdateTime());
        record.setModifiedCount(globalDataRecord.getModifiedCount());
        record.setTableName(new MysqlMbdId(globalDataRecord.getTableName()));
        record.setDescription(new MysqlMbdId(globalDataRecord.getDescription()));

        return record;
    }

    @Override
    public List<CommonGlobalDataRecord> getNewDataInstances(Date createTime, Class<?> pojoClass, int batchSize) {
        List<CommonGlobalDataRecord> globalDataRecords = new ArrayList<>(batchSize);
        for (int i = 0; i < batchSize; i++) {
            globalDataRecords.add(getNewDataInstance(createTime, pojoClass));
        }

        return globalDataRecords;
    }

    @Override
    public List<CommonGlobalDataRecord> getNewRegisteredDataInstances(Date createTime, Class<?> pojoClass, int batchSize) {
        List<CommonGlobalDataRecord> globalDataRecords = getNewDataInstances(createTime, pojoClass, batchSize);

        insertBatch(globalDataRecords);

        return getRecordsByUUID(
            globalDataRecords.parallelStream().map(CommonGlobalDataRecord::getUuid).toList()
        );
    }


    public String getUuidInString() {
        return uuidGenerator.generate().toString();
    }

    @Override
    public CommonGlobalDataRecord getNewDataInstance(Date createTime, MbdId<?> tableNameId) {
        CommonGlobalDataRecord globalDataRecord = new CommonGlobalDataRecord();
        globalDataRecord.setUuid(getUuidInString());
        globalDataRecord.setCreateTime(createTime);
        globalDataRecord.setUpdateTime(createTime);
        globalDataRecord.setModifiedCount(1L);
        globalDataRecord.setTableName(tableNameId);
        return globalDataRecord;
    }

    @Override
    public CommonGlobalDataRecord getNewRegisteredDataInstance(Date createTime, MbdId<?> tableNameId) {
        CommonGlobalDataRecord globalDataRecord = getNewDataInstance(createTime, tableNameId);
        MbdId<?> id = insertAndReturnId(globalDataRecord);
        globalDataRecord.setId(id);
        return globalDataRecord;
    }

    @Override
    public CommonGlobalDataRecord getNewDataInstance(Date createTime, Class<?> pojoClass) {
        return getNewDataInstance(createTime, coreTableCache.getTableNameId(pojoClass));
    }

    @Override
    public CommonGlobalDataRecord getNewRegisteredDataInstance(Date createTime, Class<?> pojoClass) {
        return getNewRegisteredDataInstance(createTime, coreTableCache.getTableNameId(pojoClass));
    }

    @Override
    public List<CommonGlobalDataRecord> getRecordsByUUID(List<String> uuids) {
        List<GlobalDataRecord> records = tableMapper.selectByCondition(
            new ConditionImpl().op(GlobalDataRecord.Fields.uuid, Op.in, uuids)
        );

        affectedRowsCountChecker.checkAffectedRowsCountDoesNotMatch(
            records.size(),
            uuids.size(),
            CURD.RETRIEVE,
            CoreTableNames.GLOBAL_DATA_RECORD
        );

        // checkAffectedRowsCountDoesNotMatch(
        //     records.size(),
        //     uuids.size(),
        //     CURD.RETRIEVE
        // );

        return records.parallelStream().map(this::getValue).toList();
    }

    @Override
    public void updateOneRow(MbdId<?> id, Date updateTime) {
        ConditionImpl condition = new ConditionImpl();
        condition.setAdd(GlobalDataRecord.Fields.modifiedCount, 1);
        condition.set(GlobalDataRecord.Fields.updateTime, dateFormat.format(updateTime));
        long affectedRowCount = tableMapper.updateByCondition(new GlobalDataRecord(), condition);
        affectedRowsCountChecker.checkAffectedRowShouldBeOne(
            affectedRowCount,
            CURD.UPDATE,
            CoreTableNames.GLOBAL_DATA_RECORD
        );
        // checkAffectedRowShouldBeOne(affectedRowCount, CURD.UPDATE);
    }

    @Override
    public void updateOneRow(CommonGlobalDataRecord record, Date updateTime) {
        record.setUpdateTime(updateTime);
        record.setModifiedCount(record.getModifiedCount() + 1);
        tableMapper.updateById(getPojo(record), record.getId());
    }
}
