package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl;

import cc.xfl12345.mybigdata.server.mysql.database.mapper.TableSchemaRecordMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.impl.AbstractAppTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.TableSchemaRecord;

public class TableSchemaRecordMapperImpl
    extends AbstractAppTableMapper<TableSchemaRecord>
    implements TableSchemaRecordMapper {
    @Override
    public Class<TableSchemaRecord> getTablePojoType() {
        return TableSchemaRecord.class;
    }
}




