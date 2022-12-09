package cc.xfl12345.mybigdata.server.mysql.data.source.impl;


import cc.xfl12345.mybigdata.server.common.data.source.DataSource;
import cc.xfl12345.mybigdata.server.common.data.source.NumberTypeSource;
import cc.xfl12345.mybigdata.server.common.pojo.MbdId;
import cc.xfl12345.mybigdata.server.mysql.appconst.CoreTableNames;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.AbstractBeeDoubleLayerTableDataSource;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.raw.bee.AbstractBeeDoubleLayerTableRawDataSource;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.NumberContent;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;

import java.math.BigDecimal;

public class NumberTypeSourceImpl
    extends AbstractBeeDoubleLayerTableDataSource<BigDecimal, NumberContent>
    implements NumberTypeSource {
    @Override
    protected DataSource<BigDecimal> generateRawImpl() {
        return new AbstractBeeDoubleLayerTableRawDataSource<>(globalDataRecordDataSource, mapper) {
            private final String[] selectContentFieldOnly = new String[]{NumberContent.Fields.content};

            @Override
            protected String[] getSelectContentFieldOnly() {
                return selectContentFieldOnly;
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
            protected NumberContent getPojo(MbdId<?> globalId, BigDecimal bigDecimal) {
                return NumberContent.builder()
                    .globalId(MysqlMbdId.getValue(globalId))
                    .content(bigDecimal.toPlainString())
                    .build();
            }

            @Override
            protected BigDecimal getValue(NumberContent pojo) {
                return new BigDecimal(pojo.getContent());
            }

            @Override
            protected String getTableName() {
                return CoreTableNames.NUMBER_CONTENT;
            }

            @Override
            protected String getPojoContentFieldName() {
                return NumberContent.Fields.content;
            }
        };
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
