package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl;

import cc.xfl12345.mybigdata.server.mysql.database.mapper.AuthAccountMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.impl.AbstractAppTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.AuthAccount;

public class AuthAccountMapperImpl
    extends AbstractAppTableMapper<AuthAccount>
    implements AuthAccountMapper {
    @Override
    public Class<AuthAccount> getTablePojoType() {
        return AuthAccount.class;
    }
}




