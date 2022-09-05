package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl;

import cc.xfl12345.mybigdata.server.mysql.database.mapper.StringContentMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.impl.AbstractAppTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.StringContent;

public class StringContentMapperImpl
    extends AbstractAppTableMapper<StringContent>
    implements StringContentMapper {
    @Override
    public Class<StringContent> getTablePojoType() {
        return StringContent.class;
    }
}




