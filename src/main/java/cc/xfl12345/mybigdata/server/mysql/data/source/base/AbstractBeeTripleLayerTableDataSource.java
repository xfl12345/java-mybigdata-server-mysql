package cc.xfl12345.mybigdata.server.mysql.data.source.base;

import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.data.source.IdDataSource;
import cc.xfl12345.mybigdata.server.common.data.source.StringTypeSource;
import cc.xfl12345.mybigdata.server.common.data.source.impl.AbstractDataSource;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableBasicMapper;
import cc.xfl12345.mybigdata.server.common.pojo.AffectedRowsCountChecker;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.CoreTableCache;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.BeeTableMapper;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractBeeTripleLayerTableDataSource<Value, FirstPojo, SecondPojo> extends AbstractDataSource<Value> {
    @Getter
    @Setter
    protected AffectedRowsCountChecker affectedRowsCountChecker = DefaultSingleton.AFFECTED_ROWS_COUNT_CHECKER;

    @Getter
    @Setter
    protected CoreTableCache coreTableCache;

    @Getter
    @Setter
    protected IdDataSource idDataSource;

    @Getter
    @Setter
    protected StringTypeSource stringTypeSource;

    @Getter
    @Setter
    protected BeeTableMapper<FirstPojo> firstMapper;

    @Getter
    @Setter
    protected BeeTableMapper<SecondPojo> secondMapper;

    @Override
    public void init() throws Exception {
        fieldNotNullChecker.check(coreTableCache, "coreTableCache");
        fieldNotNullChecker.check(idDataSource, MbdId.class);
        fieldNotNullChecker.check(stringTypeSource, String.class);
        fieldNotNullChecker.check(firstMapper, getFirstPojoType());
        fieldNotNullChecker.check(secondMapper, getSecondPojoType());
        super.init();
    }

    protected <T> Class<T> getMapperPojoType(TableBasicMapper<T> mapper, int genericTypeIndex) {
        return mapper == null ? getTypeFromRuntime(genericTypeIndex) : mapper.getPojoType();
    }

    @Override
    public Class<Value> getValueType() {
        return getTypeFromRuntime(0);
    }

    public Class<FirstPojo> getFirstPojoType() {
        return getMapperPojoType(firstMapper, 1);
    }

    public Class<SecondPojo> getSecondPojoType() {
        return getMapperPojoType(secondMapper, 2);
    }
}
