package cc.xfl12345.mybigdata.server.mysql.api;

import cc.xfl12345.mybigdata.server.common.api.AccountMapper;
import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableBasicMapper;
import cc.xfl12345.mybigdata.server.common.database.pojo.CommonAccount;
import cc.xfl12345.mybigdata.server.common.pojo.FieldNotNullChecker;
import cc.xfl12345.mybigdata.server.common.pojo.MbdId;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.AuthAccount;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;
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
    protected FieldNotNullChecker fieldNotNullChecker = DefaultSingleton.FIELD_NOT_NULL_CHECKER;

    @PostConstruct
    public void init() throws Exception {
        fieldNotNullChecker.check(authAccountMapper, AuthAccount.class);
    }

    public CommonAccount cast(AuthAccount account) {
        CommonAccount item = new CommonAccount();
        item.setAccountId(new MysqlMbdId(account.getAccountId()));
        item.setPasswordHash(account.getPasswordHash());
        item.setPasswordSalt(account.getPasswordSalt());
        item.setExtraInfoId(new MysqlMbdId(account.getExtraInfoId()));

        return item;
    }

    public AuthAccount cast(CommonAccount account) {
        AuthAccount item = new AuthAccount();
        item.setAccountId(MysqlMbdId.getValue(account.getAccountId()));
        item.setPasswordHash(account.getPasswordHash());
        item.setPasswordSalt(account.getPasswordSalt());
        item.setExtraInfoId(MysqlMbdId.getValue(account.getExtraInfoId()));

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
    public MbdId<?> insertAndReturnId(CommonAccount account) {
        return authAccountMapper.insertAndReturnId(cast(account));
    }

    @Override
    public CommonAccount selectOne(CommonAccount account, String... fields) {
        return cast(authAccountMapper.selectOne(cast(account), fields));
    }

    @Override
    public CommonAccount selectById(MbdId<?> globalId, String[] fields) {
        return cast(authAccountMapper.selectById(globalId, fields));
    }

    @Override
    public List<CommonAccount> selectBatchById(List<MbdId<?>> globalIdList, String... fields) {
        return authAccountMapper.selectBatchById(globalIdList, fields).parallelStream().map(this::cast).toList();
    }

    @Override
    public MbdId<?> selectId(CommonAccount account) {
        return authAccountMapper.selectId(cast(account));
    }

    @Override
    public void updateById(CommonAccount account, MbdId<?> globalId) {
        authAccountMapper.updateById(cast(account), globalId);
    }

    @Override
    public void deleteById(MbdId<?> globalId) {
        authAccountMapper.deleteById(globalId);
    }

    @Override
    public void deleteBatchById(List<MbdId<?>> globalIdList) {
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
