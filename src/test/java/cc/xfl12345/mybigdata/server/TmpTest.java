package cc.xfl12345.mybigdata.server;

import cc.xfl12345.mybigdata.server.common.pojo.PropertyHelper;
import cc.xfl12345.mybigdata.server.mysql.appconst.CoreTableNames;
import cc.xfl12345.mybigdata.server.mysql.appconst.EnumCoreTable;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TmpTest {
    public static void main(String[] args) throws Exception {
        System.out.println(EnumCoreTable.getByName(CoreTableNames.GLOBAL_DATA_RECORD));


        BeanInfo beanInfo = Introspector.getBeanInfo(GlobalDataRecord.class, GlobalDataRecord.class.getSuperclass());

        PropertyHelper propertyHelper = new PropertyHelper(
            new GlobalDataRecord(),
            beanInfo.getPropertyDescriptors()[0]
        );

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
        JsonNode jsonNode = objectMapper.valueToTree(
            GlobalDataRecord.builder().createTime(new Date()).build().clone()
        );
        System.out.println(jsonNode.toPrettyString());

    }
}
