package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.config;

public interface BeeTableMapperConfig<Pojo> {
    String getTableName();

    String getIdFieldName();

    Object getId(Pojo pojo);

    void setId(Pojo pojo, Object id);

    Pojo getNewPojoInstance();
}
