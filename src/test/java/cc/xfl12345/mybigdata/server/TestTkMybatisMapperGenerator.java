package cc.xfl12345.mybigdata.server;

import org.apache.ibatis.io.Resources;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class TestTkMybatisMapperGenerator {
    public static void main(String[] args) throws Exception {
        // String s = System.getProperty("line.separator");
        System.setProperty("line.separator", "\n");
        generate(Resources.getResourceAsStream("mybatis/tk_generator_config.xml"), true);
    }

    public static void generate(InputStream stream, boolean overwrite) throws Exception {
        List<String> warnings = new ArrayList<String>();
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(stream);
        stream.close();
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
        for (String warning : warnings) {
            System.out.println(warning);
        }
    }
}
