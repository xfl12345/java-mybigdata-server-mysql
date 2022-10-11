package cc.xfl12345.mybigdata.server.mysql.database.mapper.orm.bee.config;

import lombok.Getter;
import lombok.Setter;

import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleBeeTableMapperConfig<TablePojoType> implements BeeTableMapperConfig<TablePojoType> {
    @Getter
    @Setter
    protected String tableName;

    @Getter
    @Setter
    protected String idFieldName;

    @Setter
    protected Function<TablePojoType, Object> idGetter = (value) -> null;

    @Setter
    protected Supplier<TablePojoType> pojoInstanceSupplier = () -> null;

    public Object getId(TablePojoType value) {
        return idGetter.apply(value);
    }

    public TablePojoType getNewPojoInstance() {
        return pojoInstanceSupplier.get();
    }
}
