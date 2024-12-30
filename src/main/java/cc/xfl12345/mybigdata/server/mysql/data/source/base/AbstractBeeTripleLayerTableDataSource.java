package cc.xfl12345.mybigdata.server.mysql.data.source.base;

import cc.xfl12345.mybigdata.server.common.data.source.StringTypeSource;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.CoreTableCache;
import lombok.Getter;
import lombok.Setter;
import org.teasoft.bee.osql.api.Condition;

public abstract class AbstractBeeTripleLayerTableDataSource<Value, FirstPojo, SecondPojo>
    extends AbstractTripleLayerTableDataSource<Value, FirstPojo, SecondPojo, Condition> {

    @Getter
    @Setter
    protected CoreTableCache coreTableCache;

    @Getter
    @Setter
    protected StringTypeSource stringTypeSource;

    @Override
    public void init() throws Exception {
        fieldNotNullChecker.check(coreTableCache, "coreTableCache");
        fieldNotNullChecker.check(stringTypeSource, String.class);
        super.init();
    }
}
