package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl;

import cc.xfl12345.mybigdata.server.mysql.database.mapper.ObjectContentMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.impl.AbstractAppTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.ObjectContent;

public class ObjectContentMapperImpl
    extends AbstractAppTableMapper<ObjectContent>
    implements ObjectContentMapper {
    @Override
    public Class<ObjectContent> getTablePojoType() {
        return ObjectContent.class;
    }
}




