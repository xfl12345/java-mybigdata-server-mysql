package cc.xfl12345.mybigdata.server.mysql.data.source.base.raw.bee;

import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.raw.AbstractDoubleLayerTableRawDataSource;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.Op;
import org.teasoft.honey.osql.core.ConditionImpl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractBeeDoubleLayerTableRawDataSource<Value, Pojo>
    extends AbstractDoubleLayerTableRawDataSource<Value, Pojo, Condition> {

    protected abstract String getIdFieldName();

    protected abstract String getValueFieldName();

    protected abstract MbdId getId(Pojo pojo);

    @Override
    public LinkedHashMap<Value, MbdId> selectBatchId(List<Value> values) {
        try {
            getTableMapper().setForUpdate(true);
            Condition condition = new ConditionImpl();
            condition.selectField(getIdFieldName());
            condition.op(getValueFieldName(), Op.in, values);
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

    protected abstract String getPojoContentFieldName();
}
