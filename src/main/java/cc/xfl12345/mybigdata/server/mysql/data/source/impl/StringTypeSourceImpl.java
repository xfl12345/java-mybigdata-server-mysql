package cc.xfl12345.mybigdata.server.mysql.data.source.impl;


import cc.xfl12345.mybigdata.server.common.data.source.StringTypeSource;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableMapper;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.AbstractBeeDoubleLayerTableDataSource;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.StringContent;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;
import org.teasoft.bee.osql.api.Condition;
import org.teasoft.bee.osql.Op;
import org.teasoft.honey.osql.core.ConditionImpl;

import java.util.List;

public class StringTypeSourceImpl
    extends AbstractBeeDoubleLayerTableDataSource<String, StringContent>
    implements StringTypeSource {

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
    protected MbdId getId(StringContent stringContent) {
        return new MysqlMbdId(stringContent.getGlobalId());
    }

    @Override
    protected Condition getSelectBatchIdCondition(List<String> values) {
        return new ConditionImpl().selectField(getIdFieldName()).op(StringContent.Fields.content, Op.in, values);
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
    public Class<String> getValueType() {
        return String.class;
    }

    @Override
    protected Class<StringContent> getPojoClass() {
        return StringContent.class;
    }
}
