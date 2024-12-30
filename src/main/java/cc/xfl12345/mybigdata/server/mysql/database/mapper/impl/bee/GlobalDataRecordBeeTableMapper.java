package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee;

import cc.xfl12345.mybigdata.server.common.api.GlobalDataRecordMapper;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableMapper;
import cc.xfl12345.mybigdata.server.common.database.pojo.CommonGlobalDataRecord;
import cc.xfl12345.mybigdata.server.common.database.pojo.SingleTableMapperWarpper;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import org.teasoft.bee.osql.api.Condition;

public abstract class GlobalDataRecordBeeTableMapper
    extends SingleTableMapperWarpper<GlobalDataRecord, CommonGlobalDataRecord, Condition>
    implements GlobalDataRecordMapper {

    public TableMapper<GlobalDataRecord, Condition> getRawTableMapper() {
        return getTableMapper();
    }
}
