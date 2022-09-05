package cc.xfl12345.mybigdata.server.plugin.mybatis.org;

import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.HashSet;
import java.util.Set;

public class AnnotationUtil {
    @SuppressWarnings("unchecked")
    public static void justAddAnnotation2Field(
        TopLevelClass topLevelClass,
        Field field,
        Class<?> annotationClass,
        String innerCode) {
        String annotationClassCanonicalName = annotationClass.getCanonicalName();
        String annotationClassSimpleName = annotationClass.getSimpleName();
        String annotationClassPackageName = annotationClass.getPackageName();
        HashSet<String> annotationClassPackageImportNames = new HashSet<>();
        String[] annotationClassPackageNameParts = annotationClassPackageName.split("\\.");
        for (int i = 0; i < annotationClassPackageNameParts.length; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(annotationClassPackageNameParts[0]);
            for (int j = 1; j <= i; j++) {
                stringBuilder.append('.').append(annotationClassPackageNameParts[j]);
            }
            annotationClassPackageImportNames.add(stringBuilder + ".*");
        }

        String javaCodeWithCanonicalName = "@" + annotationClassCanonicalName;
        String javaCodeWithSimpleName = "@" + annotationClassSimpleName;
        boolean isImported = false;

        Set<FullyQualifiedJavaType> importedTypes = topLevelClass.getImportedTypes();
        for (FullyQualifiedJavaType javaType : importedTypes) {
            HashSet<String> importeds = new HashSet<>(javaType.getImportList());
            HashSet<String> intersection = (HashSet<String>) annotationClassPackageImportNames.clone();
            intersection.retainAll(importeds);
            if (importeds.contains(annotationClassCanonicalName) || intersection.size() > 0) {
                isImported = true;
            }
        }

        if (innerCode == null || "".equals(innerCode)) {
            innerCode = "";
        } else {
            innerCode = "(" + innerCode + ")";
        }

        if (isImported) {
            if (!(field.getAnnotations().contains(javaCodeWithSimpleName) ||
                field.getAnnotations().contains(javaCodeWithCanonicalName))) {
                field.addAnnotation(javaCodeWithSimpleName + innerCode);
            }
        } else {
            if (!field.getAnnotations().contains(javaCodeWithCanonicalName)) {
                field.addAnnotation(javaCodeWithCanonicalName + innerCode);
            }
        }

    }

    @SuppressWarnings("unchecked")
    public static void justAddAnnotation2Class(
        TopLevelClass topLevelClass,
        Class<?> annotationClass,
        String innerCode) {
        String annotationClassCanonicalName = annotationClass.getCanonicalName();
        String annotationClassSimpleName = annotationClass.getSimpleName();
        String annotationClassPackageName = annotationClass.getPackageName();
        HashSet<String> annotationClassPackageImportNames = new HashSet<>();
        String[] annotationClassPackageNameParts = annotationClassPackageName.split("\\.");
        for (int i = 0; i < annotationClassPackageNameParts.length; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(annotationClassPackageNameParts[0]);
            for (int j = 1; j <= i; j++) {
                stringBuilder.append('.').append(annotationClassPackageNameParts[j]);
            }
            annotationClassPackageImportNames.add(stringBuilder + ".*");
        }

        String javaCodeWithCanonicalName = "@" + annotationClassCanonicalName;
        String javaCodeWithSimpleName = "@" + annotationClassSimpleName;
        boolean isImported = false;

        Set<FullyQualifiedJavaType> importedTypes = topLevelClass.getImportedTypes();
        for (FullyQualifiedJavaType javaType : importedTypes) {
            HashSet<String> importeds = new HashSet<>(javaType.getImportList());
            HashSet<String> intersection = (HashSet<String>) annotationClassPackageImportNames.clone();
            intersection.retainAll(importeds);
            if (importeds.contains(annotationClassCanonicalName) || intersection.size() > 0) {
                isImported = true;
            }
        }

        if (innerCode == null || "".equals(innerCode)) {
            innerCode = "";
        } else {
            innerCode = "(" + innerCode + ")";
        }

        // HashSet<? extends Class<? extends Annotation>> targetClassAnnotations = (HashSet<? extends Class<? extends Annotation>>) Arrays.stream(targetClass.getAnnotations()).parallel().map(Annotation::getClass).collect(Collectors.toSet());


        if (isImported) {
            if (!(topLevelClass.getAnnotations().contains(javaCodeWithSimpleName) ||
                topLevelClass.getAnnotations().contains(javaCodeWithCanonicalName))) {
                topLevelClass.addAnnotation(javaCodeWithCanonicalName + innerCode);
            }
        } else {
            if (!topLevelClass.getAnnotations().contains(javaCodeWithCanonicalName)) {
                topLevelClass.addAnnotation(javaCodeWithCanonicalName + innerCode);
            }
        }

    }
}
