package cc.xfl12345.mybigdata.server.mysql.database.mapper.base;

import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.database.error.SqlErrorAnalyst;
import cc.xfl12345.mybigdata.server.common.pojo.AffectedRowsCountChecker;
import cc.xfl12345.mybigdata.server.common.pojo.FieldNotNullChecker;
import cc.xfl12345.mybigdata.server.mysql.database.converter.AppIdTypeConverter;
import com.fasterxml.uuid.NoArgGenerator;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;

@lombok.NoArgsConstructor
@lombok.experimental.FieldNameConstants
public class MapperProperties {
    @Getter
    @Setter
    protected boolean needCheckProperties = true;

    @Getter
    @Setter
    protected FieldNotNullChecker fieldNotNullChecker = DefaultSingleton.FIELD_NOT_NULL_CHECKER;

    @Getter
    @Setter
    protected AffectedRowsCountChecker affectedRowsCountChecker = DefaultSingleton.AFFECTED_ROWS_COUNT_CHECKER;

    @Getter
    @Setter
    protected volatile NoArgGenerator uuidGenerator = null;

    @Getter
    @Setter
    protected volatile CoreTableCache coreTableCache = null;

    @Getter
    @Setter
    protected volatile AppIdTypeConverter idTypeConverter = null;

    @Getter
    @Setter
    protected SqlErrorAnalyst sqlErrorAnalyst;

    @PostConstruct
    public void init() throws Exception {
        if (needCheckProperties) {
            checkProperties();
        }
    }

    public void checkProperties() throws Exception {
        fieldNotNullChecker.check(getFieldNotNullChecker(), Fields.fieldNotNullChecker);
        fieldNotNullChecker.check(getAffectedRowsCountChecker(), Fields.affectedRowsCountChecker);
        fieldNotNullChecker.check(getUuidGenerator(), Fields.uuidGenerator);
        fieldNotNullChecker.check(getCoreTableCache(), Fields.coreTableCache);
        fieldNotNullChecker.check(getIdTypeConverter(), Fields.idTypeConverter);
        fieldNotNullChecker.check(getSqlErrorAnalyst(), Fields.sqlErrorAnalyst);
    }
}
