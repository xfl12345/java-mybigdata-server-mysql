package cc.xfl12345.mybigdata.server.database.pojo;

import cc.xfl12345.mybigdata.server.database.pojo.schema.Columns;
import cc.xfl12345.mybigdata.server.database.pojo.schema.KeyColumnUsage;
import org.teasoft.bee.osql.annotation.JoinTable;
import org.teasoft.bee.osql.annotation.JoinType;


@lombok.Data
@lombok.experimental.SuperBuilder
@lombok.NoArgsConstructor
@lombok.experimental.FieldNameConstants
@io.swagger.annotations.ApiModel("")
@jakarta.persistence.Table(name = "COLUMNS")
@org.teasoft.bee.osql.annotation.Table("COLUMNS")
public class SimpleColumnMeta {
    @jakarta.persistence.Column(name = "TABLE_SCHEMA", nullable = false, length = 64)
    private String tableSchema;

    @jakarta.persistence.Column(name = "TABLE_NAME", nullable = false, length = 64)
    private String tableName;

    @jakarta.persistence.Column(name = "COLUMN_NAME", nullable = false, length = 64)
    private String columnName;

    @jakarta.persistence.Column(name = "ORDINAL_POSITION", nullable = false)
    private Long ordinalPosition;

    @jakarta.persistence.Column(name = "DATA_TYPE", nullable = false, length = 64)
    private String dataType;

    // @OneToOne(targetEntity = KeyColumnUsage.class)
    // @JoinColumn(
    //     name = "CONSTRAINT_NAME",
    //     referencedColumnName = "CONSTRAINT_NAME",
    //     table = "KEY_COLUMN_USAGE"
    // )
    // protected String constraintName;
    //
    // @OneToOne(targetEntity = KeyColumnUsage.class)
    // @JoinColumn(
    //     name = "REFERENCED_TABLE_NAME",
    //     referencedColumnName = "REFERENCED_TABLE_NAME",
    //     table = "KEY_COLUMN_USAGE"
    // )
    // protected String referencedTableName;
    //
    // @OneToOne(targetEntity = KeyColumnUsage.class)
    // @JoinColumn(
    //     name = "REFERENCED_COLUMN_NAME",
    //     referencedColumnName = "REFERENCED_COLUMN_NAME",
    //     table = "KEY_COLUMN_USAGE"
    // )
    // protected String referencedColumnName;

    @JoinTable(
        mainField = Columns.Fields.columnName,
        subField = KeyColumnUsage.Fields.columnName,
        joinType = JoinType.LEFT_JOIN,
        subClazz = KeyColumnUsage.class
    )
    private KeyColumnUsage keyColumnUsage;
}
