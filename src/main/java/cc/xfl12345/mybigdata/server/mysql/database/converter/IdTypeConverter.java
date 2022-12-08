package cc.xfl12345.mybigdata.server.mysql.database.converter;

/**
 * ID 类型数据转换器。
 * 为了应对日后可能更换 ID 类型的情况，提前做好准备。实现最少能量修改代码。
 * @param <ID>
 */
public abstract class IdTypeConverter<ID> {
    protected Class<ID> idTypeClass;

    protected ID id;

    public IdTypeConverter(Class<ID> cls) {
        idTypeClass = cls;
    }

    public ID convert(Object id) {
        return idTypeClass.cast(id);
    }

    public Class<ID> getIdTypeClass() {
        return idTypeClass;
    }
}
