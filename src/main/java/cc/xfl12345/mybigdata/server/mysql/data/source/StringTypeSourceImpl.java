package cc.xfl12345.mybigdata.server.mysql.data.source;


import cc.xfl12345.mybigdata.server.common.data.source.StringTypeSource;
import cc.xfl12345.mybigdata.server.common.data.source.impl.SingleTableDataSource;
import cc.xfl12345.mybigdata.server.mysql.database.constant.StringContentConstant;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.StringContentMapper;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.AppTableMapper;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.StringContent;
import lombok.Setter;

public class StringTypeSourceImpl extends SingleTableDataSource<String, StringContent> implements StringTypeSource {
    @Setter
    protected StringContentMapper mapper;

    @Override
    public AppTableMapper<StringContent> getMapper() {
        return mapper;
    }

    protected String[] selectContentFieldOnly = new String[]{StringContentConstant.CONTENT};

    @Override
    protected String[] getSelectContentFieldOnly() {
        return selectContentFieldOnly;
    }

    @Override
    protected StringContent getPojo(String s) {
        StringContent stringContent = new StringContent();
        stringContent.setContent(s);
        return stringContent;
    }

    @Override
    protected String getValue(StringContent stringContent) {
        return stringContent.getContent();
    }
}
