package cc.xfl12345.mybigdata.server.mysql.pojo;

import cc.xfl12345.mybigdata.server.common.appconst.AppDataType;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableBasicMapper;
import cc.xfl12345.mybigdata.server.mysql.appconst.EnumCoreTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@FieldNameConstants
@SuperBuilder
public class MapperPack<T> {
    @Getter
    @Setter
    protected Class<T> pojoClass;

    @Getter
    @Setter
    protected AppDataType dataType;

    @Getter
    @Setter
    protected TableBasicMapper<T> mapper;

    @Getter
    @Setter
    protected EnumCoreTable coreTable;

    @Getter
    @Setter
    protected ClassDeclaredInfo classDeclaredInfo;

    @Getter
    @Setter
    protected String tableName;
}
