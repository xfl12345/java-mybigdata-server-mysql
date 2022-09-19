package cc.xfl12345.mybigdata.server.mysql.mapper;

import cc.xfl12345.mybigdata.server.common.data.condition.SingleTableCondition;
import cc.xfl12345.mybigdata.server.common.web.mapper.AdvanceSearchMapper;
import cc.xfl12345.mybigdata.server.mysql.database.constant.NumberContentConstant;
import cc.xfl12345.mybigdata.server.mysql.database.constant.StringContentConstant;
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
import java.util.ArrayList;
import java.util.List;

public class AdvanceSearchMapperImpl implements AdvanceSearchMapper {
    @Override
    public List<String> selectStringByPrefix(String prefix) {
        List<String> result = null;

        Condition condition = new ConditionImpl();
        condition.selectField(StringContentConstant.CONTENT)
            .op(StringContentConstant.CONTENT, Op.likeRight, prefix);

        Transaction transaction = SessionFactory.getTransaction();
        try {
            transaction.begin();

            SuidRich suid = BeeFactory.getHoneyFactory().getSuidRich();
            List<StringContent> contents = suid.select(new StringContent(), condition);

            result = contents.parallelStream().map(StringContent::getContent).toList();
        } catch (Exception e) {
            transaction.rollback();
            return new ArrayList<>();
        }

        return result;
    }

    @Override
    public List<BigDecimal> selectNumberByPrefix(String prefix) {
        List<BigDecimal> result = null;

        Condition condition = new ConditionImpl();
        condition.selectField(NumberContentConstant.CONTENT)
            .op(NumberContentConstant.CONTENT, Op.likeRight, prefix);

        Transaction transaction = SessionFactory.getTransaction();
        try {
            transaction.begin();

            SuidRich suid = BeeFactory.getHoneyFactory().getSuidRich();
            List<NumberContent> contents = suid.select(new NumberContent(), condition);

            result = contents.parallelStream().map(item -> new BigDecimal(item.getContent())).toList();
        } catch (Exception e) {
            transaction.rollback();
            return new ArrayList<>();
        }

        return result;
    }

    @Override
    public List<BigDecimal> selectNumberByPrefix(Integer prefix) {
        return selectNumberByPrefix(prefix.toString());
    }

    @Override
    public List<Object> selectByCondition(SingleTableCondition condition) {
        return null;
    }
}
