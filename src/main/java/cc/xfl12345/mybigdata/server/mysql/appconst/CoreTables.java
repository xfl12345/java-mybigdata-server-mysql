package cc.xfl12345.mybigdata.server.mysql.appconst;

import java.util.HashMap;
import java.util.Map;

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

    private static final Map<String, CoreTables> nameMap;

    static {
        nameMap = new HashMap<>(CoreTables.values().length);
        for (CoreTables item : CoreTables.values()) {
            nameMap.put(item.getName(), item);
        }
    }

    public static CoreTables getByName(String name) {
        return nameMap.get(name);
    }

}
