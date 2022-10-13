package cc.xfl12345.mybigdata.server.mysql.data.source;


import cc.xfl12345.mybigdata.server.common.data.source.StringTypeSource;
import cc.xfl12345.mybigdata.server.common.data.source.impl.AbstractSingleTableDataSource;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.StringContent;

public class StringTypeSourceImpl
    extends AbstractSingleTableDataSource<String, StringContent>
    implements StringTypeSource {
    protected String[] selectContentFieldOnly = new String[]{StringContent.Fields.content};

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
