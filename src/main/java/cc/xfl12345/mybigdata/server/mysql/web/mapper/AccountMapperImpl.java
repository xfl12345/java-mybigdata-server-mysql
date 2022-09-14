package cc.xfl12345.mybigdata.server.mysql.web.mapper;

import cc.xfl12345.mybigdata.server.common.appconst.AppConst;
import cc.xfl12345.mybigdata.server.common.web.mapper.AccountMapper;
import cc.xfl12345.mybigdata.server.common.web.pojo.Account;
import cc.xfl12345.mybigdata.server.mysql.database.converter.AppIdTypeConverter;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.AuthAccountMapper;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.AuthAccount;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

public class AccountMapperImpl implements AccountMapper, InitializingBean {
    @Getter
    @Setter
    protected AuthAccountMapper authAccountMapper;

    @Getter
    @Setter
    protected volatile AppIdTypeConverter idTypeConverter = null;

    @Getter
    @Setter
    protected String fieldCanNotBeNullMessageTemplate = AppConst.FIELD_CAN_NOT_BE_NULL_MESSAGE_TEMPLATE;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (authAccountMapper == null) {
            throw new IllegalArgumentException(fieldCanNotBeNullMessageTemplate.formatted("authAccountMapper"));
        }
        if (idTypeConverter == null) {
            throw new IllegalArgumentException(fieldCanNotBeNullMessageTemplate.formatted("idTypeConverter"));
        }
    }

    public Account cast(AuthAccount account) {
        Account item = new Account();
        item.setAccountId(account.getAccountId());
        item.setPasswordHash(account.getPasswordHash());
        item.setPasswordSalt(account.getPasswordSalt());
        item.setExtraInfoId(account.getExtraInfoId());

        return item;
    }

    public AuthAccount cast(Account account) {
        AuthAccount item = new AuthAccount();
        idTypeConverter.injectId2Object(account.getAccountId(), item::setAccountId);
        item.setPasswordHash(account.getPasswordHash());
        item.setPasswordSalt(account.getPasswordSalt());
        idTypeConverter.injectId2Object(account.getExtraInfoId(), item::setExtraInfoId);

        return item;
    }

    @Override
    public long insert(Account account) throws Exception {
        return authAccountMapper.insert(cast(account));
    }

    @Override
    public long insertBatch(List<Account> list) throws Exception {
        return authAccountMapper.insertBatch(list.parallelStream().map(this::cast).toList());
    }

    @Override
    public Object insertAndReturnId(Account account) throws Exception {
        return authAccountMapper.insertAndReturnId(cast(account));
    }

    @Override
    public Account selectOne(Account account, String[] strings) throws Exception {
        return cast(authAccountMapper.selectOne(cast(account), null));
    }

    @Override
    public Account selectById(Object globalId, String[] strings) throws Exception {
        return cast(authAccountMapper.selectById(globalId, null));
    }

    @Override
    public Object selectId(Account account) throws Exception {
        return authAccountMapper.selectId(cast(account));
    }

    @Override
    public void updateById(Account account, Object globalId) throws Exception {
        authAccountMapper.updateById(cast(account), globalId);
    }

    @Override
    public void deleteById(Object globalId) throws Exception {
        authAccountMapper.deleteById(globalId);
    }
}
