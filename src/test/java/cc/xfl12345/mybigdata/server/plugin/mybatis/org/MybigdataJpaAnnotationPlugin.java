package cc.xfl12345.mybigdata.server.plugin.mybatis.org;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;
import java.util.Properties;

import static cc.xfl12345.mybigdata.server.plugin.mybatis.org.AnnotationUtil.justAddAnnotation2Field;

public class MybigdataJpaAnnotationPlugin extends PluginAdapter {
    protected boolean addJpaEntityAnnotation = true;
    protected boolean addJpaIdAnnotation = true;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        addJpaEntityAnnotation = Boolean.parseBoolean(
            properties.getProperty("enableJpaEntity", "true")
        );
        addJpaIdAnnotation = Boolean.parseBoolean(
            properties.getProperty("enableJpaId", "true")
        );
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (addJpaEntityAnnotation) {
            topLevelClass.addAnnotation("@javax.persistence.Entity");
        }
        return true;
    }


    @Override
    public boolean modelFieldGenerated(Field field,
                                       TopLevelClass topLevelClass,
                                       IntrospectedColumn introspectedColumn,
                                       IntrospectedTable introspectedTable,
                                       ModelClassType modelClassType) {
        if (addJpaIdAnnotation && introspectedColumn.isIdentity()) {
            justAddAnnotation2Field(topLevelClass, field, javax.persistence.Id.class, null);
        }
        // if (introspectedColumn.getActualColumnName().equals("global_id")) {
        //     System.out.println("Table:[" + introspectedTable.getFullyQualifiedTableNameAtRuntime() + "], "
        //         + "Column:[" + introspectedColumn.getActualColumnName() + "], "
        //         + "isGeneratedColumn:[" + introspectedColumn.isGeneratedColumn() + "], "
        //         + "isIdentity:[" + introspectedColumn.isIdentity() + "]"
        //     );
        // }
        return true;
    }
}
