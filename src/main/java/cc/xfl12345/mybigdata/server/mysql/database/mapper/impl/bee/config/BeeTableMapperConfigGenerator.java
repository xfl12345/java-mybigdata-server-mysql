package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.config;



import cc.xfl12345.mybigdata.server.common.appconst.KeyWords;

import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Supplier;

public class BeeTableMapperConfigGenerator {

    public static <Pojo> BeeTableMapperConfig<Pojo> getConfig(Class<Pojo> cls) throws NoSuchMethodException {
        String tableName;
        String idFieldName = KeyWords.KEY_WORD_GLOBAL_ID;
        Function<Pojo, Object> idGetter = (value) -> null;
        Supplier<Pojo> pojoInstanceSupplier;

        // tableName
        Table tableAnnotation = cls.getAnnotation(Table.class);
        tableName = tableAnnotation.name();

        // idFieldName && idGetter
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Id.class) != null) {
                idFieldName = field.getName();
                // 获取该字段的 Getter 方法
                Method method = cls.getDeclaredMethod("get" +
                    // 大写字段名称的第一个字母
                    Character.toUpperCase(idFieldName.charAt(0)) +
                    // 拼接字段名称的剩余字符
                    (idFieldName.length() == 1 ? "" : idFieldName.substring(1))
                );

                idGetter = (value) -> {
                    try {
                        return method.invoke(value);
                    } catch (Exception e) {
                        throw  new RuntimeException(e);
                    }
                };

                break;
            }
        }

        // pojoInstanceSupplier
        Constructor<Pojo> constructor = cls.getDeclaredConstructor();
        pojoInstanceSupplier = () -> {
            try {
                return constructor.newInstance();
            } catch (Exception e) {
                throw  new RuntimeException(e);
            }
        };

        SimpleBeeTableMapperConfig<Pojo> config = new SimpleBeeTableMapperConfig<>();
        config.setTableName(tableName);
        config.setIdFieldName(idFieldName);
        config.setIdGetter(idGetter);
        config.setPojoInstanceSupplier(pojoInstanceSupplier);

        return config;
    }
}
