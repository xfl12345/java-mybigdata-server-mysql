package cc.xfl12345.mybigdata.server.plugin.mybatis.org;

import cc.xfl12345.mybigdata.server.common.appconst.KeyWords;
import lombok.Getter;
import lombok.Setter;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.teasoft.bee.osql.annotation.JoinTable;
import org.teasoft.bee.osql.annotation.JoinType;

import java.util.List;

public class MyBigDataBeeOrmAssociationPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        IntrospectedColumn column = introspectedTable.getColumn(KeyWords.KEY_WORD_GLOBAL_ID);
        if (column != null) {
            String associationClassName = "cc.xfl12345.mybigdata.server.mysql.table.pojo.GlobalDataRecord";
            topLevelClass.addImportedType(new FullyQualifiedJavaType(List.class.getCanonicalName()));
            Field field = new Field();
            field.setVisibility(JavaVisibility.PRIVATE);
            field.setType(new FullyQualifiedJavaType("List<" + associationClassName + ">"));
            field.setName("globalDataRecords");
            field.addAnnotation("@" + Getter.class.getCanonicalName());
            field.addAnnotation("@" + Setter.class.getCanonicalName());
            field.addAnnotation("@" + JoinTable.class.getCanonicalName() + "(" +
                "mainField = \"" + KeyWords.KEY_WORD_GLOBAL_ID + "\", " +
                "subField = \"id\", " +
                "subClazz = " + associationClassName + ".class, " +
                "joinType = " + JoinType.class.getCanonicalName() + ".LEFT_JOIN)"
            );
            topLevelClass.addField(field);
        }
        return true;
    }
}
