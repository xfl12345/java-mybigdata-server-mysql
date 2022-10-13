package cc.xfl12345.mybigdata.server.mysql.api;

import cc.xfl12345.mybigdata.server.common.api.AdvanceSearchMapper;
import cc.xfl12345.mybigdata.server.common.data.condition.SingleTableCondition;
import cc.xfl12345.mybigdata.server.common.pojo.IdAndValue;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.NumberContent;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.StringContent;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.bee.osql.transaction.Transaction;
import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.ConditionImpl;
import org.teasoft.honey.osql.core.SessionFactory;

import java.math.BigDecimal;
import java.util.List;

public class AdvanceSearchMapperImpl implements AdvanceSearchMapper {
    @Override
    public List<IdAndValue<String>> selectStringByPrefix(String prefix) {
        List<IdAndValue<String>> result = null;

        Condition condition = new ConditionImpl();
        condition
            .selectField(StringContent.Fields.globalId, StringContent.Fields.content)
            .op(StringContent.Fields.content, Op.likeRight, prefix);

        Transaction transaction = SessionFactory.getTransaction();
        try {
            transaction.begin();

            SuidRich suid = BeeFactory.getHoneyFactory().getSuidRich();
            List<StringContent> contents = suid.select(new StringContent(), condition);

            result = contents.parallelStream().map(item -> {
                IdAndValue<String> idAndValue = new IdAndValue<>();
                idAndValue.id = item.getGlobalId();
                idAndValue.value = item.getContent();
                return idAndValue;
            }).toList();

            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }

        return result;
    }

    @Override
    public List<IdAndValue<BigDecimal>> selectNumberByPrefix(String prefix) {
        List<IdAndValue<BigDecimal>> result = null;

        Condition condition = new ConditionImpl();
        condition
            .selectField(NumberContent.Fields.globalId, NumberContent.Fields.content)
            .op(NumberContent.Fields.content, Op.likeRight, prefix);

        Transaction transaction = SessionFactory.getTransaction();
        try {
            transaction.begin();

            SuidRich suid = BeeFactory.getHoneyFactory().getSuidRich();
            List<NumberContent> contents = suid.select(new NumberContent(), condition);

            result = contents.parallelStream().map(item -> {
                IdAndValue<BigDecimal> idAndValue = new IdAndValue<>();
                idAndValue.id = item.getGlobalId();
                idAndValue.value = new BigDecimal(item.getContent());
                return idAndValue;
            }).toList();

            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }

        return result;
    }

    @Override
    public List<IdAndValue<BigDecimal>> selectNumberByPrefix(Integer prefix) {
        return selectNumberByPrefix(prefix.toString());
    }

    @Override
    public List<Object> selectByCondition(SingleTableCondition condition) {
        return null;
    }
}
