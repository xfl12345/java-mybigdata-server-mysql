package cc.xfl12345.mybigdata.server.mysql.database.mapper.base;

import cc.xfl12345.mybigdata.server.common.appconst.AppConst;
import cc.xfl12345.mybigdata.server.mysql.database.converter.AppIdTypeConverter;
import com.fasterxml.uuid.NoArgGenerator;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;

public class TableMapperProperties {
    @Getter
    @Setter
    protected boolean needCheckProperties = true;

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
    @Setter
    protected volatile NoArgGenerator uuidGenerator = null;

    @Getter
    @Setter
    protected volatile CoreTableCache coreTableCache = null;

    @Getter
    @Setter
    protected volatile AppIdTypeConverter idTypeConverter = null;

    @PostConstruct
    public void init() throws Exception {
        if (needCheckProperties) {
            checkProperties();
        }
    }

    public void checkProperties() throws IllegalArgumentException {
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
}
