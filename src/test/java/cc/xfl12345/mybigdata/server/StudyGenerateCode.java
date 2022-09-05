package cc.xfl12345.mybigdata.server;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;

public class StudyGenerateCode {

    public static void main(String[] args) {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
//        FoxyURLClassLoader urlClassLoader = new FoxyURLClassLoader();

        DynamicType.Unloaded<?> dynamicType = new ByteBuddy()
            .with(new NamingStrategy.AbstractBase() {
                protected String name(TypeDescription superClass) {
                    return "i.love.ByteBuddy." + superClass.getSimpleName();
                }
            })
            .subclass(Object.class)
            .make();


//        new ByteArrayClassLoader(Thread.currentThread().getContextClassLoader(), new HashMap<>())

    }

}
