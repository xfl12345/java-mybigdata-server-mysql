package cc.xfl12345.mybigdata.server.mysql.database.mapper.orm.bee;

import cc.xfl12345.mybigdata.server.common.database.mapper.ConditionSweet;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableMapper;
import org.teasoft.bee.osql.Condition;

public interface BeeTableMapper<ValueType> extends TableMapper<ValueType, Condition>, ConditionSweet<Condition> {
}
