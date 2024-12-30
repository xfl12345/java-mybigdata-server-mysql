package cc.xfl12345.mybigdata.server.mysql.database.mapper.base;

import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.pojo.FieldNotNullChecker;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractTableMapper {
    @Getter
    @Setter
    protected FieldNotNullChecker fieldNotNullChecker = DefaultSingleton.FIELD_NOT_NULL_CHECKER;

    @Getter
    @Setter
    protected MapperProperties mapperProperties;

    @PostConstruct
    public void init() throws Exception {
        fieldNotNullChecker.check(mapperProperties, "mapperProperties");
    }

    @PreDestroy
    public void destroy() throws Exception {
    }
}
