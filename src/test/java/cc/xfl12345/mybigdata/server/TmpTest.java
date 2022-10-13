package cc.xfl12345.mybigdata.server;

import cc.xfl12345.mybigdata.server.mysql.appconst.CoreTableNames;
import cc.xfl12345.mybigdata.server.mysql.appconst.EnumCoreTable;

public class TmpTest {
    public static void main(String[] args) {
        System.out.println(EnumCoreTable.getByName(CoreTableNames.GLOBAL_DATA_RECORD));
    }
}
