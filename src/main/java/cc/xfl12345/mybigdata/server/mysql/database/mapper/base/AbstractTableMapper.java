package cc.xfl12345.mybigdata.server.mysql.database.mapper.base;

import cc.xfl12345.mybigdata.server.common.appconst.AppConst;
import cc.xfl12345.mybigdata.server.common.appconst.CURD;
import cc.xfl12345.mybigdata.server.common.database.error.TableOperationException;
import cc.xfl12345.mybigdata.server.mysql.database.converter.AppIdTypeConverter;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import com.fasterxml.uuid.NoArgGenerator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;
import java.util.Date;

public abstract class AbstractTableMapper implements DisposableBean {
    @Getter
    @Setter
    protected String fieldCanNotBeNullMessageTemplate = AppConst.FIELD_CAN_NOT_BE_NULL_MESSAGE_TEMPLATE;
    @Getter
    @Setter
    protected String messageAffectedRowShouldBe1 = "Affected row count should be 1.";

    @Getter
    @Setter
    protected String messageAffectedRowsCountDoesNotMatch = "Affected rows count does not match.";

    @Getter
    protected volatile NoArgGenerator uuidGenerator = null;

    public void setUuidGenerator(NoArgGenerator uuidGenerator) {
        this.uuidGenerator = uuidGenerator;
    }

    @Getter
    protected volatile CoreTableCache coreTableCache = null;

    public void setCoreTableCache(CoreTableCache coreTableCache) {
        this.coreTableCache = coreTableCache;
    }

    @Getter
    protected volatile AppIdTypeConverter idTypeConverter = null;

    public void setIdTypeConverter(AppIdTypeConverter idTypeConverter) {
        this.idTypeConverter = idTypeConverter;
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        if (uuidGenerator == null) {
            throw new IllegalArgumentException(fieldCanNotBeNullMessageTemplate.formatted("uuidGenerator"));
        }
        if (coreTableCache == null) {
            throw new IllegalArgumentException(fieldCanNotBeNullMessageTemplate.formatted("coreTableCache"));
        }
        if (idTypeConverter == null) {
            throw new IllegalArgumentException(fieldCanNotBeNullMessageTemplate.formatted("idTypeConverter"));
        }
    }

    @Override
    public void destroy() throws Exception {
    }

    public String getUuidInString() {
        return uuidGenerator.generate().toString();
    }

    public GlobalDataRecord getNewGlobalDataRecord(Date createTime, Object tableNameId) {
        GlobalDataRecord globalDataRecord = new GlobalDataRecord();
        globalDataRecord.setUuid(getUuidInString());
        globalDataRecord.setCreateTime(createTime);
        globalDataRecord.setUpdateTime(createTime);
        globalDataRecord.setModifiedCount(1L);
        idTypeConverter.injectId2Object(tableNameId, globalDataRecord::setId);
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
            messageAffectedRowShouldBe1,
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
            messageAffectedRowsCountDoesNotMatch,
            affectedRowsCount,
            operation,
            tableName
        );
    }

}
