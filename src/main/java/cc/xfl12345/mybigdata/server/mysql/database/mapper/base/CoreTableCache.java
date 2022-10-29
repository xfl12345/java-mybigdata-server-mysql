package cc.xfl12345.mybigdata.server.mysql.database.mapper.base;

import cc.xfl12345.mybigdata.server.common.database.AbstractCoreTableCache;
import cc.xfl12345.mybigdata.server.common.pojo.TwoWayMap;
import cc.xfl12345.mybigdata.server.mysql.appconst.CoreTableNames;
import cc.xfl12345.mybigdata.server.mysql.appconst.EnumCoreTable;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.BooleanContent;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.StringContent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.teasoft.bee.osql.BeeException;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.bee.osql.transaction.Transaction;
import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.ConditionImpl;
import org.teasoft.honey.osql.core.SessionFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Slf4j
public class CoreTableCache extends AbstractCoreTableCache<Long, String> {

    public CoreTableCache() {
        tableNameCache = new TwoWayMap<>(EnumCoreTable.values().length + 1);
    }

    @Override
    public void init() throws Exception {
        if (jacksonObjectMapper == null) {
            jacksonObjectMapper = new ObjectMapper();
        }
        super.init();
    }

    @Getter
    @Setter
    protected ObjectMapper jacksonObjectMapper;

    @Override
    public void refreshBooleanCache() throws Exception {
        // 开启事务
        Transaction transaction = SessionFactory.getTransaction();
        try {
            transaction.begin();
            SuidRich suid = BeeFactory.getHoneyFactory().getSuidRich();

            // 查询数据
            List<BooleanContent> booleanContents = suid.select(new BooleanContent());
            for (BooleanContent booleanContent : booleanContents) {
                if (booleanContent.getContent()) {
                    idOfTrue = booleanContent.getGlobalId();
                } else {
                    idOfFalse = booleanContent.getGlobalId();
                }
            }

            transaction.commit();
        } catch (BeeException e) {
            log.error(e.getMessage());
            transaction.rollback();
            throw e;
        }
        log.info("Cache \"global_id\" for JSON constant - boolean value: true <---> " + idOfTrue);
        log.info("Cache \"global_id\" for JSON constant - boolean value: false <---> " + idOfFalse);
    }

    protected void refreshCoreTableNameCache(List<String> values) throws Exception {
        Condition condition = new ConditionImpl();
        condition.selectField(StringContent.Fields.globalId, StringContent.Fields.content)
            .op(StringContent.Fields.content, Op.in, values);

        // 开启事务
        Transaction transaction = SessionFactory.getTransaction();
        try {
            transaction.begin();
            SuidRich suid = BeeFactory.getHoneyFactory().getSuidRich();

            // 查询数据
            List<StringContent> contents = suid.select(new StringContent(), condition);
            if (contents.size() != values.size()) {
                HashSet<String> actuallyGet = new HashSet<>(contents.stream().map(StringContent::getContent).toList());
                HashSet<String> missing = new HashSet<>(values);
                missing.removeAll(actuallyGet);
                throw new IllegalArgumentException("We are looking for " + values.size() + " records. " +
                    "We get table name data in follow: " + jacksonObjectMapper.valueToTree(contents).toPrettyString() +
                    ". It is missing " + jacksonObjectMapper.valueToTree(missing).toPrettyString() + "."
                );
            }

            for (StringContent content : contents) {
                tableNameCache.put(content.getContent(), content.getGlobalId());
            }

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public void refreshCoreTableNameCache() throws Exception {
        refreshCoreTableNameCache(Arrays.stream(EnumCoreTable.values()).map(EnumCoreTable::getName).toList());
        log.info("Cache \"global_id\" for core table name: " +
            jacksonObjectMapper.valueToTree(tableNameCache.getKey2ValueMap()).toPrettyString()
        );
    }

    @Override
    protected String tableNameOfBoolean() {
        return CoreTableNames.BOOLEAN_CONTENT;
    }
}
