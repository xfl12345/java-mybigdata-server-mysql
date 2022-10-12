package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee;

import cc.xfl12345.mybigdata.server.common.database.mapper.ConditionSweet;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableMapper;
import org.teasoft.bee.osql.Condition;

public interface BeeTableMapper<Value> extends TableMapper<Value, Condition>, ConditionSweet<Condition> {
    @Override
    default Class<Condition> getConditionType() {
        return Condition.class;
    }
}
