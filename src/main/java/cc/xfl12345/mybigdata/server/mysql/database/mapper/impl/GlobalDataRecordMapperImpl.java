package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl;

import cc.xfl12345.mybigdata.server.mysql.database.mapper.GlobalDataRecordMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.impl.AbstractAppTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;

public class GlobalDataRecordMapperImpl
    extends AbstractAppTableMapper<GlobalDataRecord>
    implements GlobalDataRecordMapper {
    @Override
    public Class<GlobalDataRecord> getTablePojoType() {
        return GlobalDataRecord.class;
    }
}




