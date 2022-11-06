package cc.xfl12345.mybigdata.server.plugin.mybatis.org;

/*
 * Copyright (c) 2018.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import cc.xfl12345.mybigdata.server.common.api.OpenCloneable;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;

/**
 * ---------------------------------------------------------------------------
 * Cloneable
 * ---------------------------------------------------------------------------
 * @author xfl12345
 * ---------------------------------------------------------------------------
 */
public class ModelCloneablePlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.supportCloneable(topLevelClass);
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.supportCloneable(topLevelClass);
        return super.modelPrimaryKeyClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.supportCloneable(topLevelClass);
        return super.modelRecordWithBLOBsClassGenerated(topLevelClass, introspectedTable);
    }

    /**
     * 支持Cloneable
     */
    private void supportCloneable(TopLevelClass topLevelClass) {
        topLevelClass.addImportedType(new FullyQualifiedJavaType(OpenCloneable.class.getCanonicalName()));
        // implement
        topLevelClass.addSuperInterface(new FullyQualifiedJavaType(OpenCloneable.class.getCanonicalName()));
        // clone
        Method cloneMethod = new Method("clone");
        cloneMethod.setVisibility(JavaVisibility.PUBLIC);
        cloneMethod.setReturnType(topLevelClass.getType());
        cloneMethod.addAnnotation("@Override");
        cloneMethod.addException(new FullyQualifiedJavaType(CloneNotSupportedException.class.getCanonicalName()));
        cloneMethod.addBodyLine("return (" + topLevelClass.getType().getShortName() + ") super.clone();");
        topLevelClass.addMethod(cloneMethod);
    }
}
