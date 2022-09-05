package cc.xfl12345.mybigdata.server.mysql.database.converter;

/**
 * ID 类型数据转换器，统一使用该类。
 * 为了应对日后可能更换 ID 类型的情况，提前做好准备。实现最少能量修改代码。
 */
public class AppIdTypeConverter extends IdTypeConverter<Long> {
    public AppIdTypeConverter() {
        super(Long.class);
    }
}
