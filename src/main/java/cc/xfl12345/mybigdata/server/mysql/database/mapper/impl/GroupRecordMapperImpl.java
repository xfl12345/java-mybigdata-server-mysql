package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl;

import cc.xfl12345.mybigdata.server.mysql.database.mapper.GroupRecordMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.impl.AbstractAppTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GroupRecord;

public class GroupRecordMapperImpl
    extends AbstractAppTableMapper<GroupRecord>
    implements GroupRecordMapper {
    @Override
    public Class<GroupRecord> getTablePojoType() {
        return GroupRecord.class;
    }
}




