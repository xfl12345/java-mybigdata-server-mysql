package cc.xfl12345.mybigdata.server.mysql.appconst;

public enum CoreTables {
    GLOBAL_DATA_RECORD(CoreTableNames.GLOBAL_DATA_RECORD),
    TABLE_SCHEMA_RECORD(CoreTableNames.TABLE_SCHEMA_RECORD),

    STRING_CONTENT(CoreTableNames.STRING_CONTENT),
    BOOLEAN_CONTENT(CoreTableNames.BOOLEAN_CONTENT),
    NUMBER_CONTENT(CoreTableNames.NUMBER_CONTENT),

    GROUP_RECORD(CoreTableNames.GROUP_RECORD),
    GROUP_CONTENT(CoreTableNames.GROUP_CONTENT),

    OBJECT_RECORD(CoreTableNames.OBJECT_RECORD),
    OBJECT_CONTENT(CoreTableNames.OBJECT_CONTENT),

    AUTH_ACCOUNT(CoreTableNames.AUTH_ACCOUNT);

    CoreTables(String name) {
        this.name = name;
    }

    private final String name;

    public String getName() {
        return name;
    }
}
