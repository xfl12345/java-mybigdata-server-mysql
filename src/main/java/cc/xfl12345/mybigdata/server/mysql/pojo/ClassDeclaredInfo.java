package cc.xfl12345.mybigdata.server.mysql.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.persistence.Column;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiConsumer;

public class ClassDeclaredInfo {
    @Getter
    @Setter
    protected Class<?> clazz;

    @Getter
    protected Map<String, Field> declaredFields;

    /**
     * fieldName -> Annotation Type -> Annotation List
     */
    @Getter
    protected Map<String, Map<Class<?>, List<Annotation>>> fieldsAnnotations;

    @Getter
    protected Map<Class<?>, List<Annotation>> clazzAnnotations;

    @Getter
    protected Map<Class<?>, List<Annotation>> class2JpaAnnotationMap;

    @Getter
    protected Map<Annotation, Field> annotation2FieldMap;

    @Getter
    protected Set<Annotation> jpaColumns;

    @Getter
    protected BeanInfo beanInfo;

    /**
     * propertyName -> PropertyDescriptor
     */
    @Getter
    protected Map<String, PropertyDescriptor> propertiesMap;

    public ClassDeclaredInfo() {
    }

    @PostConstruct
    public void init() throws Exception {
        declaredFields = new ConcurrentHashMap<>();
        fieldsAnnotations = new ConcurrentHashMap<>();
        clazzAnnotations = new ConcurrentHashMap<>();
        class2JpaAnnotationMap = new ConcurrentHashMap<>();
        annotation2FieldMap = new HashMap<>();
        jpaColumns = new CopyOnWriteArraySet<>();

        beanInfo = Introspector.getBeanInfo(clazz, clazz.getSuperclass());

        propertiesMap = new HashMap<>();
        for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
            propertiesMap.put(descriptor.getDisplayName(), descriptor);
        }

        BiConsumer<Map<Class<?>, List<Annotation>>, Annotation> annotationHandler = (annotationMap, annotation) -> {
            // 获取类型
            Class<?> annotationClass = annotation.annotationType();
            // 初始化 & 获取 & 加入 List
            annotationMap.putIfAbsent(annotationClass, new CopyOnWriteArrayList<>());
            List<Annotation> annotationList = annotationMap.get(annotationClass);
            annotationList.add(annotation);
            // 识别类型
            String annotationName = annotationClass.getCanonicalName();
            if (annotationName.startsWith("javax.persistence.")) {
                class2JpaAnnotationMap.putIfAbsent(annotationClass, new CopyOnWriteArrayList<>());
                List<Annotation> jpaAnnotationList = class2JpaAnnotationMap.get(annotationClass);
                jpaAnnotationList.add(annotation);
            }
        };


        // 遍历 Class 上的注解
        Arrays.asList(clazz.getDeclaredAnnotations()).parallelStream().forEach(annotation -> {
            annotationHandler.accept(clazzAnnotations, annotation);
        });

        // 遍历 字段
        Arrays.asList(clazz.getDeclaredFields()).parallelStream().forEach(field -> {
            String fieldName = field.getName();
            declaredFields.put(fieldName, field);

            Map<Class<?>, List<Annotation>> annotationMap = new ConcurrentHashMap<>();
            fieldsAnnotations.put(fieldName, annotationMap);

            // 遍历 字段 上的注解
            Annotation[] annotations = field.getDeclaredAnnotations();
            Arrays.asList(annotations).parallelStream().forEach(annotation -> {
                annotationHandler.accept(annotationMap, annotation);
                annotation2FieldMap.put(annotation, field);
                if (annotation instanceof Column) {
                    jpaColumns.add(annotation);
                }
            });
        });
    }

    // protected <T> void annotationHandler(Map<Class<?>, List<Annotation>> annotationMap, Annotation annotation, String type) {
    //     // 获取类型
    //     Class<?> annotationClass = annotation.annotationType();
    //     // 初始化 & 获取 & 加入 List
    //     annotationMap.putIfAbsent(annotationClass, new CopyOnWriteArrayList<>());
    //     List<Annotation> annotationList = annotationMap.get(annotationClass);
    //     annotationList.add(annotation);
    //     // 识别类型
    //     String annotationName = annotationClass.getCanonicalName();
    //     if (annotationName.startsWith("javax.persistence.")) {
    //         class2JpaAnnotationMap.putIfAbsent(annotationClass, new CopyOnWriteArrayList<>());
    //         List<Annotation> jpaAnnotationList = class2JpaAnnotationMap.get(annotationClass);
    //         jpaAnnotationList.add(annotation);
    //
    //         switch (type) {
    //             case "Field":
    //                 if (annotation instanceof Column) {
    //                     jpaColumns.add(annotation);
    //                 }
    //                 break;
    //             case "Class":
    //                 if (annotation instanceof Table) {
    //                     jpaColumns.add(annotation);
    //                 }
    //                 break;
    //             default: break;
    //         }
    //     }
    // }

    @SuppressWarnings("unchecked")
    public <T> List<T> getJpaAnnotationByType(Class<T> jpaAnnotationType) {
        return (List<T>) class2JpaAnnotationMap.get(jpaAnnotationType);
    }
}
