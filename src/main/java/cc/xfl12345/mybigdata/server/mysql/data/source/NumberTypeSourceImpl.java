package cc.xfl12345.mybigdata.server.mysql.data.source;


import cc.xfl12345.mybigdata.server.common.data.source.NumberTypeSource;
import cc.xfl12345.mybigdata.server.common.data.source.impl.SingleTableDataSource;
import cc.xfl12345.mybigdata.server.mysql.database.constant.NumberContentConstant;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.NumberContentMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.AppTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.NumberContent;
import lombok.Setter;

import java.math.BigDecimal;

public class NumberTypeSourceImpl extends SingleTableDataSource<BigDecimal, NumberContent> implements NumberTypeSource {
    @Setter
    protected NumberContentMapper mapper;

    @Override
    public AppTableMapper<NumberContent> getMapper() {
        return mapper;
    }

    protected String[] selectContentFieldOnly = new String[]{NumberContentConstant.CONTENT};

    @Override
    protected String[] getSelectContentFieldOnly() {
        return selectContentFieldOnly;
    }

    protected boolean isInteger(BigDecimal value) {
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
    protected BigDecimal getValue(NumberContent pojo) {
        return new BigDecimal(pojo.getContent());
    }
}
