package cc.xfl12345.mybigdata.server.mysql.api;

import cc.xfl12345.mybigdata.server.common.api.AccountMapper;
import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableBasicMapper;
import cc.xfl12345.mybigdata.server.common.database.pojo.CommonAccount;
import cc.xfl12345.mybigdata.server.common.pojo.FieldNotNullChecker;
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
    protected FieldNotNullChecker fieldNotNullChecker = DefaultSingleton.FIELD_NOT_NULL_CHECKER;

    @PostConstruct
    public void init() throws Exception {
        fieldNotNullChecker.check(authAccountMapper, AuthAccount.class);
        fieldNotNullChecker.check(idTypeConverter, "idTypeConverter");
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
        item.setAccountId(idTypeConverter.convert(account.getAccountId()));
        item.setPasswordHash(account.getPasswordHash());
        item.setPasswordSalt(account.getPasswordSalt());
        item.setExtraInfoId(idTypeConverter.convert(account.getExtraInfoId()));

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
    public List<CommonAccount> selectBatchById(List<Object> globalIdList, String... fields) {
        return authAccountMapper.selectBatchById(globalIdList, fields).parallelStream().map(this::cast).toList();
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
    public void deleteBatchById(List<Object> globalIdList) {
        authAccountMapper.deleteBatchById(globalIdList);
    }

    @Override
    public boolean isForUpdate() {
        return authAccountMapper.isForUpdate();
    }

    @Override
    public void setForUpdate(boolean forUpdate) {
        authAccountMapper.setForUpdate(forUpdate);
    }

    @Override
    public void clearForUpdateFlag() {
        authAccountMapper.clearForUpdateFlag();
    }

    @Override
    public Class<CommonAccount> getPojoType() {
        return CommonAccount.class;
    }
}
