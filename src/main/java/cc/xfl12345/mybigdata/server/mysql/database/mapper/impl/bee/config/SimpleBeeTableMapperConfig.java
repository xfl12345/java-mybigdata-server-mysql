package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.config;

import lombok.Getter;
import lombok.Setter;

import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleBeeTableMapperConfig<Pojo> implements BeeTableMapperConfig<Pojo> {
    @Getter
    @Setter
    protected String tableName;

    @Getter
    @Setter
    protected String idFieldName;

    @Setter
    protected Function<Pojo, Object> idGetter = (value) -> null;

    @Setter
    protected Supplier<Pojo> pojoInstanceSupplier = () -> null;

    public Object getId(Pojo value) {
        return idGetter.apply(value);
    }

    public Pojo getNewPojoInstance() {
        return pojoInstanceSupplier.get();
    }
}
