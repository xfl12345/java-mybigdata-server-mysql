package cc.xfl12345.mybigdata.server;

import cc.xfl12345.mybigdata.server.common.utility.MyReflectUtils;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import strman.Strman;

import java.io.IOException;
import java.util.Collection;

public class StudyGenerateMapperConfig {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Collection<Class<?>> pojoList = MyReflectUtils.getClasses(
            GlobalDataRecord.class.getPackageName(),
            false,
            false,
            false
        );
        for (Class<?> pojoClass : pojoList) {
            String pojoSimpleName = pojoClass.getSimpleName();
            String mapperInterfaceClassName = pojoSimpleName + "Mapper";
            String mapperProxyClassName = mapperInterfaceClassName + "Proxy";
            String mapperName = Strman.toCamelCase(mapperInterfaceClassName);

            System.out.println("\n" +
                "    @Bean\n" +
                "    @ConditionalOnMissingBean\n" +
                "    public " + mapperInterfaceClassName + " " + mapperName + "()" +
                " throws Exception {\n" +
                // "        return new "+ mapperProxyClassName + "(getMapper(" + pojoSimpleName + ".class));\n" +
                // "        return new "+ mapperProxyClassName + "();\n" +
                "        return initMapper(new "+ mapperProxyClassName + "());\n" +
                "    }"
            );
        }
    }
}
