package cc.xfl12345.mybigdata.server.mysql.database.mapper.orm.bee.config;



import cc.xfl12345.mybigdata.server.common.appconst.KeyWords;

import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Supplier;

public class BeeTableMapperConfigGenerator {

    public static <TablePojoType> BeeTableMapperConfig<TablePojoType> getConfig(Class<TablePojoType> cls) throws NoSuchMethodException {
        String tableName;
        String idFieldName = KeyWords.KEY_WORD_GLOBAL_ID;
        Function<TablePojoType, Long> idGetter = (value) -> null;
        Supplier<TablePojoType> pojoInstanceSupplier;

        // tableName
        Table tableAnnotation = cls.getAnnotation(Table.class);
        tableName = tableAnnotation.name();

        // idFieldName && idGetter
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Id.class) != null) {
                idFieldName = field.getName();
                Method method = cls.getDeclaredMethod("get" +
                    Character.toUpperCase(idFieldName.charAt(0)) +
                    (idFieldName.length() == 1 ? "" : idFieldName.substring(1))
                );

                idGetter = (value) -> {
                    try {
                        return (Long) method.invoke(value);
                    } catch (Exception e) {
                        throw  new RuntimeException(e);
                    }
                };

                break;
            }
        }

        // pojoInstanceSupplier
        Constructor<TablePojoType> constructor = cls.getDeclaredConstructor();
        pojoInstanceSupplier = () -> {
            try {
                return constructor.newInstance();
            } catch (Exception e) {
                throw  new RuntimeException(e);
            }
        };

        SimpleBeeTableMapperConfig<TablePojoType> config = new SimpleBeeTableMapperConfig<>();
        config.setTableName(tableName);
        config.setIdFieldName(idFieldName);
        config.setIdGetter(idGetter);
        config.setPojoInstanceSupplier(pojoInstanceSupplier);

        return config;
    }
}
