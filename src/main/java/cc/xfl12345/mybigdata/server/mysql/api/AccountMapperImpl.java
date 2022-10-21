package cc.xfl12345.mybigdata.server.mysql.api;

import cc.xfl12345.mybigdata.server.common.api.AccountMapper;
import cc.xfl12345.mybigdata.server.common.appconst.AppConst;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableBasicMapper;
import cc.xfl12345.mybigdata.server.common.database.pojo.CommonAccount;
import cc.xfl12345.mybigdata.server.mysql.database.converter.AppIdTypeConverter;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.AuthAccount;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import java.util.List;

public class AccountMapperImpl implements AccountMapper {
    @Getter
    @Setter
    protected TableBasicMapper<AuthAccount> authAccountMapper;

    @Getter
    @Setter
    protected volatile AppIdTypeConverter idTypeConverter = null;

    @Getter
    @Setter
    protected String fieldCanNotBeNullMessageTemplate = AppConst.FIELD_CAN_NOT_BE_NULL_MESSAGE_TEMPLATE;

    @PostConstruct
    public void init() throws Exception {
        if (authAccountMapper == null) {
            throw new IllegalArgumentException(fieldCanNotBeNullMessageTemplate.formatted("authAccountMapper"));
        }
        if (idTypeConverter == null) {
            throw new IllegalArgumentException(fieldCanNotBeNullMessageTemplate.formatted("idTypeConverter"));
        }
    }

    public CommonAccount cast(AuthAccount account) {
        CommonAccount item = new CommonAccount();
        item.setAccountId(account.getAccountId());
        item.setPasswordHash(account.getPasswordHash());
        item.setPasswordSalt(account.getPasswordSalt());
        item.setExtraInfoId(account.getExtraInfoId());

        return item;
    }

    public AuthAccount cast(CommonAccount account) {
        AuthAccount item = new AuthAccount();
        idTypeConverter.injectId2Object(account.getAccountId(), item::setAccountId);
        item.setPasswordHash(account.getPasswordHash());
        item.setPasswordSalt(account.getPasswordSalt());
        idTypeConverter.injectId2Object(account.getExtraInfoId(), item::setExtraInfoId);

        return item;
    }

    @Override
    public long insert(CommonAccount account) {
        return authAccountMapper.insert(cast(account));
    }

    @Override
    public long insertBatch(List<CommonAccount> accounts) {
        return authAccountMapper.insertBatch(accounts.parallelStream().map(this::cast).toList());
    }

    @Override
    public Object insertAndReturnId(CommonAccount account) {
        return authAccountMapper.insertAndReturnId(cast(account));
    }

    @Override
    public CommonAccount selectOne(CommonAccount account, String... fields) {
        return cast(authAccountMapper.selectOne(cast(account), fields));
    }

    @Override
    public CommonAccount selectById(Object globalId, String[] fields) {
        return cast(authAccountMapper.selectById(globalId, fields));
    }

    @Override
    public Object selectId(CommonAccount account) {
        return authAccountMapper.selectId(cast(account));
    }

    @Override
    public void updateById(CommonAccount account, Object globalId) {
        authAccountMapper.updateById(cast(account), globalId);
    }

    @Override
    public void deleteById(Object globalId) {
        authAccountMapper.deleteById(globalId);
    }

    @Override
    public Class<CommonAccount> getPojoType() {
        return CommonAccount.class;
    }
}
