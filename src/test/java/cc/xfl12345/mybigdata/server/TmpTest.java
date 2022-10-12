package cc.xfl12345.mybigdata.server;

import cc.xfl12345.mybigdata.server.mysql.appconst.CoreTableNames;
import cc.xfl12345.mybigdata.server.mysql.appconst.CoreTables;

public class TmpTest {
    public static void main(String[] args) {
        System.out.println(CoreTables.getByName(CoreTableNames.GLOBAL_DATA_RECORD));
    }
}
