package cc.xfl12345.mybigdata.server.mysql.data.source.impl;


import cc.xfl12345.mybigdata.server.common.appconst.AppDataType;
import cc.xfl12345.mybigdata.server.common.data.source.DataSource;
import cc.xfl12345.mybigdata.server.common.data.source.IdDataSource;
import cc.xfl12345.mybigdata.server.common.data.source.StringTypeSource;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableMapper;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.AbstractBeeDoubleLayerTableDataSource;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.raw.bee.AbstractBeeDoubleLayerTableRawDataSource;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.StringContent;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;
import org.teasoft.bee.osql.Condition;

public class StringTypeSourceImpl
    extends AbstractBeeDoubleLayerTableDataSource<String, StringContent>
    implements StringTypeSource {
    @Override
    protected DataSource<String> generateRawImpl() {
        DataSource<?> myself = this;
        return new AbstractBeeDoubleLayerTableRawDataSource<String, StringContent>() {
            @Override
            public AppDataType getDataEnumType() {
                return myself.getDataEnumType();
            }

            @Override
            protected TableMapper<StringContent, Condition> getTableMapper() {
                return mapper;
            }

            private final String[] fieldNames4Select = new String[]{StringContent.Fields.content};

            @Override
            protected String[] getFieldNames4Select() {
                return fieldNames4Select;
            }

            @Override
            protected String getIdFieldName() {
                return StringContent.Fields.globalId;
            }

            @Override
            protected String getValueFieldName() {
                return StringContent.Fields.content;
            }

            @Override
            protected MbdId getId(StringContent stringContent) {
                return new MysqlMbdId(stringContent.getGlobalId());
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
            protected StringContent getPojo(MbdId globalId, String value) {
                return StringContent.builder()
                    .globalId(MysqlMbdId.getValue(globalId))
                    .content(value)
                    .build();
            }

            @Override
            protected MbdId getTableNameId(Class<?> pojoClass) {
                return coreTableCache.getTableNameId(pojoClass);
            }

            @Override
            protected IdDataSource getIdDataSource() {
                return idDataSource;
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
