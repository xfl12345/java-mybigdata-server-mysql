package cc.xfl12345.mybigdata.server.database.pojo.schema;

import java.io.Serializable;

/**
 * 表名：KEY_COLUMN_USAGE
*/
@lombok.Data
@lombok.experimental.SuperBuilder
@lombok.NoArgsConstructor
@lombok.experimental.FieldNameConstants
@jakarta.persistence.Table(name = "KEY_COLUMN_USAGE")
public class KeyColumnUsage implements Cloneable, Serializable {
    @jakarta.persistence.Column(name = "CONSTRAINT_CATALOG", nullable = false, length = 512)
    private String constraintCatalog;

    @jakarta.persistence.Column(name = "CONSTRAINT_SCHEMA", nullable = false, length = 64)
    private String constraintSchema;

    @jakarta.persistence.Column(name = "CONSTRAINT_NAME", nullable = false, length = 64)
    private String constraintName;

    @jakarta.persistence.Column(name = "TABLE_CATALOG", nullable = false, length = 512)
    private String tableCatalog;

    @jakarta.persistence.Column(name = "TABLE_SCHEMA", nullable = false, length = 64)
    private String tableSchema;

    @jakarta.persistence.Column(name = "TABLE_NAME", nullable = false, length = 64)
    private String tableName;

    @jakarta.persistence.Column(name = "COLUMN_NAME", nullable = false, length = 64)
    private String columnName;

    @jakarta.persistence.Column(name = "ORDINAL_POSITION", nullable = false)
    private Long ordinalPosition;

    @jakarta.persistence.Column(name = "POSITION_IN_UNIQUE_CONSTRAINT", nullable = true)
    private Long positionInUniqueConstraint;

    @jakarta.persistence.Column(name = "REFERENCED_TABLE_SCHEMA", nullable = true, length = 64)
    private String referencedTableSchema;

    @jakarta.persistence.Column(name = "REFERENCED_TABLE_NAME", nullable = true, length = 64)
    private String referencedTableName;

    @jakarta.persistence.Column(name = "REFERENCED_COLUMN_NAME", nullable = true, length = 64)
    private String referencedColumnName;

    private static final long serialVersionUID = 1L;

    @Override
    public KeyColumnUsage clone() throws CloneNotSupportedException {
        return (KeyColumnUsage) super.clone();
    }
}
