/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package cc.xfl12345.mybigdata.server.plugin.mybatis.tk;

import io.swagger.annotations.ApiModelProperty;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.util.StringUtility;
import tk.mybatis.mapper.generator.FalseMethodPlugin;

import java.text.MessageFormat;
import java.util.*;

import static cc.xfl12345.mybigdata.server.plugin.mybatis.org.AnnotationUtil.justAddAnnotation2Class;
import static cc.xfl12345.mybigdata.server.plugin.mybatis.org.AnnotationUtil.justAddAnnotation2Field;

public class SmartMapperPlugin extends FalseMethodPlugin {

    private Set<String> mappers = new HashSet<String>();
    private boolean caseSensitive = false;
    private boolean useMapperCommentGenerator = true;
    //开始的分隔符，例如mysql为`，sqlserver为[
    private String beginningDelimiter = "";
    //结束的分隔符，例如mysql为`，sqlserver为]
    private String endingDelimiter = "";
    //数据库模式
    private String schema;
    //注释生成器
    private CommentGeneratorConfiguration commentCfg;
    //强制生成注解
    private boolean forceAnnotation;

    //是否需要生成Data注解
    private boolean needsData = false;
    //是否需要生成Getter注解
    private boolean needsGetter = false;
    //是否需要生成Setter注解
    private boolean needsSetter = false;
    //是否需要生成ToString注解
    private boolean needsToString = false;
    //是否需要生成Accessors(chain = true)注解
    private boolean needsAccessors = false;
    private boolean needsBuilder = false;
    private boolean needsSuperBuilder = false;
    private boolean needsNoArgsConstructor = false;
    private boolean needsAllArgsConstructor = false;

    private boolean needFieldNameConstants = false;

    //是否需要生成EqualsAndHashCode注解
    private boolean needsEqualsAndHashCode = false;
    //是否需要生成EqualsAndHashCode注解，并且“callSuper = true”
    private boolean needsEqualsAndHashCodeAndCallSuper = false;
    //是否生成字段名常量
    private boolean generateColumnConsts = false;
    //是否生成默认的属性的静态方法
    private boolean generateDefaultInstanceMethod = false;
    //是否生成swagger注解,包括 @ApiModel和@ApiModelProperty
    private boolean needsSwagger = false;

    public String getDelimiterName(String name) {
        StringBuilder nameBuilder = new StringBuilder();
        if (StringUtility.stringHasValue(schema)) {
            nameBuilder.append(schema);
            nameBuilder.append(".");
        }
        nameBuilder.append(beginningDelimiter);
        nameBuilder.append(name);
        nameBuilder.append(endingDelimiter);
        return nameBuilder.toString();
    }

    public String getFieldCommentDelimiterName(String name) {
        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append(beginningDelimiter);
        nameBuilder.append(name);
        nameBuilder.append(endingDelimiter);
        return nameBuilder.toString();
    }

