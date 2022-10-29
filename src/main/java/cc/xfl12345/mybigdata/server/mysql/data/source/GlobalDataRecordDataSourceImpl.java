package cc.xfl12345.mybigdata.server.mysql.data.source;

import cc.xfl12345.mybigdata.server.common.data.source.GlobalDataRecordDataSource;
import cc.xfl12345.mybigdata.server.common.data.source.impl.AbstractSingleTableDataSource;
import cc.xfl12345.mybigdata.server.common.database.pojo.CommonGlobalDataRecord;
import cc.xfl12345.mybigdata.server.mysql.database.converter.AppIdTypeConverter;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import lombok.Getter;
import lombok.Setter;

public class GlobalDataRecordDataSourceImpl
    extends AbstractSingleTableDataSource<CommonGlobalDataRecord, GlobalDataRecord>
    implements GlobalDataRecordDataSource {
    @Getter
    @Setter
    protected AppIdTypeConverter idTypeConverter;

    @Override
    public void init() throws Exception {
        super.init();
        if (idTypeConverter == null) {
            throw new IllegalArgumentException(fieldCanNotBeNullMessageTemplate.formatted("idTypeConverter"));
        }
    }

    protected String[] selectContentFieldOnly = new String[]{
        GlobalDataRecord.Fields.id,
        GlobalDataRecord.Fields.uuid,
        GlobalDataRecord.Fields.createTime,
        GlobalDataRecord.Fields.updateTime,
        GlobalDataRecord.Fields.modifiedCount,
        GlobalDataRecord.Fields.tableName,
        GlobalDataRecord.Fields.description
    };

    @Override
    protected String[] getSelectContentFieldOnly() {
        return selectContentFieldOnly;
    }

    @Override
    protected CommonGlobalDataRecord getValue(GlobalDataRecord globalDataRecord) {
        CommonGlobalDataRecord record = new CommonGlobalDataRecord();
        record.setId(globalDataRecord.getId());
        record.setUuid(globalDataRecord.getUuid());
        record.setCreateTime(globalDataRecord.getCreateTime());
        record.setUpdateTime(globalDataRecord.getUpdateTime());
        record.setModifiedCount(globalDataRecord.getModifiedCount());
        record.setTableName(globalDataRecord.getTableName());
        record.setDescription(globalDataRecord.getDescription());

        return record;
    }

    @Override
    protected GlobalDataRecord getPojo(CommonGlobalDataRecord commonGlobalDataRecord) {
        GlobalDataRecord record = new GlobalDataRecord();
        idTypeConverter.injectId2Object(commonGlobalDataRecord.getId(), record::setId);
        record.setUuid(commonGlobalDataRecord.getUuid());
        record.setCreateTime(commonGlobalDataRecord.getCreateTime());
        record.setUpdateTime(commonGlobalDataRecord.getUpdateTime());
        record.setModifiedCount(commonGlobalDataRecord.getModifiedCount());
        idTypeConverter.injectId2Object(commonGlobalDataRecord.getTableName(), record::setTableName);
        idTypeConverter.injectId2Object(commonGlobalDataRecord.getDescription(), record::setDescription);

        return record;
    }
}
