package cc.xfl12345.mybigdata.server.mysql.data.source;

import cc.xfl12345.mybigdata.server.common.appconst.AppDataType;
import cc.xfl12345.mybigdata.server.common.data.requirement.DataRequirementPack;
import cc.xfl12345.mybigdata.server.common.data.source.DataSourceHome;
import cc.xfl12345.mybigdata.server.common.data.source.IdDataSource;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.*;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.CoreTableCache;
import lombok.Getter;
import lombok.Setter;

public class DataSourceHomeImpl extends DataSourceHome {
    @Getter
    @Setter
    protected CoreTableCache coreTableCache;

    private IdDataSource idDataSource;

    @Override
    public void init() throws Exception {
        super.init();

        fieldNotNullChecker.check(coreTableCache, "coreTableCache");

        idDataSource = dataSourceBag.getIdDataSource();
    }

    @Override
    public AppDataType getDataTypeById(MbdId id) {
        return idDataSource.getDataEnumType(id);
    }

    @Override
    public MbdId getIdByData(BaseMbdObject data) {
        // TODO
        return null;
    }

    @Override
    public BaseMbdObject getDataById(MbdId id, DataRequirementPack dataRequirement) {
        BaseMbdObject result = null;
        if (dataRequirement == null) {
            AppDataType dataType = getDataTypeById(id);
            switch (dataType) {
                case Boolean -> {
                    PlainMbdBoolean mbdObject = new PlainMbdBoolean();
                    mbdObject.setGlobalId(new MbdId(id));
                    mbdObject.setValue(coreTableCache.getBooleanById(id));
                    result = mbdObject;
                }
                case String -> {
                    PlainMbdString mbdObject = new PlainMbdString();
                    mbdObject.setGlobalId(new MbdId(id));
                    mbdObject.setValue(dataSourceBag.getStringTypeSource().selectById(id));
                    result = mbdObject;
                }
                case Number -> {
                    PlainMbdNumber mbdObject = new PlainMbdNumber();
                    mbdObject.setGlobalId(new MbdId(id));
                    mbdObject.setValue(dataSourceBag.getNumberTypeSource().selectById(id));
                    result = mbdObject;
                }
                case Array -> {
                    result = dataSourceBag.getGroupTypeSource().selectById(id);
                }
                case Object -> {
                    result = dataSourceBag.getObjectTypeSource().selectById(id);
                }
                case JsonSchema -> {
                    result = dataSourceBag.getJsonSchemaSource().selectById(id);
                }
                default -> {}
            }
        }

        // TODO
        return result;
    }

    @Override
    public MbdId setData(BaseMbdObject data) {
        // TODO
        return null;
    }
}
