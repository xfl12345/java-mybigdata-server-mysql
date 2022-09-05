package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl;

import cc.xfl12345.mybigdata.server.mysql.database.mapper.GroupContentMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.impl.AbstractAppTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GroupContent;

public class GroupContentMapperImpl
    extends AbstractAppTableMapper<GroupContent>
    implements GroupContentMapper {
    @Override
    public Class<GroupContent> getTablePojoType() {
        return GroupContent.class;
    }
}




