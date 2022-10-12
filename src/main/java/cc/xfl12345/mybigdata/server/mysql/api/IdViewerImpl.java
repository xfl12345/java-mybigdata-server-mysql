package cc.xfl12345.mybigdata.server.mysql.api;

import cc.xfl12345.mybigdata.server.common.api.IdViewer;
import cc.xfl12345.mybigdata.server.common.appconst.AppConst;
import cc.xfl12345.mybigdata.server.common.appconst.AppDataType;
import cc.xfl12345.mybigdata.server.common.data.requirement.DataRequirementPack;
import cc.xfl12345.mybigdata.server.common.database.error.TableDataException;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableBasicMapper;
import cc.xfl12345.mybigdata.server.mysql.appconst.CoreTables;
import cc.xfl12345.mybigdata.server.mysql.database.constant.GlobalDataRecordConstant;
import cc.xfl12345.mybigdata.server.mysql.database.converter.AppIdTypeConverter;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.CoreTableCache;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.teasoft.bee.osql.transaction.Transaction;
import org.teasoft.honey.osql.core.SessionFactory;

import java.util.HashMap;

public class IdViewerImpl implements IdViewer, InitializingBean {
    @Getter
    @Setter
    protected String fieldCanNotBeNullMessageTemplate = AppConst.FIELD_CAN_NOT_BE_NULL_MESSAGE_TEMPLATE;

    @Getter
    @Setter
    protected CoreTableCache coreTableCache;

    @Getter
    @Setter
    protected AppIdTypeConverter idTypeConverter;

    @Getter
    @Setter
    protected TableBasicMapper<GlobalDataRecord> globalDataRecordMapper;

    protected HashMap<String, CoreTables> tableNameMap;

    protected void initTableNameMap() {
        tableNameMap = new HashMap<>(CoreTables.values().length);
        for (CoreTables coreTable : CoreTables.values()) {
            tableNameMap.put(coreTable.getName(), coreTable);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (coreTableCache == null) {
            throw new IllegalArgumentException(fieldCanNotBeNullMessageTemplate.formatted("coreTableCache"));
        }
        if (idTypeConverter == null) {
            throw new IllegalArgumentException(fieldCanNotBeNullMessageTemplate.formatted("idTypeConverter"));
        }
        if (globalDataRecordMapper == null) {
            throw new IllegalArgumentException(fieldCanNotBeNullMessageTemplate.formatted("globalDataRecordMapper"));
        }

        initTableNameMap();
    }

    @Override
    public AppDataType getDataTypeById(Object id) {
        Object tableNameId;
        String tableNameUUID;
        Transaction transaction = SessionFactory.getTransaction();
        try {
            transaction.begin();
            GlobalDataRecord globalDataRecord = globalDataRecordMapper.selectById(
                id,
                new String[]{
                    GlobalDataRecordConstant.ID,
                    GlobalDataRecordConstant.UUID,
                    GlobalDataRecordConstant.TABLE_NAME
                }
            );
            tableNameId = globalDataRecord.getTableName();
            tableNameUUID = globalDataRecord.getUuid();
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }

        AppDataType appDataType = AppDataType.Null;
        String tableName = coreTableCache.getTableNameCache().getKey(idTypeConverter.convert(tableNameId));
        if (tableName != null) {
            switch (tableNameMap.get(tableName)) {
                default -> {
                    throw new TableDataException(
                        "全局数据记录表出现了不该出现的引用表",
                        new Object[]{id},
                        new String[]{tableNameUUID},
                        tableName
                    );
                }
                case TABLE_SCHEMA_RECORD -> appDataType = AppDataType.JsonSchema;
                case STRING_CONTENT -> appDataType = AppDataType.String;
                case BOOLEAN_CONTENT -> appDataType = AppDataType.Boolean;
                case NUMBER_CONTENT -> appDataType = AppDataType.Number;
                case GROUP_RECORD -> appDataType = AppDataType.Array;
                case OBJECT_RECORD -> appDataType = AppDataType.Object;
            }
        }

        return appDataType;
    }

    @Override
    public Object getDataById(Object id, DataRequirementPack dataRequirement) {
        return null;
    }

}
