package cc.xfl12345.mybigdata.server.mysql.data.source;


import cc.xfl12345.mybigdata.server.common.data.source.NumberTypeSource;
import cc.xfl12345.mybigdata.server.common.data.source.impl.AbstractSingleTableDataSource;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.NumberContent;

import java.math.BigDecimal;

public class NumberTypeSourceImpl
    extends AbstractSingleTableDataSource<BigDecimal, NumberContent>
    implements NumberTypeSource {
    protected String[] selectContentFieldOnly = new String[]{NumberContent.Fields.content};

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
