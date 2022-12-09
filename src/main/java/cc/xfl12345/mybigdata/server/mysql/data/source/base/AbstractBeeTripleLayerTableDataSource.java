package cc.xfl12345.mybigdata.server.mysql.data.source.base;

import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.data.source.GlobalDataRecordDataSource;
import cc.xfl12345.mybigdata.server.common.data.source.impl.AbstractDataSource;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableBasicMapper;
import cc.xfl12345.mybigdata.server.common.database.pojo.CommonGlobalDataRecord;
import cc.xfl12345.mybigdata.server.common.pojo.AffectedRowsCountChecker;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.BeeTableMapper;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractBeeTripleLayerTableDataSource<Value, FirstPojo, SecondPojo> extends AbstractDataSource<Value> {
    @Getter
    @Setter
    protected AffectedRowsCountChecker affectedRowsCountChecker = DefaultSingleton.AFFECTED_ROWS_COUNT_CHECKER;

    @Getter
    @Setter
    protected GlobalDataRecordDataSource globalDataRecordDataSource;

    @Getter
    @Setter
    protected BeeTableMapper<FirstPojo> firstMapper;

    @Getter
    @Setter
    protected BeeTableMapper<SecondPojo> secondMapper;

    @Override
    public void init() throws Exception {
        fieldNotNullChecker.check(globalDataRecordDataSource, CommonGlobalDataRecord.class);
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
