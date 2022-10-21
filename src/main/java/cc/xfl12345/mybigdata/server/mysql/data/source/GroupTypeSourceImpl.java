package cc.xfl12345.mybigdata.server.mysql.data.source;

import cc.xfl12345.mybigdata.server.common.data.source.GroupTypeSource;

import java.util.List;

public class GroupTypeSourceImpl implements GroupTypeSource {
    @Override
    public Object insertAndReturnId(List<Object> objects) {
        return null;
    }

    @Override
    public long insert(List<Object> objects) {
        return 0;
    }

    @Override
    public long insertBatch(List<List<Object>> lists) {
        return 0;
    }

    @Override
    public Object selectId(List<Object> objects) {
        return null;
    }

    @Override
    public List<Object> selectById(Object globalId) {
        return null;
    }

    @Override
    public void updateById(List<Object> objects, Object globalId) {

    }

    @Override
    public void deleteById(Object globalId) {

    }
}
