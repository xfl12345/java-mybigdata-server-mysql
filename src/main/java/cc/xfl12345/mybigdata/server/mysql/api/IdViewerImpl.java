package cc.xfl12345.mybigdata.server.mysql.api;

import cc.xfl12345.mybigdata.server.common.api.IdViewer;
import cc.xfl12345.mybigdata.server.common.appconst.AppConst;
import cc.xfl12345.mybigdata.server.common.appconst.AppDataType;
import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.data.requirement.DataRequirementPack;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableBasicMapper;
import cc.xfl12345.mybigdata.server.common.pojo.FieldNotNullChecker;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.CoreTableCache;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import lombok.Getter;
import lombok.Setter;
import org.teasoft.bee.osql.transaction.Transaction;
import org.teasoft.honey.osql.core.SessionFactory;

import javax.annotation.PostConstruct;

public class IdViewerImpl implements IdViewer {
    @Getter
    @Setter
    protected FieldNotNullChecker fieldNotNullChecker = DefaultSingleton.FIELD_NOT_NULL_CHECKER;

    @Getter
    @Setter
    protected TableBasicMapper<GlobalDataRecord> globalDataRecordMapper;

    @Getter
    @Setter
    protected CoreTableCache coreTableCache;


    @PostConstruct
    public void init() throws Exception {
        fieldNotNullChecker.check(coreTableCache, "coreTableCache");
        fieldNotNullChecker.check(globalDataRecordMapper, GlobalDataRecord.class);
    }

    @Override
    public AppDataType getDataTypeById(Object id) {
        Object tableNameId;
        Transaction transaction = SessionFactory.getTransaction();
        try {
            transaction.begin();
            GlobalDataRecord globalDataRecord = globalDataRecordMapper.selectById(
                id, GlobalDataRecord.Fields.id, GlobalDataRecord.Fields.tableName
            );
            tableNameId = globalDataRecord.getTableName();
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }

        AppDataType appDataType = AppDataType.Null;
        if (tableNameId != null) {
            appDataType = coreTableCache.getPoInfo(coreTableCache.getPojoClass(tableNameId)).getDataType();
        }

        return appDataType;
    }

    @Override
    public Object getDataById(Object id, DataRequirementPack dataRequirement) {
        return null;
    }

}
