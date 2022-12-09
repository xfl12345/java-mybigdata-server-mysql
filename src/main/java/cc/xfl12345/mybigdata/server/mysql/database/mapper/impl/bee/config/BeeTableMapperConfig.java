package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.config;

import cc.xfl12345.mybigdata.server.common.pojo.MbdId;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;

public interface BeeTableMapperConfig<Pojo> {
    String getTableName();

    String getIdFieldName();

    MysqlMbdId getId(Pojo pojo);

    void setId(Pojo pojo, MbdId<?> id);

    Pojo getNewPojoInstance();
}