    /**
     * 生成的Mapper接口
     *
     * @param interfaze
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        //获取实体类
        FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        //import接口
        for (String mapper : mappers) {
            interfaze.addImportedType(new FullyQualifiedJavaType(mapper));
            interfaze.addSuperInterface(new FullyQualifiedJavaType(mapper + "<" + entityType.getShortName() + ">"));
        }
        //import实体类
        interfaze.addImportedType(entityType);
        return true;
    }


    protected void addJpaColumnAnnotation(
        Field field,
        TopLevelClass topLevelClass,
        IntrospectedColumn introspectedColumn,
        IntrospectedTable introspectedTable,
        String column) {
        String javaCode = "name = \"" + getFieldCommentDelimiterName(column) + "\""
            + ", nullable = " + introspectedColumn.isNullable();
        if (introspectedColumn.isStringColumn()) {
            javaCode += ", length = "+ introspectedColumn.getLength();
        }
        justAddAnnotation2Field(topLevelClass, field, javax.persistence.Column.class, javaCode);
    }

    @Override
    public boolean modelFieldGenerated(Field field,
                                       TopLevelClass topLevelClass,
                                       IntrospectedColumn introspectedColumn,
                                       IntrospectedTable introspectedTable,
                                       ModelClassType modelClassType) {
        //添加注解
        if (field.isTransient()) {
            //@Column
            justAddAnnotation2Field(topLevelClass, field, javax.persistence.Transient.class, null);
        }
        for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
            if (introspectedColumn == column) {
                justAddAnnotation2Field(topLevelClass, field, javax.persistence.Id.class, null);
                break;
            }
        }
        String column = introspectedColumn.getActualColumnName();
        if (StringUtility.stringContainsSpace(column) || introspectedTable.getTableConfiguration().isAllColumnDelimitingEnabled()) {
            column = introspectedColumn.getContext().getBeginningDelimiter()
                + column
                + introspectedColumn.getContext().getEndingDelimiter();
        }
        if (!column.equals(introspectedColumn.getJavaProperty())) {
            //@Column
            addJpaColumnAnnotation(field, topLevelClass, introspectedColumn, introspectedTable, column);
        } else if (StringUtility.stringHasValue(beginningDelimiter) || StringUtility.stringHasValue(endingDelimiter)) {
            addJpaColumnAnnotation(field, topLevelClass, introspectedColumn, introspectedTable, column);
        } else if (forceAnnotation) {
            addJpaColumnAnnotation(field, topLevelClass, introspectedColumn, introspectedTable, column);
        }
        if (introspectedColumn.isIdentity()) {
            if ("JDBC".equals(introspectedTable.getTableConfiguration().getGeneratedKey().getRuntimeSqlStatement())) {
                justAddAnnotation2Field(topLevelClass, field, javax.persistence.GeneratedValue.class, "generator = \"JDBC\"");
            } else {
                justAddAnnotation2Field(topLevelClass, field, javax.persistence.GeneratedValue.class, "strategy = GenerationType.IDENTITY");
            }
        } else if (introspectedColumn.isSequenceColumn()) {
            //在 Oracle 中，如果需要是 SEQ_TABLENAME，那么可以配置为 select SEQ_{1} from dual
            String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
            String sql = MessageFormat.format(introspectedTable.getTableConfiguration().getGeneratedKey().getRuntimeSqlStatement(), tableName, tableName.toUpperCase());
            justAddAnnotation2Field(topLevelClass, field, javax.persistence.GeneratedValue.class, "strategy = GenerationType.IDENTITY, generator = \"" + sql + "\"");
        }
        // region swagger注解
        if (this.needsSwagger) {
            String remarks = introspectedColumn.getRemarks();
            if (remarks == null) {
                remarks = "";
            }
            String swaggerAnnotation = "@ApiModelProperty(value = \"%s\" da )";
            justAddAnnotation2Field(topLevelClass, field, ApiModelProperty.class,
                "\"" +
                    remarks.replaceAll("\r", "").replaceAll("\n", "")
                    + "\""
            );
        }
        // endregion
        return true;
    }


    protected void addJpaTableAnnotation(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String tableName) {
        justAddAnnotation2Class(topLevelClass, javax.persistence.Table.class, "name = \"" + getDelimiterName(tableName) + "\"");
    }

    /**
     * 处理实体类的包和@Table注解
     *
     * @param topLevelClass
     * @param introspectedTable
     */
    protected void processEntityClass(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // //引入JPA注解
        // topLevelClass.addImportedType("javax.persistence.*");
        //lombok扩展开始
        //如果需要Data，引入包，代码增加注解
        if (this.needsData) {
            justAddAnnotation2Class(topLevelClass, lombok.Data.class, null);
        }
        //如果需要Getter，引入包，代码增加注解
        if (this.needsGetter) {
            justAddAnnotation2Class(topLevelClass, lombok.Getter.class, null);
        }
        //如果需要Setter，引入包，代码增加注解
        if (this.needsSetter) {
            justAddAnnotation2Class(topLevelClass, lombok.Setter.class, null);
        }
        //如果需要ToString，引入包，代码增加注解
        if (this.needsToString) {
            justAddAnnotation2Class(topLevelClass, lombok.ToString.class, null);
        }
        // 如果需要EqualsAndHashCode，并且“callSuper = true”，引入包，代码增加注解
        if (this.needsEqualsAndHashCodeAndCallSuper) {
            justAddAnnotation2Class(topLevelClass, lombok.EqualsAndHashCode.class, "callSuper = true");
        } else {
            // 如果需要EqualsAndHashCode，引入包，代码增加注解
            if (this.needsEqualsAndHashCode) {
                justAddAnnotation2Class(topLevelClass, lombok.EqualsAndHashCode.class, null);
            }
        }
        // 如果需要Accessors，引入包，代码增加注解
        if (this.needsAccessors) {
            justAddAnnotation2Class(topLevelClass, lombok.experimental.Accessors.class, "chain = true");
        }
        if (this.needsSuperBuilder) {
            justAddAnnotation2Class(topLevelClass, lombok.experimental.SuperBuilder.class, null);
        }
        if (this.needsBuilder) {
            justAddAnnotation2Class(topLevelClass, lombok.Builder.class, null);
        }
        if (this.needsNoArgsConstructor) {
            justAddAnnotation2Class(topLevelClass, lombok.NoArgsConstructor.class, null);
        }
        if (this.needsAllArgsConstructor) {
            justAddAnnotation2Class(topLevelClass, lombok.AllArgsConstructor.class, null);
        }
        if (this.needFieldNameConstants) {
            justAddAnnotation2Class(topLevelClass, lombok.experimental.FieldNameConstants.class, null);
        }
        // lombok扩展结束
        // region swagger扩展
        if (this.needsSwagger) {
            //增加注解(去除注释中的转换符)
            String remarks = introspectedTable.getRemarks();
            if (remarks == null) {
                remarks = "";
            }
            justAddAnnotation2Class(topLevelClass, io.swagger.annotations.ApiModel.class, "\"" + remarks.replaceAll("\r", "").replaceAll("\n", "") + "\"");
        }
        // endregion swagger扩展
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();

        //region 文档注释
        String remarks = introspectedTable.getRemarks();
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * 表名：" + tableName);
        if (remarks != null) {
            remarks = remarks.trim();
        }
        if (remarks != null && remarks.trim().length() > 0) {
            String[] lines = remarks.split("\\r?\\n");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (i == 0) {
                    topLevelClass.addJavaDocLine(" * 表注释：" + line);
                } else {
                    topLevelClass.addJavaDocLine(" *         " + line);
                }
            }
        }
        topLevelClass.addJavaDocLine("*/");
        //endregion
        //如果包含空格，或者需要分隔符，需要完善
        if (StringUtility.stringContainsSpace(tableName)) {
            tableName = context.getBeginningDelimiter()
                + tableName
                + context.getEndingDelimiter();
        }
        //是否忽略大小写，对于区分大小写的数据库，会有用
        if (caseSensitive && !topLevelClass.getType().getShortName().equals(tableName)) {
            addJpaTableAnnotation(topLevelClass, introspectedTable, tableName);
        } else if (!topLevelClass.getType().getShortName().equalsIgnoreCase(tableName)) {
            addJpaTableAnnotation(topLevelClass, introspectedTable, tableName);
        } else if (StringUtility.stringHasValue(schema)
            || StringUtility.stringHasValue(beginningDelimiter)
            || StringUtility.stringHasValue(endingDelimiter)) {
            addJpaTableAnnotation(topLevelClass, introspectedTable, tableName);
        } else if (forceAnnotation) {
            addJpaTableAnnotation(topLevelClass, introspectedTable, tableName);
        }
        if (generateColumnConsts) {
            for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
                Field field = new Field();
                field.setVisibility(JavaVisibility.PUBLIC);
                field.setStatic(true);
                field.setFinal(true);
                field.setName(introspectedColumn.getActualColumnName().toUpperCase()); //$NON-NLS-1$
                field.setType(new FullyQualifiedJavaType(String.class.getName())); //$NON-NLS-1$
                field.setInitializationString("\"" + introspectedColumn.getJavaProperty() + "\"");
                context.getCommentGenerator().addClassComment(topLevelClass, introspectedTable);
                topLevelClass.addField(field);
                //增加字段名常量,用于pageHelper
                Field columnField = new Field();
                columnField.setVisibility(JavaVisibility.PUBLIC);
                columnField.setStatic(true);
                columnField.setFinal(true);
                columnField.setName("DB_" + introspectedColumn.getActualColumnName().toUpperCase()); //$NON-NLS-1$
                columnField.setType(new FullyQualifiedJavaType(String.class.getName())); //$NON-NLS-1$
                columnField.setInitializationString("\"" + introspectedColumn.getActualColumnName() + "\"");
                topLevelClass.addField(columnField);
            }
        }
        if (generateDefaultInstanceMethod) {
            //注意基本类型和包装的index要一致,方便后面使用
            List<String> baseClassName = Arrays.asList("byte", "short", "char", "int", "long", "float", "double", "boolean");
            List<String> wrapperClassName = Arrays.asList("Byte", "Short", "Character", "Integer", "Long", "Float", "Double", "Boolean");
            List<String> otherClassName = Arrays.asList("String", "BigDecimal", "BigInteger");
            Method defaultMethod = new Method();
            //增加方法注释
            defaultMethod.addJavaDocLine("/**");
            defaultMethod.addJavaDocLine(" * 带默认值的实例");
            defaultMethod.addJavaDocLine("*/");
            defaultMethod.setStatic(true);
            defaultMethod.setName("defaultInstance");
            defaultMethod.setVisibility(JavaVisibility.PUBLIC);
            defaultMethod.setReturnType(topLevelClass.getType());
            defaultMethod.addBodyLine(String.format("%s instance = new %s();", topLevelClass.getType().getShortName(), topLevelClass.getType().getShortName()));
            for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
                String shortName = introspectedColumn.getFullyQualifiedJavaType().getShortName();
                if (!baseClassName.contains(shortName) && !wrapperClassName.contains(shortName) && !otherClassName.contains(shortName)) {
                    continue;
                }
                if (introspectedColumn.getDefaultValue() != null) {
                    String defaultValue = introspectedColumn.getDefaultValue();
                    //处理备注中带有类型描述情况，如 postgresql中存在 ''::character varying
                    if (defaultValue.matches("'\\.*'::\\w+(\\s\\w+)?")) {
                        //
                        defaultValue = defaultValue.substring(0, defaultValue.lastIndexOf("::"));
                    }
                    //去除前后'',如 '123456' -> 123456
                    if (defaultValue.startsWith("'") && defaultValue.endsWith("'")) {
                        if (defaultValue.length() == 2) {
                            defaultValue = "";
                        } else {
                            defaultValue = defaultValue.substring(1, defaultValue.length() - 1);
                        }
                    }
                    //暂不支持时间类型默认值识别,不同数据库表达式不同
                    if ("Boolean".equals(shortName) || "boolean".equals(shortName)) {
                        if ("0".equals(defaultValue)) {
                            defaultValue = "false";
                        } else if ("1".equals(defaultValue)) {
                            defaultValue = "true";
                        }
                    }

                    if ("String".equals(shortName)) {
                        //字符串,不通过new String 创建
                        // 其实通过new String 没有任何问题,不过强迫症,idea会提示,所以改了
                        defaultMethod.addBodyLine(String.format("instance.%s = \"%s\";", introspectedColumn.getJavaProperty(), defaultValue));
                    } else {
                        String javaProperty = introspectedColumn.getJavaProperty();
                        if (baseClassName.contains(shortName)) {
                            //基本类型,转成包装类的new 创建
                            javaProperty = wrapperClassName.get(baseClassName.indexOf(shortName));
                        }
                        //通过 new 方法转换
                        defaultMethod.addBodyLine(String.format("instance.%s = new %s(\"%s\");", javaProperty, shortName, defaultValue));
                    }
                }

            }
            defaultMethod.addBodyLine("return instance;");
            topLevelClass.addMethod(defaultMethod);
        }
    }

    /**
     * 如果需要生成Getter注解，就不需要生成get相关代码了
     */
    @Override
    public boolean modelGetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              ModelClassType modelClassType) {

        return !(this.needsData || this.needsGetter);
    }

    /**
     * 如果需要生成Setter注解，就不需要生成set相关代码了
     */
    @Override
    public boolean modelSetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              ModelClassType modelClassType) {
        return !(this.needsData || this.needsSetter);
    }

    /**
     * 生成基础实体类
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        processEntityClass(topLevelClass, introspectedTable);
        return true;
    }

    /**
     * 生成实体类注解KEY对象
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        processEntityClass(topLevelClass, introspectedTable);
        return true;
    }

    /**
     * 生成带BLOB字段的对象
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        processEntityClass(topLevelClass, introspectedTable);
        return false;
    }


    @Override
    public void setContext(Context context) {
        super.setContext(context);
        //设置默认的注释生成器
        useMapperCommentGenerator = !"FALSE".equalsIgnoreCase(context.getProperty("useMapperCommentGenerator"));
        if (useMapperCommentGenerator) {
            commentCfg = new CommentGeneratorConfiguration();
            commentCfg.setConfigurationType(SmartMapperCommentGenerator.class.getCanonicalName());
            context.setCommentGeneratorConfiguration(commentCfg);
        }
        //支持oracle获取注释#114
        context.getJdbcConnectionConfiguration().addProperty("remarksReporting", "true");
        //支持mysql获取注释
        context.getJdbcConnectionConfiguration().addProperty("useInformationSchema", "true");
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        String mappers = getProperty("mappers");
        if (StringUtility.stringHasValue(mappers)) {
            for (String mapper : mappers.split(",")) {
                this.mappers.add(mapper);
            }
        } else {
            throw new RuntimeException("Mapper插件缺少必要的mappers属性!");
        }
        this.caseSensitive = Boolean.parseBoolean(this.properties.getProperty("caseSensitive"));
        this.forceAnnotation = getPropertyAsBoolean("forceAnnotation");
        this.beginningDelimiter = getProperty("beginningDelimiter", "");
        this.endingDelimiter = getProperty("endingDelimiter", "");
        this.schema = getProperty("schema");
        //lombok扩展
        String lombok = getProperty("lombok");
        if (lombok != null && !"".equals(lombok)) {
            this.needsData = lombok.contains("Data");
            //@Data 优先级高于 @Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode
            this.needsGetter = !this.needsData && lombok.contains("Getter");
            this.needsSetter = !this.needsData && lombok.contains("Setter");
            this.needsToString = !this.needsData && lombok.contains("ToString");
            this.needsEqualsAndHashCode = !this.needsData && lombok.contains("EqualsAndHashCode");
            // 配置lombok扩展EqualsAndHashCode注解是否添加“callSuper = true”
            String lombokEqualsAndHashCodeCallSuper = getProperty("lombokEqualsAndHashCodeCallSuper", "false");
            this.needsEqualsAndHashCodeAndCallSuper = this.needsEqualsAndHashCode && "TRUE".equalsIgnoreCase(lombokEqualsAndHashCodeCallSuper);
            this.needsAccessors = lombok.contains("Accessors");
            this.needsSuperBuilder = lombok.contains("SuperBuilder");
            this.needsBuilder = !this.needsSuperBuilder && lombok.contains("Builder");
            this.needsNoArgsConstructor = lombok.contains("NoArgsConstructor");
            this.needsAllArgsConstructor = lombok.contains("AllArgsConstructor");
            this.needFieldNameConstants = lombok.contains("FieldNameConstants");
        }
        //swagger扩展
        String swagger = getProperty("swagger", "false");
        if ("TRUE".equalsIgnoreCase(swagger)) {
            this.needsSwagger = true;
        }
        this.generateColumnConsts = getPropertyAsBoolean("generateColumnConsts");
        this.generateDefaultInstanceMethod = getPropertyAsBoolean("generateDefaultInstanceMethod");
    }

    protected String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    protected String getProperty(String key, String defaultValue) {
        return this.properties.getProperty(key, defaultValue);
    }

    protected Boolean getPropertyAsBoolean(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }
}
