package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee;

import cc.xfl12345.mybigdata.server.common.appconst.CURD;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.AbstractTypedTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.config.BeeTableMapperConfig;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.config.BeeTableMapperConfigGenerator;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.AuthAccount;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;
import lombok.Getter;
import lombok.Setter;
import org.teasoft.bee.osql.api.Condition;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.api.SuidRich;
import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.ConditionImpl;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BeeTableMapperImpl<Pojo>
    extends AbstractTypedTableMapper<Pojo> implements BeeTableMapper<Pojo> {
    protected ThreadLocal<Boolean> forUpdate = new ThreadLocal<>();

    protected Class<Pojo> pojoClass;

    @Getter
    @Setter
    protected BeeTableMapperConfig<Pojo> mapperConfig;

    protected String[] selectIdFieldOnly;

    protected Function<Pojo, MysqlMbdId> insertAndReturnIdImpl = (pojo) -> {
        throw new UnsupportedOperationException();
    };

    public BeeTableMapperImpl(Class<Pojo> pojoClass) {
        this.pojoClass = pojoClass;
    }

    @PostConstruct
    public void init() throws Exception {
        Class<Pojo> pojoClass = getPojoType();
        // 仅仅支持有 自增主键 的表
        if (GlobalDataRecord.class.equals(pojoClass) || AuthAccount.class.equals(pojoClass)) {
            insertAndReturnIdImpl = (pojo) -> new MysqlMbdId(getSuidRich().insertAndReturnId(pojo));
        }

        if (mapperConfig == null) {
            mapperConfig = BeeTableMapperConfigGenerator.getConfig(
                mapperProperties.getCoreTableCache().getPoInfo(getPojoType())
            );
        }

        selectIdFieldOnly = new String[]{mapperConfig.getIdFieldName()};
        super.init();
    }

    @Override
    public String getTableName() {
        return mapperConfig.getTableName();
    }

    @Override
    public Class<Pojo> getPojoType() {
        return pojoClass;
    }

    @Override
    public long insert(Pojo pojo) {
        long affectedRowCount = getSuidRich().insert(pojo);
        checkAffectedRowShouldBeOne(affectedRowCount, CURD.CREATE);
        return affectedRowCount;
    }

    @Override
    public long insertBatch(List<Pojo> pojoList) {
        long affectedRowCount = getSuidRich().insert(pojoList);
        checkAffectedRowsCountDoesNotMatch(affectedRowCount, pojoList.size(), CURD.CREATE);
        return affectedRowCount;
    }

    @Override
    public MbdId insertAndReturnId(Pojo pojo) {
        return insertAndReturnIdImpl.apply(pojo);
    }

    @Override
    public List<Pojo> selectByCondition(Condition condition) {
        intiConditionWithForUpdate(condition);
        return getSuidRich().select(mapperConfig.getNewPojoInstance(), condition);
    }

    @Override
    public Pojo selectOne(Pojo pojo, String... fields) {
        Condition condition = getConditionWithSelectedFields(fields);
        intiConditionWithForUpdate(condition);
        List<Pojo> items = getSuidRich().select(pojo, condition);
        checkAffectedRowShouldBeOne(items.size(), CURD.RETRIEVE);

        return items.get(0);
    }

    @Override
    public Pojo selectById(MbdId globalId, String... fields) {
        Condition condition = getConditionWithSelectedFields(fields);
        intiConditionWithForUpdate(condition);
        addId2Condition(condition, globalId);
        List<Pojo> items = getSuidRich().select(mapperConfig.getNewPojoInstance(), condition);
        checkAffectedRowShouldBeOne(items.size(), CURD.RETRIEVE);

        return items.get(0);
    }

    @Override
    public LinkedHashMap<MbdId, Pojo> selectBatchById(List<MbdId> globalIdList, String... fields) {
        List<Long> theDatabaseIdList = globalIdList.parallelStream().map(MysqlMbdId::getValue).toList();

        Condition condition = getConditionWithSelectedFields(fields);
        intiConditionWithForUpdate(condition);
        addIdList2Condition(theDatabaseIdList, condition);

        int arrayLength = globalIdList.size();

        // 先安排 缓存下标的异步任务
        ConcurrentHashMap<Object, Integer> idIndexMap = new ConcurrentHashMap<>();
        FutureTask<Void> futureTask = new FutureTask<>(() -> {
            IntStream.range(0, arrayLength).parallel()
                .forEach(index -> idIndexMap.put(theDatabaseIdList.get(index), index));
            return null;
        });
        futureTask.run();

        // 查询数据库
        List<Pojo> tmpResult = getSuidRich().select(mapperConfig.getNewPojoInstance(), condition);
        checkAffectedRowsCountDoesNotMatch(tmpResult.size(), globalIdList.size(), CURD.RETRIEVE);

        try {
            // 异步变同步
            futureTask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        // 排序，对号入座
        @SuppressWarnings("unchecked")
        Pojo[] resultArray = (Pojo[]) Array.newInstance(getPojoType(), arrayLength);
        tmpResult.parallelStream().forEach(item -> {
            resultArray[idIndexMap.get(mapperConfig.getId(item))] = item;
        });

        return Arrays.asList(resultArray).parallelStream().collect(Collectors.toMap(
            mapperConfig::getMbdId,
            item -> item,
            (key1, key2) -> key2,
            LinkedHashMap::new
        ));
    }

    @Override
    public MbdId selectId(Pojo pojo) {
        Pojo item = selectOne(pojo, selectIdFieldOnly);
        return mapperConfig.getMbdId(item);
    }

    @Override
    public long updateByCondition(Pojo pojo, Condition condition) {
        return getSuidRich().update(pojo, condition);
    }

    @Override
    public void updateById(Pojo pojo, MbdId globalId) {
        mapperConfig.setMbdId(pojo, globalId);
        long affectedRowCount = getSuidRich().updateBy(pojo, mapperConfig.getIdFieldName());
        checkAffectedRowShouldBeOne(affectedRowCount, CURD.UPDATE);
    }

    @Override
    public long deleteByCondition(Condition condition) {
        return getSuidRich().delete(mapperConfig.getNewPojoInstance(), condition);
    }

    @Override
    public void deleteById(MbdId globalId) {
        long affectedRowCount = getSuidRich().delete(mapperConfig.getNewPojoInstance(), getConditionWithId(globalId));
        checkAffectedRowShouldBeOne(affectedRowCount, CURD.DELETE);
    }

    @Override
    public void deleteBatchById(List<MbdId> globalIdList) {
        Condition condition = new ConditionImpl();
        addIdList2Condition(globalIdList.parallelStream().map(MysqlMbdId::getValue).toList(), condition);
        long affectedRowCount = getSuidRich().delete(mapperConfig.getNewPojoInstance(), condition);
        checkAffectedRowsCountDoesNotMatch(affectedRowCount, globalIdList.size(), CURD.DELETE);
    }

    @Override
    public boolean isForUpdate() {
        return Boolean.TRUE.equals(this.forUpdate.get());
    }

    @Override
    public void setForUpdate(boolean forUpdate) {
        this.forUpdate.set(forUpdate);
    }

    @Override
    public void clearForUpdateFlag() {
        this.forUpdate.remove();
    }

    protected void intiConditionWithForUpdate(Condition condition) {
        if (isForUpdate()) {
            condition.forUpdate();
        }
    }

    protected SuidRich getSuidRich() {
        return BeeFactory.getHoneyFactory().getSuidRich();
    }

    protected void addIdList2Condition(List<Long> idList, Condition condition) {
        condition.op(
            mapperConfig.getIdFieldName(),
            Op.in,
            idList
            // idList.parallelStream().map(MysqlMbdId::getValue).toList()
        );
    }

    @Override
    public Condition getConditionWithSelectedFields(String... fields) {
        Condition condition = new ConditionImpl();
        addFields2Condition(condition, fields);
        return condition;
    }

    @Override
    public void addFields2Condition(Condition condition, String... fields) {
        if (fields != null && fields.length > 0) {
            condition.selectField(fields);
        }
    }

    @Override
    public Condition getConditionWithId(MbdId id) {
        Condition condition = new ConditionImpl();
        addId2Condition(condition, id);
        return condition;
    }

    @Override
    public void addId2Condition(Condition condition, MbdId id) {
        condition.op(mapperConfig.getIdFieldName(), Op.eq, id.getValue());
    }
}
