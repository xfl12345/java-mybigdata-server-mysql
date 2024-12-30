package cc.xfl12345.mybigdata.server.database.pojo.schema;

import java.io.Serializable;

/**
 * 表名：COLUMNS
*/
@lombok.Data
@lombok.experimental.SuperBuilder
@lombok.NoArgsConstructor
@lombok.experimental.FieldNameConstants
@jakarta.persistence.Table(name = "COLUMNS")
@org.teasoft.bee.osql.annotation.Table("COLUMNS")
public class Columns implements Cloneable, Serializable {
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

    @jakarta.persistence.Column(name = "IS_NULLABLE", nullable = false, length = 3)
    private String isNullable;

    @jakarta.persistence.Column(name = "DATA_TYPE", nullable = false, length = 64)
    private String dataType;

    @jakarta.persistence.Column(name = "CHARACTER_MAXIMUM_LENGTH", nullable = true)
    private Long characterMaximumLength;

    @jakarta.persistence.Column(name = "CHARACTER_OCTET_LENGTH", nullable = true)
    private Long characterOctetLength;

    @jakarta.persistence.Column(name = "NUMERIC_PRECISION", nullable = true)
    private Long numericPrecision;

    @jakarta.persistence.Column(name = "NUMERIC_SCALE", nullable = true)
    private Long numericScale;

    @jakarta.persistence.Column(name = "DATETIME_PRECISION", nullable = true)
    private Long datetimePrecision;

    @jakarta.persistence.Column(name = "CHARACTER_SET_NAME", nullable = true, length = 32)
    private String characterSetName;

    @jakarta.persistence.Column(name = "COLLATION_NAME", nullable = true, length = 32)
    private String collationName;

    @jakarta.persistence.Column(name = "COLUMN_KEY", nullable = false, length = 3)
    private String columnKey;

    @jakarta.persistence.Column(name = "EXTRA", nullable = false, length = 30)
    private String extra;

    @jakarta.persistence.Column(name = "PRIVILEGES", nullable = false, length = 80)
    private String privileges;

    @jakarta.persistence.Column(name = "COLUMN_COMMENT", nullable = false, length = 1024)
    private String columnComment;

    @jakarta.persistence.Column(name = "COLUMN_DEFAULT", nullable = true, length = 2147483647)
    private String columnDefault;

    @jakarta.persistence.Column(name = "COLUMN_TYPE", nullable = false, length = 2147483647)
    private String columnType;

    @jakarta.persistence.Column(name = "GENERATION_EXPRESSION", nullable = false, length = 2147483647)
    private String generationExpression;

    private static final long serialVersionUID = 1L;

    @Override
    public Columns clone() throws CloneNotSupportedException {
        return (Columns) super.clone();
    }
}
