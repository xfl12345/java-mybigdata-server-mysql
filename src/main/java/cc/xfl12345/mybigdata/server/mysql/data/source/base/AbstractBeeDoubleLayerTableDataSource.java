package cc.xfl12345.mybigdata.server.mysql.data.source.base;

import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.data.source.GlobalDataRecordDataSource;
import cc.xfl12345.mybigdata.server.common.data.source.impl.AbstractDataSource;
import cc.xfl12345.mybigdata.server.common.database.pojo.CommonGlobalDataRecord;
import cc.xfl12345.mybigdata.server.common.pojo.AffectedRowsCountChecker;
import cc.xfl12345.mybigdata.server.mysql.database.converter.AppIdTypeConverter;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.BeeTableMapper;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractBeeDoubleLayerTableDataSource<Value, Pojo> extends AbstractDataSource<Value> {
    @Getter
    @Setter
    protected AffectedRowsCountChecker affectedRowsCountChecker = DefaultSingleton.AFFECTED_ROWS_COUNT_CHECKER;

    @Getter
    @Setter
    protected AppIdTypeConverter idTypeConverter;

    @Getter
    @Setter
    protected GlobalDataRecordDataSource globalDataRecordDataSource;

    @Getter
    @Setter
    protected BeeTableMapper<Pojo> mapper;

    @Override
    public void init() throws Exception {
        fieldNotNullChecker.check(globalDataRecordDataSource, CommonGlobalDataRecord.class);
        fieldNotNullChecker.check(mapper, getPojoClass());
        fieldNotNullChecker.check(idTypeConverter, "idTypeConverter");
        super.init();
    }

    protected abstract Class<Pojo> getPojoClass();
}
