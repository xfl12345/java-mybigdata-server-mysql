package cc.xfl12345.mybigdata.server.mysql.database.mapper.base;

import cc.xfl12345.mybigdata.server.common.database.mapper.TableNoConditionMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.BeeTableMapperImpl;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class TableMapperProxy<Pojo> implements TableNoConditionMapper<Pojo> {
    protected AbstractTypedTableMapper<Pojo> tableMapper;

    // 默认使用 Bee 框架实现
    public TableMapperProxy() {
        tableMapper = new BeeTableMapperImpl<>(getGenericTypeFromRuntime());
    }

    public TableMapperProxy(AbstractTypedTableMapper<Pojo> tableMapper) {
        this.tableMapper = tableMapper;
    }

    @SuppressWarnings("unchecked")
    protected Class<Pojo> getGenericTypeFromRuntime() {
        return (Class<Pojo>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public AbstractTypedTableMapper<Pojo> getTableMapper() {
        return tableMapper;
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        tableMapper.afterPropertiesSet();
    }

    @Override
    public long insert(Pojo pojo) {
        return tableMapper.insert(pojo);
    }

    @Override
    public long insertBatch(List<Pojo> pojos) {
        return tableMapper.insertBatch(pojos);
    }

    @Override
    public Object insertAndReturnId(Pojo pojo) {
        return tableMapper.insertAndReturnId(pojo);
    }

    @Override
    public Pojo selectOne(Pojo pojo, String[] fields) {
        return tableMapper.selectOne(pojo, fields);
    }

    @Override
    public Pojo selectById(Object globalId, String[] fields) {
        return tableMapper.selectById(globalId, fields);
    }

    @Override
    public Object selectId(Pojo pojo) {
        return tableMapper.selectId(pojo);
    }

    @Override
    public void updateById(Pojo pojo, Object globalId) {
        tableMapper.updateById(pojo, globalId);
    }

    @Override
    public void deleteById(Object globalId) {
        tableMapper.deleteById(globalId);
    }

    @Override
    public Class<Pojo> getGenericType() {
        return tableMapper.getGenericType();
    }
}
