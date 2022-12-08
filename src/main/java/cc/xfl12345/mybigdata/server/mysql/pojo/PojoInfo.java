package cc.xfl12345.mybigdata.server.mysql.pojo;

import cc.xfl12345.mybigdata.server.common.api.OpenCloneable;
import cc.xfl12345.mybigdata.server.common.appconst.AppDataType;
import cc.xfl12345.mybigdata.server.common.pojo.ClassDeclaredInfo;
import cc.xfl12345.mybigdata.server.mysql.appconst.EnumCoreTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.persistence.Table;

@NoArgsConstructor
@FieldNameConstants
@SuperBuilder
public class PojoInfo {
    @Getter
    @Setter
    protected Class<? extends OpenCloneable> pojoClass;

    @Getter
    @Setter
    protected AppDataType dataType;

    @Getter
    @Setter
    protected EnumCoreTable coreTable;

    @Getter
    @Setter
    protected ClassDeclaredInfo classDeclaredInfo;

    /**
     * 数据库里的表名
     */
    @Getter
    @Setter
    protected String tableName;

    private OpenCloneable emptyObject;

    public PojoInfo(Class<OpenCloneable> pojoClass) throws Exception {
        this.pojoClass = pojoClass;
        emptyObject = pojoClass.getDeclaredConstructor().newInstance();
        classDeclaredInfo = new ClassDeclaredInfo(emptyObject);

        tableName = classDeclaredInfo.getAnnotationByType(Table.class).get(0).name();
        coreTable = EnumCoreTable.getByName(tableName);

        if (coreTable != null) {
            switch (coreTable) {
                case TABLE_SCHEMA_RECORD -> dataType = AppDataType.JsonSchema;
                case STRING_CONTENT -> dataType = AppDataType.String;
                case BOOLEAN_CONTENT -> dataType = AppDataType.Boolean;
                case NUMBER_CONTENT -> dataType = AppDataType.Number;
                case GROUP_RECORD -> dataType = AppDataType.Array;
                case OBJECT_RECORD -> dataType = AppDataType.Object;
                default -> dataType = null;
            }
        }
    }

    public OpenCloneable getNewPoInstance() {
        try {
            return (OpenCloneable) emptyObject.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
