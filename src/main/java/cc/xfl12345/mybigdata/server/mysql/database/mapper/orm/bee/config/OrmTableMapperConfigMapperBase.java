package cc.xfl12345.mybigdata.server.mysql.database.mapper.orm.bee.config;

import java.util.HashMap;
import java.util.Map;

public class OrmTableMapperConfigMapperBase<ConfigType> implements OrmTableMapperConfigMapper<ConfigType> {
    protected Map<Class<?>, ConfigType> map;

    public OrmTableMapperConfigMapperBase() {
        map = new HashMap<>();
    }

    public OrmTableMapperConfigMapperBase(int initialCapacity) {
        map = new HashMap<>(initialCapacity);
    }


    public <TablePojoType> void putConfig(Class<TablePojoType> cls, ConfigType config) {
        map.put(cls, config);
    }

    public <TablePojoType> ConfigType getConfig(Class<TablePojoType> cls) {
        return tryCast(map.get(cls), cls);
    }

    public <TablePojoType> ConfigType removeConfig(Class<TablePojoType> cls) {
        return tryCast(map.remove(cls), cls);
    }

    @SuppressWarnings("unchecked")
    private <TablePojoType> ConfigType tryCast(ConfigType config, Class<TablePojoType> cls) {
        return config == null ? null : (ConfigType) config;
    }
}

