package cc.xfl12345.mybigdata.server;

import cc.xfl12345.mybigdata.server.mysql.database.mapper.GlobalDataRecordMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.AppTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.impl.AbstractAppTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.jar.asm.Opcodes;

import java.lang.reflect.Type;

public class StudyGenerateCode {

    public static void main(String[] args) throws Exception {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
//        FoxyURLClassLoader urlClassLoader = new FoxyURLClassLoader();

        DynamicType.Unloaded<?> dynamicType = new ByteBuddy()
            .with(new NamingStrategy.AbstractBase() {
                protected String name(TypeDescription superClass) {
                    return "i.love.ByteBuddy." + superClass.getSimpleName();
                }
            })
            .subclass(Object.class)
            .make();
        Class<?> genClass = dynamicType.load(classLoader).getLoaded();
        dynamicType.close();

        System.out.println(genClass.getCanonicalName());
//        new ByteArrayClassLoader(Thread.currentThread().getContextClassLoader(), new HashMap<>())

        StudyGenerateCode studyGenerateCode = new StudyGenerateCode();
        AbstractAppTableMapper<?> mapper = studyGenerateCode.getMapper(GlobalDataRecord.class, GlobalDataRecordMapper.class);
        System.out.println(mapper.getClass().getCanonicalName());
        System.out.println(mapper.getTablePojoType().getCanonicalName());
    }

    @SuppressWarnings("unchecked")
    public  <T> AbstractAppTableMapper<T> getMapper(
        Class<T> pojoType,
        Class<? extends AppTableMapper<T>> mapperType) throws Exception {
        // Type pojoType = mapperType.getGenericSuperclass();
        DynamicType.Unloaded<AbstractAppTableMapper<T>> dynamicType = (DynamicType.Unloaded<AbstractAppTableMapper<T>>) new ByteBuddy()
            .with(new NamingStrategy.AbstractBase() {
                protected String name(TypeDescription superClass) {
                    return GlobalDataRecordMapper.class.getPackageName() + ".impl."
                        + mapperType.getSimpleName() + "DynamicImpl";
                }
            })
            .subclass(TypeDescription.Generic.Builder.parameterizedType(AbstractAppTableMapper.class, pojoType).build())
            .implement(mapperType)
            .modifiers(Opcodes.ACC_PUBLIC)
            .defineMethod(
                "getTablePojoType",
                TypeDescription.Generic.Builder.parameterizedType(Class.class, pojoType).build(),
                Opcodes.ACC_PUBLIC
            )
            .withParameters(new Type[]{})
            .intercept(FixedValue.value(pojoType))
            .annotateMethod(AnnotationDescription.Builder.ofType(Override.class).build())
            .make();
        Class<AbstractAppTableMapper<T>> mapperClass = (Class<AbstractAppTableMapper<T>>) dynamicType
            .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER_PERSISTENT)
            .getLoaded();
        dynamicType.close();
        return mapperClass.getDeclaredConstructor().newInstance();
    }

}
