package cc.xfl12345.mybigdata.server.mysql.database.mapper.base;

import cc.xfl12345.mybigdata.server.common.appconst.AppConst;
import cc.xfl12345.mybigdata.server.mysql.database.converter.AppIdTypeConverter;
import com.fasterxml.uuid.NoArgGenerator;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import java.util.function.Supplier;


@lombok.NoArgsConstructor
@lombok.experimental.FieldNameConstants
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

    public void checkProperties() throws Exception {
        checkProperty(this::getUuidGenerator, Fields.uuidGenerator);
        checkProperty(this::getCoreTableCache, Fields.coreTableCache);
        checkProperty(this::getIdTypeConverter, Fields.idTypeConverter);
    }

    public void checkProperty(Supplier<?> supplier, String fieldName) throws Exception {
        checkProperty(supplier, fieldName, fieldCanNotBeNullMessageTemplate);
    }

    public static void checkProperty(Supplier<?> supplier, String fieldName, String messageTemplate) throws Exception {
        if(supplier.get() == null) {
            throw new IllegalArgumentException(messageTemplate.formatted(fieldName));
        }
    }
}
