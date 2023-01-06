package cc.xfl12345.mybigdata.server.mysql.api;

import cc.xfl12345.mybigdata.server.common.appconst.CURD;
import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableBasicMapper;
import cc.xfl12345.mybigdata.server.common.database.pojo.CommonGlobalDataRecord;
import cc.xfl12345.mybigdata.server.common.pojo.AffectedRowsCountChecker;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;
import cc.xfl12345.mybigdata.server.mysql.appconst.CoreTableNames;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.CoreTableCache;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.BeeTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.GlobalDataRecordBeeTableMapper;
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

public class GlobalDataRecordMapperImpl extends GlobalDataRecordBeeTableMapper {
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
    protected TableBasicMapper<GlobalDataRecord> getTableBasicMapper() {
        return tableMapper;
    }

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
    protected CommonGlobalDataRecord cast2CommonPojo(GlobalDataRecord globalDataRecord) {
        CommonGlobalDataRecord record = new CommonGlobalDataRecord();
        record.setId(new MbdId(globalDataRecord.getId()));
        record.setUuid(globalDataRecord.getUuid());
        record.setCreateTime(globalDataRecord.getCreateTime());
        record.setUpdateTime(globalDataRecord.getUpdateTime());
        record.setModifiedCount(globalDataRecord.getModifiedCount());
        record.setTableName(new MbdId(globalDataRecord.getTableName()));
        record.setDescription(new MbdId(globalDataRecord.getDescription()));

        return record;
    }

    @Override
    protected GlobalDataRecord cast2Pojo(CommonGlobalDataRecord commonGlobalDataRecord) {
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

    @Override
    protected Class<GlobalDataRecord> getDatabasePojoClass() {
        return GlobalDataRecord.class;
    }


    public String getUuidInString() {
        return uuidGenerator.generate().toString();
    }

    @Override
    public CommonGlobalDataRecord getNewDataInstance(Date createTime, MbdId tableNameId) {
        CommonGlobalDataRecord globalDataRecord = new CommonGlobalDataRecord();
        globalDataRecord.setUuid(getUuidInString());
        globalDataRecord.setCreateTime(createTime);
        globalDataRecord.setUpdateTime(createTime);
        globalDataRecord.setModifiedCount(1L);
        globalDataRecord.setTableName(new MbdId(tableNameId));
        return globalDataRecord;
    }

    @Override
    public CommonGlobalDataRecord getNewRegisteredDataInstance(Date createTime, MbdId tableNameId) {
        CommonGlobalDataRecord globalDataRecord = getNewDataInstance(createTime, tableNameId);
        MbdId id = insertAndReturnId(globalDataRecord);
        globalDataRecord.setId(new MbdId(id));
        return globalDataRecord;
    }

    @Override
    public List<CommonGlobalDataRecord> getNewDataInstances(Date createTime, MbdId tableNameId, int batchSize) {
        List<CommonGlobalDataRecord> globalDataRecords = new ArrayList<>(batchSize);
        for (int i = 0; i < batchSize; i++) {
            globalDataRecords.add(getNewDataInstance(createTime, tableNameId));
        }

        return globalDataRecords;
    }

    @Override
    public List<CommonGlobalDataRecord> getNewRegisteredDataInstances(Date createTime, MbdId tableNameId, int batchSize) {
        List<CommonGlobalDataRecord> globalDataRecords = getNewDataInstances(createTime, tableNameId, batchSize);

        insertBatch(globalDataRecords);

        return getRecordsByUUID(
            globalDataRecords.parallelStream().map(CommonGlobalDataRecord::getUuid).toList()
        );
    }

    @Override
    public List<CommonGlobalDataRecord> getRecordsByUUID(List<String> uuids) {
        List<GlobalDataRecord> records = getTableMapper().selectByCondition(
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

        return records.parallelStream().map(this::cast2CommonPojo).toList();
    }

    @Override
    public void updateOneRow(MbdId id, Date updateTime) {
        ConditionImpl condition = new ConditionImpl();
        condition.setAdd(GlobalDataRecord.Fields.modifiedCount, 1);
        condition.set(GlobalDataRecord.Fields.updateTime, dateFormat.format(updateTime));
        long affectedRowCount = getTableMapper().updateByCondition(new GlobalDataRecord(), condition);
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
        getTableMapper().updateById(cast2Pojo(record), record.getId());
    }

    @Override
    public Class<CommonGlobalDataRecord> getPojoType() {
        return CommonGlobalDataRecord.class;
    }
}
