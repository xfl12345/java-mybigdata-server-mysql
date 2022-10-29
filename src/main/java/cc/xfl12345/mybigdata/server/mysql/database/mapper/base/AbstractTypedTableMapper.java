package cc.xfl12345.mybigdata.server.mysql.database.mapper.base;

import cc.xfl12345.mybigdata.server.common.appconst.AppDataType;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableBasicMapper;
import cc.xfl12345.mybigdata.server.mysql.appconst.EnumCoreTable;
import cc.xfl12345.mybigdata.server.mysql.pojo.ClassDeclaredInfo;
import cc.xfl12345.mybigdata.server.mysql.pojo.MapperPack;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;

public abstract class AbstractTypedTableMapper<Pojo>
    extends AbstractTableMapper
    implements TableBasicMapper<Pojo> {
    @Getter
    @Setter
    protected MapperPack<Pojo> mapperPack;

    protected MapperPack<Pojo> generateMapperPack() throws Exception {
        return generateMapperPack(this);
    }

    protected <T> MapperPack<T> generateMapperPack(TableBasicMapper<T> mapper) throws Exception {
        Class<T> pojoClass = mapper.getPojoType();
        ClassDeclaredInfo classDeclaredInfo = new ClassDeclaredInfo();
        classDeclaredInfo.setClazz(pojoClass);
        classDeclaredInfo.init();

        String databaseTableName = classDeclaredInfo.getJpaAnnotationByType(Table.class).get(0).name();
        EnumCoreTable enumCoreTable = EnumCoreTable.getByName(databaseTableName);
        AppDataType dataType = null;

        if (enumCoreTable != null) {
            switch (enumCoreTable) {
                case TABLE_SCHEMA_RECORD -> dataType = AppDataType.JsonSchema;
                case STRING_CONTENT -> dataType = AppDataType.String;
                case BOOLEAN_CONTENT -> dataType = AppDataType.Boolean;
                case NUMBER_CONTENT -> dataType = AppDataType.Number;
                case GROUP_RECORD -> dataType = AppDataType.Array;
                case OBJECT_RECORD -> dataType = AppDataType.Object;
            }
        }

        return MapperPack.<T>builder()
            .pojoClass(pojoClass)
            .dataType(dataType)
            .mapper(mapper)
            .coreTable(enumCoreTable)
            .classDeclaredInfo(classDeclaredInfo)
            .tableName(databaseTableName)
            .build();
    }
}
