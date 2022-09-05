package cc.xfl12345.mybigdata.server.mysql.database.converter;

import java.util.function.Consumer;

/**
 * ID 类型数据转换器。
 * 为了应对日后可能更换 ID 类型的情况，提前做好准备。实现最少能量修改代码。
 * @param <IdType>
 */
public abstract class IdTypeConverter<IdType> {
    protected Class<IdType> idTypeClass;

    protected IdType id;

    public IdTypeConverter(Class<IdType> cls) {
        idTypeClass = cls;
    }

    public IdType convert(Object id) {
        return idTypeClass.cast(id);
    }

    public void injectId2Object(Object id, Consumer<IdType> func) {
        func.accept(idTypeClass.cast(id));
    }
}
