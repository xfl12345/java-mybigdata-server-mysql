package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.config;

import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;

public interface BeeTableMapperConfig<Pojo> {
    String getTableName();

    String getIdFieldName();

    MysqlMbdId getMbdId(Pojo pojo);

    void setMbdId(Pojo pojo, MbdId id);

    Object getId(Pojo pojo);

    void setId(Pojo pojo, Object id);

    Pojo getNewPojoInstance();
}
