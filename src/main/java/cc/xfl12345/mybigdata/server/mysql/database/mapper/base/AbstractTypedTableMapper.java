package cc.xfl12345.mybigdata.server.mysql.database.mapper.base;

import cc.xfl12345.mybigdata.server.common.database.mapper.TableNoConditionMapper;

public abstract class AbstractTypedTableMapper<Pojo>
    extends AbstractTableMapper
    implements TableNoConditionMapper<Pojo> {
}
