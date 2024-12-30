package cc.xfl12345.mybigdata.server.mysql.data.source.impl;


import cc.xfl12345.mybigdata.server.common.data.source.NumberTypeSource;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableMapper;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.AbstractBeeDoubleLayerTableDataSource;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.NumberContent;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;
import org.teasoft.bee.osql.api.Condition;
import org.teasoft.bee.osql.Op;
import org.teasoft.honey.osql.core.ConditionImpl;

import java.math.BigDecimal;
import java.util.List;

public class NumberTypeSourceImpl
    extends AbstractBeeDoubleLayerTableDataSource<BigDecimal, NumberContent>
    implements NumberTypeSource {

    @Override
    protected TableMapper<NumberContent, Condition> getTableMapper() {
        return mapper;
    }

    private final String[] fieldNames4Select = new String[]{NumberContent.Fields.content};

    @Override
    protected String[] getFieldNames4Select() {
        return fieldNames4Select;
    }

    @Override
    protected String getIdFieldName() {
        return NumberContent.Fields.globalId;
    }


    @Override
    protected Condition getSelectBatchIdCondition(List<BigDecimal> values) {
        return new ConditionImpl()
            .selectField(getIdFieldName())
            .op(
                NumberContent.Fields.content,
                Op.in,
                values.parallelStream().map(BigDecimal::toPlainString).toList()
            );
    }

    @Override
    protected MbdId getId(NumberContent numberContent) {
        return new MysqlMbdId(numberContent.getGlobalId());
    }

    private boolean isInteger(BigDecimal value) {
        return value.scale() <= 0;
    }

    @Override
    protected NumberContent getPojo(BigDecimal value) {
        NumberContent numberContent = new NumberContent();
        String numberInString = value.toPlainString();

        numberContent.setNumberisinteger(isInteger(value));
        numberContent.setNumberis64bit(
            numberContent.getNumberisinteger() && new BigDecimal(value.longValue()).compareTo(value) == 0
        );
        numberContent.setContent(numberInString);

        return numberContent;
    }

    @Override
    protected NumberContent getPojo(MbdId globalId, BigDecimal bigDecimal) {
        return NumberContent.builder()
            .globalId(MysqlMbdId.getValue(globalId))
            .content(bigDecimal.toPlainString())
            .build();
    }

    @Override
    protected MbdId getTableNameId(Class<?> pojoClass) {
        return coreTableCache.getTableNameId(pojoClass);
    }

    @Override
    protected BigDecimal getValue(NumberContent pojo) {
        return new BigDecimal(pojo.getContent());
    }

    @Override
    public Class<BigDecimal> getValueType() {
        return BigDecimal.class;
    }

    @Override
    protected Class<NumberContent> getPojoClass() {
        return NumberContent.class;
    }
}
