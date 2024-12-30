package cc.xfl12345.mybigdata.server.mysql.data.source.base;

import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.data.source.IdDataSource;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;
import cc.xfl12345.mybigdata.server.common.pojo.AffectedRowsCountChecker;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.CoreTableCache;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.BeeTableMapper;
import lombok.Getter;
import lombok.Setter;
import org.teasoft.bee.osql.api.Condition;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractBeeDoubleLayerTableDataSource<Value, Pojo>
    extends AbstractDoubleLayerTableDataSource<Value, Pojo, Condition> {
    @Getter
    @Setter
    protected AffectedRowsCountChecker affectedRowsCountChecker = DefaultSingleton.AFFECTED_ROWS_COUNT_CHECKER;

    @Getter
    @Setter
    protected CoreTableCache coreTableCache;

    @Getter
    @Setter
    protected IdDataSource idDataSource;

    @Getter
    @Setter
    protected BeeTableMapper<Pojo> mapper;

    @Override
    public void init() throws Exception {
        fieldNotNullChecker.check(coreTableCache, "coreTableCache");
        fieldNotNullChecker.check(idDataSource, MbdId.class);
        fieldNotNullChecker.check(mapper, getPojoClass());
        super.init();
    }

    protected abstract Class<Pojo> getPojoClass();

    protected abstract String getIdFieldName();

    protected abstract MbdId getId(Pojo pojo);

    protected abstract Condition getSelectBatchIdCondition(List<Value> values);

    @Override
    public LinkedHashMap<Value, MbdId> selectBatchId(List<Value> values) {
        try {
            getTableMapper().setForUpdate(true);
            Condition condition = getSelectBatchIdCondition(values);
            // condition.selectField(getIdFieldName());
            // condition.op(getValueFieldName(), Op.in, values);
            return getTableMapper().selectByCondition(condition).parallelStream().collect(Collectors.toMap(
                this::getValue,
                this::getId,
                (key1, key2) -> key2,
                LinkedHashMap::new
            ));
        } finally {
            getTableMapper().clearForUpdateFlag();
        }
    }

    @Override
    public MbdId insertAndReturnId(Value value) {
        MbdId id = getIdDataSource().getNewRegisteredId(new Date(), getTableNameId(getTableMapper().getPojoType()));
        getTableMapper().insert(getPojo(id, value));
        return id;
    }

    @Override
    public long insert(Value value) {
        MbdId id = getIdDataSource().getNewRegisteredId(new Date(), getTableNameId(getTableMapper().getPojoType()));
        return getTableMapper().insert(getPojo(id, value));
    }
}
