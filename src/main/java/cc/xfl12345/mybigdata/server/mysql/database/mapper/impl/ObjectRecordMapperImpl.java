package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl;

import cc.xfl12345.mybigdata.server.mysql.database.mapper.ObjectRecordMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.impl.AbstractAppTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.ObjectRecord;

public class ObjectRecordMapperImpl
    extends AbstractAppTableMapper<ObjectRecord>
    implements ObjectRecordMapper {
    @Override
    public Class<ObjectRecord> getTablePojoType() {
        return ObjectRecord.class;
    }
}




