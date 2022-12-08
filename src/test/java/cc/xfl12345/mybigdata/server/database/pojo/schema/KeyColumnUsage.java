package cc.xfl12345.mybigdata.server.database.pojo.schema;

import java.io.Serializable;

/**
 * 表名：KEY_COLUMN_USAGE
*/
@lombok.Data
@lombok.experimental.SuperBuilder
@lombok.NoArgsConstructor
@lombok.experimental.FieldNameConstants
@javax.persistence.Table(name = "KEY_COLUMN_USAGE")
public class KeyColumnUsage implements Cloneable, Serializable {
    @javax.persistence.Column(name = "CONSTRAINT_CATALOG", nullable = false, length = 512)
    private String constraintCatalog;

    @javax.persistence.Column(name = "CONSTRAINT_SCHEMA", nullable = false, length = 64)
    private String constraintSchema;

    @javax.persistence.Column(name = "CONSTRAINT_NAME", nullable = false, length = 64)
    private String constraintName;

    @javax.persistence.Column(name = "TABLE_CATALOG", nullable = false, length = 512)
    private String tableCatalog;

    @javax.persistence.Column(name = "TABLE_SCHEMA", nullable = false, length = 64)
    private String tableSchema;

    @javax.persistence.Column(name = "TABLE_NAME", nullable = false, length = 64)
    private String tableName;

    @javax.persistence.Column(name = "COLUMN_NAME", nullable = false, length = 64)
    private String columnName;

    @javax.persistence.Column(name = "ORDINAL_POSITION", nullable = false)
    private Long ordinalPosition;

    @javax.persistence.Column(name = "POSITION_IN_UNIQUE_CONSTRAINT", nullable = true)
    private Long positionInUniqueConstraint;

    @javax.persistence.Column(name = "REFERENCED_TABLE_SCHEMA", nullable = true, length = 64)
    private String referencedTableSchema;

    @javax.persistence.Column(name = "REFERENCED_TABLE_NAME", nullable = true, length = 64)
    private String referencedTableName;

    @javax.persistence.Column(name = "REFERENCED_COLUMN_NAME", nullable = true, length = 64)
    private String referencedColumnName;

    private static final long serialVersionUID = 1L;

    @Override
    public KeyColumnUsage clone() throws CloneNotSupportedException {
        return (KeyColumnUsage) super.clone();
    }
}