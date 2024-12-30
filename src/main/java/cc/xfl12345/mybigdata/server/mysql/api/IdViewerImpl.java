package cc.xfl12345.mybigdata.server.mysql.api;

import cc.xfl12345.mybigdata.server.common.api.IdViewer;
import cc.xfl12345.mybigdata.server.common.appconst.AppDataType;
import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.data.source.DataSourceHome;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.BaseMbdObject;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;
import cc.xfl12345.mybigdata.server.common.pojo.FieldNotNullChecker;
import lombok.Getter;
import lombok.Setter;

import jakarta.annotation.PostConstruct;

public class IdViewerImpl implements IdViewer {
    @Getter
    @Setter
    protected FieldNotNullChecker fieldNotNullChecker = DefaultSingleton.FIELD_NOT_NULL_CHECKER;

    @Getter
    @Setter
    protected DataSourceHome dataSourceHome;

    @PostConstruct
    public void init() throws Exception {
        fieldNotNullChecker.check(dataSourceHome, "dataSourceHome");
    }

    @Override
    public AppDataType getDataTypeById(MbdId id) {
        return dataSourceHome.getDataTypeById(id);
    }

    @Override
    public BaseMbdObject getDataById(MbdId id, long recursionDepth) {
        return dataSourceHome.getMbdDataById(id, recursionDepth);
    }

}
