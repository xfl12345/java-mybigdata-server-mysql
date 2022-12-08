package cc.xfl12345.mybigdata.server;

import cc.xfl12345.mybigdata.server.common.appconst.CommonConst;
import cc.xfl12345.mybigdata.server.database.pojo.SimpleColumnMeta;
import cc.xfl12345.mybigdata.server.database.pojo.schema.Columns;
import cc.xfl12345.mybigdata.server.database.pojo.schema.KeyColumnUsage;
import cc.xfl12345.mybigdata.server.mysql.spring.helper.JdbcContextFinalizer;
import cc.xfl12345.mybigdata.server.mysql.util.MysqlJdbcUrlBean;
import cc.xfl12345.mybigdata.server.pojo.DataSourceConfig;
import com.alibaba.druid.pool.DruidDataSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.conf.ConnectionUrl;
import lombok.extern.slf4j.Slf4j;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.MoreTable;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.ConditionImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class StudyBeeOrmAboutMysql {
    public static void main(String[] args) throws SQLException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        DataSourceConfig dataSourceConfig = TestLoadDataSource.getDataSourceConfig();
        MysqlJdbcUrlBean mysqlJdbcUrlBean = new MysqlJdbcUrlBean(
            ConnectionUrl.getConnectionUrlInstance(dataSourceConfig.getUrl(), null)
        );
        mysqlJdbcUrlBean.setDatabaseName(CommonConst.INFORMATION_SCHEMA_TABLE_NAME);
        dataSourceConfig.setUrl(mysqlJdbcUrlBean.buildURL());
        DruidDataSource dataSource = TestLoadDataSource.getDataSource(dataSourceConfig);

        BeeFactory beeFactory = BeeFactory.getInstance();
        beeFactory.setDataSource(dataSource);
        SuidRich suidRich = BeeFactory.getHoneyFactory().getSuidRich();

        String tableSchemaName = "mybigdata";
        // 查询 table name
        Condition tableNameCondition = new ConditionImpl();
        tableNameCondition.selectField(Columns.Fields.tableName);
        tableNameCondition.op(Columns.Fields.tableSchema, Op.eq, tableSchemaName);
        tableNameCondition.op(Columns.Fields.tableName, Op.notLike, "view\\_%");
        tableNameCondition.groupBy(Columns.Fields.tableName);
        List<String> tableNames = suidRich.select(new Columns(), tableNameCondition)
            .parallelStream().map(Columns::getTableName).toList();
        log.info("tableNames: " + tableNames + "\n".repeat(5));

        // 查询 各个 table 的 Column
        for (String tableName : tableNames) {
            Condition condition = new ConditionImpl();
            condition.op(Columns.Fields.tableSchema, Op.eq, tableSchemaName);
            condition.op(Columns.Fields.tableName, Op.eq, tableName);

            List<Columns> columns = suidRich.select(new Columns(), condition);
            List<KeyColumnUsage> keyColumnUsages = suidRich.select(new KeyColumnUsage(), condition);

            log.info("Table name: [" + tableName + "]. Table columns: "
                + objectMapper.valueToTree(columns).toPrettyString()
                + ", Table key usage: "
                + objectMapper.valueToTree(keyColumnUsages).toPrettyString()
                + "." + "\n".repeat(5)
            );
        }

        BeeFactory.getHoneyFactory().getMoreTable().select(
            SimpleColumnMeta.builder()
                .tableSchema(tableSchemaName)
                .keyColumnUsage(
                    KeyColumnUsage.builder().tableSchema(tableSchemaName).build()
                )
                .build()
            // new ConditionImpl().op(
            //     Columns.Fields.tableSchema ,
            //     Op.eq,
            //     tableSchemaName
            // )
        );



        JdbcContextFinalizer.deregister(null);
    }
}
