package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl;

import cc.xfl12345.mybigdata.server.mysql.database.mapper.NumberContentMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.impl.AbstractAppTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.NumberContent;

public class NumberContentMapperImpl
    extends AbstractAppTableMapper<NumberContent>
    implements NumberContentMapper {
    @Override
    public Class<NumberContent> getTablePojoType() {
        return NumberContent.class;
    }
}




