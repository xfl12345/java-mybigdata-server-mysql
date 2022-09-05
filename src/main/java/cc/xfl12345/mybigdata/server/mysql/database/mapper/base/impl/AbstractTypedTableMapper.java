package cc.xfl12345.mybigdata.server.mysql.database.mapper.base.impl;

import cc.xfl12345.mybigdata.server.common.database.mapper.TablePojoTypeGetter;

public abstract class AbstractTypedTableMapper<TablePojoType>
    extends AbstractTableMapper implements TablePojoTypeGetter<TablePojoType> {

    public abstract Class<TablePojoType> getTablePojoType();
}
