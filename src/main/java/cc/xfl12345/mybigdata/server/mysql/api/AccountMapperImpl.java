package cc.xfl12345.mybigdata.server.mysql.api;

import cc.xfl12345.mybigdata.server.common.api.AccountMapper;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableBasicMapper;
import cc.xfl12345.mybigdata.server.common.database.pojo.CommonAccount;
import cc.xfl12345.mybigdata.server.common.database.pojo.SingleTableBasicMapperWarpper;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.AuthAccount;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;
import lombok.Getter;
import lombok.Setter;

public class AccountMapperImpl extends SingleTableBasicMapperWarpper<AuthAccount, CommonAccount> implements AccountMapper {
    @Getter
    @Setter
    protected TableBasicMapper<AuthAccount> tableBasicMapper;

    public CommonAccount cast2CommonPojo(AuthAccount account) {
        CommonAccount item = new CommonAccount();
        item.setAccountId(new MbdId(account.getAccountId()));
        item.setPasswordHash(account.getPasswordHash());
        item.setPasswordSalt(account.getPasswordSalt());
        item.setExtraInfoId(new MbdId(account.getExtraInfoId()));

        return item;
    }

    public AuthAccount cast2Pojo(CommonAccount account) {
        AuthAccount item = new AuthAccount();
        item.setAccountId(MysqlMbdId.getValue(account.getAccountId()));
        item.setPasswordHash(account.getPasswordHash());
        item.setPasswordSalt(account.getPasswordSalt());
        item.setExtraInfoId(MysqlMbdId.getValue(account.getExtraInfoId()));

        return item;
    }

    @Override
    protected Class<AuthAccount> getDatabasePojoClass() {
        return AuthAccount.class;
    }

    @Override
    public Class<CommonAccount> getPojoType() {
        return CommonAccount.class;
    }
}
