package cc.xfl12345.mybigdata.server;

import cc.xfl12345.mybigdata.server.common.data.handler.StringTypeHandler;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.CoreTableCache;
import cc.xfl12345.mybigdata.server.mysql.spring.helper.JdbcContextFinalizer;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.teasoft.honey.osql.core.BeeFactory;

public class TestDataHandler {
    public static void main(String[] args) throws Exception {
        DruidDataSource dataSource = TestLoadDataSource.getDataSource();
        BeeFactory beeFactory = BeeFactory.getInstance();
        beeFactory.setDataSource(dataSource);

        CoreTableCache coreTableCache = new CoreTableCache();



        StringTypeHandler stringTypeHandler = new StringTypeHandler();
        // stringTypeHandler.setUuidGenerator(Generators.timeBasedGenerator());
        // stringTypeHandler.setCoreTableCache(coreTableCache);
        // stringTypeHandler.afterPropertiesSet();

        // printJSON(stringTypeHandler.selectStringByPrefix("t"));

        // StringTypeResult stringTypeResult = stringTypeHandler.selectStringByFullText("text", null);
        // printJSON(stringTypeResult);
        // if (stringTypeResult.getSimpleResult().equals(SimpleCoreTableCurdResult.SUCCEED)) {
        //     printJSON(stringTypeHandler.updateStringByGlobalId("text666", stringTypeResult.getStringContent().getGlobalId()));
        //     printJSON(stringTypeHandler.updateStringByFullText("text666", "text"));
        // }


        // stringTypeHandler.destroy();

        JdbcContextFinalizer.deregister(null);
    }

    public static void printJSON(Object obj) {
        System.out.println(JSON.toJSONString(obj, JSONWriter.Feature.PrettyFormat));
    }
}
