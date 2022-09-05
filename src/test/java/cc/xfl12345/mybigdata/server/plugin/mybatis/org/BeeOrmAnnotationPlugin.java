package cc.xfl12345.mybigdata.server.plugin.mybatis.org;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.teasoft.bee.osql.annotation.Column;
import org.teasoft.bee.osql.annotation.Entity;
import org.teasoft.bee.osql.annotation.PrimaryKey;
import org.teasoft.bee.osql.annotation.Table;

import java.util.List;

import static cc.xfl12345.mybigdata.server.plugin.mybatis.org.AnnotationUtil.justAddAnnotation2Class;
import static cc.xfl12345.mybigdata.server.plugin.mybatis.org.AnnotationUtil.justAddAnnotation2Field;

public class BeeOrmAnnotationPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        justAddAnnotation2Class(topLevelClass, Table.class,  "\"" + tableName + "\"");
        // justAddAnnotation2Class(topLevelClass, Entity.class,  "\"" + tableName + "\"");
        return true;
    }

    @Override
    public boolean modelFieldGenerated(Field field,
                                       TopLevelClass topLevelClass,
                                       IntrospectedColumn introspectedColumn,
                                       IntrospectedTable introspectedTable,
                                       ModelClassType modelClassType) {
        justAddAnnotation2Field(topLevelClass, field, Column.class, "\"" + introspectedColumn.getActualColumnName() + "\"");
        if (introspectedColumn.isIdentity()) {
            justAddAnnotation2Field(topLevelClass, field, PrimaryKey.class, null);
        }
        return true;
    }
}
