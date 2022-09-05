package cc.xfl12345.mybigdata.server;


import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import freemarker.cache.URLTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import strman.Strman;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class StudyFreemarker {

    private static final String TEMPLATE_PATH = "freemarker/templates/";

    public static void main(String[] args) {
        // step1 创建freeMarker配置实例
        Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        Writer out = null;

        try {
            // step2 获取模版路径
            configuration.setTemplateLoader(new URLTemplateLoader() {
                @Override
                protected URL getURL(String name) {
                    return Thread.currentThread().getContextClassLoader().getResource(TEMPLATE_PATH + name);
                }
            });
            // step3 创建数据模型
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("classPath", "cc.xfl12345.mybigdata.server");

            String canonicalName = GlobalDataRecord.class.getCanonicalName();
            String simpleName = GlobalDataRecord.class.getSimpleName();

            dataMap.put("classCanonicalName", canonicalName);
            dataMap.put("classSimpleName", simpleName);
            dataMap.put("classSimpleNameCamelCase", Strman.toCamelCase(simpleName));
            dataMap.put("classSimpleNamePascalCase", Strman.toStudlyCase(simpleName));
            // step4 加载模版文件
            Template template = configuration.getTemplate("simple_write_spring_bean_define.ftl");
            // step5 生成并输出数据
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            out = new OutputStreamWriter(byteArrayOutputStream);
            template.process(dataMap, out);

            out.close();
            System.out.println(byteArrayOutputStream.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
