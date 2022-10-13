package cc.xfl12345.mybigdata.server.mysql.appconst;

import java.util.HashMap;
import java.util.Map;

public enum EnumCoreTable {
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

    EnumCoreTable(String name) {
        this.name = name;
    }

    private final String name;

    public String getName() {
        return name;
    }

    private static final Map<String, EnumCoreTable> nameMap;

    static {
        nameMap = new HashMap<>(EnumCoreTable.values().length);
        for (EnumCoreTable item : EnumCoreTable.values()) {
            nameMap.put(item.getName(), item);
        }
    }

    public static EnumCoreTable getByName(String name) {
        return nameMap.get(name);
    }

}
