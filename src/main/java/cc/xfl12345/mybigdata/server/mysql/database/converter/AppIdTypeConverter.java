package cc.xfl12345.mybigdata.server.mysql.database.converter;

import java.math.BigDecimal;

/**
 * ID 类型数据转换器，统一使用该类。
 * 为了应对日后可能更换 ID 类型的情况，提前做好准备。实现最少能量修改代码。
 */
public class AppIdTypeConverter extends IdTypeConverter<Long> {
    public AppIdTypeConverter() {
        super(Long.class);
    }

    @Override
    public Long convert(Object id) {
        if (id instanceof String str) {
            return Long.parseLong(str);
        } else if (id instanceof BigDecimal bigDecimal) {
            return bigDecimal.longValue();
        }

        return super.convert(id);
    }
}
