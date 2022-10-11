package cc.xfl12345.mybigdata.server.mysql.database.mapper.orm.bee.config;

public interface BeeTableMapperConfig<TablePojoType> {
    String getTableName();

    String getIdFieldName();

    Object getId(TablePojoType value);

    TablePojoType getNewPojoInstance();
}
