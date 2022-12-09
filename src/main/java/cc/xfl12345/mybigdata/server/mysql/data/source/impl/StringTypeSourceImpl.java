package cc.xfl12345.mybigdata.server.mysql.data.source.impl;


import cc.xfl12345.mybigdata.server.common.data.source.DataSource;
import cc.xfl12345.mybigdata.server.common.data.source.StringTypeSource;
import cc.xfl12345.mybigdata.server.common.pojo.MbdId;
import cc.xfl12345.mybigdata.server.mysql.appconst.CoreTableNames;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.AbstractBeeDoubleLayerTableDataSource;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.raw.bee.AbstractBeeDoubleLayerTableRawDataSource;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.StringContent;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;

public class StringTypeSourceImpl
    extends AbstractBeeDoubleLayerTableDataSource<String, StringContent>
    implements StringTypeSource {
    @Override
    protected DataSource<String> generateRawImpl() {
        return new AbstractBeeDoubleLayerTableRawDataSource<>(globalDataRecordDataSource, mapper) {
            private final String[] selectContentFieldOnly = new String[]{StringContent.Fields.content};

            @Override
            protected String[] getSelectContentFieldOnly() {
                return selectContentFieldOnly;
            }

            @Override
            protected String getValue(StringContent stringContent) {
                return stringContent.getContent();
            }

            @Override
            protected StringContent getPojo(String value) {
                StringContent stringContent = new StringContent();
                stringContent.setContent(value);
                return stringContent;
            }

            @Override
            protected StringContent getPojo(MbdId<?> globalId, String value) {
                return StringContent.builder()
                    .globalId(MysqlMbdId.getValue(globalId))
                    .content(value)
                    .build();
            }

            @Override
            protected String getTableName() {
                return CoreTableNames.STRING_CONTENT;
            }

            @Override
            protected String getPojoContentFieldName() {
                return StringContent.Fields.content;
            }

            // @Override
            // public Class<String> getValueType() {
            //     return String.class;
            // }
        };
    }

    @Override
    public Class<String> getValueType() {
        return String.class;
    }

    @Override
    protected Class<StringContent> getPojoClass() {
        return StringContent.class;
    }
}
