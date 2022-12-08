package cc.xfl12345.mybigdata.server.mysql.database.mapper.base;

import cc.xfl12345.mybigdata.server.common.appconst.CURD;
import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableBasicMapper;
import cc.xfl12345.mybigdata.server.common.pojo.AffectedRowsCountChecker;

public abstract class AbstractTypedTableMapper<Pojo>
    extends AbstractTableMapper
    implements TableBasicMapper<Pojo> {
    protected AffectedRowsCountChecker affectedRowsCountChecker;

    @Override
    public void init() throws Exception {
        super.init();
        affectedRowsCountChecker = mapperProperties.getAffectedRowsCountChecker();
        if (affectedRowsCountChecker == null) {
            affectedRowsCountChecker = DefaultSingleton.AFFECTED_ROWS_COUNT_CHECKER;
        }
    }

    public void checkAffectedRowShouldBeOne(long affectedRowsCount, CURD operation) {
        affectedRowsCountChecker.checkAffectedRowShouldBeOne(affectedRowsCount, operation, getTableName());
    }

    public void checkAffectedRowsCountDoesNotMatch(long affectedRowsCount, long expectCount, CURD operation) {
        affectedRowsCountChecker.checkAffectedRowsCountDoesNotMatch(affectedRowsCount, expectCount, operation, getTableName());
    }
    public abstract String getTableName();
}
