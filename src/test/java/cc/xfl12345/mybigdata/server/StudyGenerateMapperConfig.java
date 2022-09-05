package cc.xfl12345.mybigdata.server;

import cc.xfl12345.mybigdata.server.common.utility.MyReflectUtils;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import strman.Strman;

import java.io.IOException;
import java.util.List;

public class StudyGenerateMapperConfig {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        List<Class<?>> pojoList = MyReflectUtils.getClasses(GlobalDataRecord.class.getPackageName(), false);
        for (Class<?> pojoClass : pojoList) {
            String pojoSimpleName = pojoClass.getSimpleName();
            String mapperInterfaceClassName = String.format("%sMapper", pojoSimpleName);
            String mapperInterfaceClassNameCamelCase = Strman.toCamelCase(mapperInterfaceClassName);

            System.out.println("\n" +
                "    @Bean\n" +
                "    @ConditionalOnMissingBean\n" +
                "    public " + mapperInterfaceClassName + " "  + mapperInterfaceClassNameCamelCase + "()" +
                " throws Exception {\n" +
                "        return (" + mapperInterfaceClassName + ") getMapper(" + pojoSimpleName + ".class);\n" +
                "    }"
            );
        }
    }
}
