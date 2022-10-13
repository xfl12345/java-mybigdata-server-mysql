package cc.xfl12345.mybigdata.server.mysql.database.mapper.base;

import cc.xfl12345.mybigdata.server.common.appconst.CURD;
import cc.xfl12345.mybigdata.server.common.database.error.TableOperationException;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PreDestroy;
import java.util.Date;

public abstract class AbstractTableMapper {
    @Getter
    @Setter
    protected TableMapperProperties tableMapperProperties;

    @PreDestroy
    public void destroy() throws Exception {
    }

    public String getUuidInString() {
        return tableMapperProperties.getUuidGenerator().generate().toString();
    }

    public GlobalDataRecord getNewGlobalDataRecord(Date createTime, Object tableNameId) {
        GlobalDataRecord globalDataRecord = new GlobalDataRecord();
        globalDataRecord.setUuid(getUuidInString());
        globalDataRecord.setCreateTime(createTime);
        globalDataRecord.setUpdateTime(createTime);
        globalDataRecord.setModifiedCount(1L);
        tableMapperProperties.getIdTypeConverter().injectId2Object(tableNameId, globalDataRecord::setId);
        return globalDataRecord;
    }

    public abstract GlobalDataRecord getNewRegisteredGlobalDataRecord(Date createTime, Object tableNameId);

    public TableOperationException getUpdateShouldBe1Exception(long affectedRowsCount, String tableName) {
        return getAffectedRowShouldBe1Exception(
            affectedRowsCount,
            CURD.UPDATE,
            tableName
        );
    }

    public TableOperationException getAffectedRowShouldBe1Exception(long affectedRowsCount, CURD operation, String tableName) {
        return new TableOperationException(
            tableMapperProperties.getMessageAffectedRowShouldBe1(),
            affectedRowsCount,
            operation,
            tableName
        );
    }

    public void checkAffectedRowShouldBe1(long affectedRowsCount, CURD operation, String tableName) throws TableOperationException {
        if (affectedRowsCount != 1) {
            throw getAffectedRowShouldBe1Exception(affectedRowsCount, operation, tableName);
        }
    }

    public TableOperationException getAffectedRowsCountDoesNotMatch(long affectedRowsCount, CURD operation, String tableName) {
        return new TableOperationException(
            tableMapperProperties.getMessageAffectedRowsCountDoesNotMatch(),
            affectedRowsCount,
            operation,
            tableName
        );
    }

}
