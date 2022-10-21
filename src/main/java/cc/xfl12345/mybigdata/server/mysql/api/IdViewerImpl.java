package cc.xfl12345.mybigdata.server.mysql.api;

import cc.xfl12345.mybigdata.server.common.api.IdViewer;
import cc.xfl12345.mybigdata.server.common.appconst.AppConst;
import cc.xfl12345.mybigdata.server.common.appconst.AppDataType;
import cc.xfl12345.mybigdata.server.common.data.requirement.DataRequirementPack;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableBasicMapper;
import cc.xfl12345.mybigdata.server.mysql.database.converter.AppIdTypeConverter;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.CoreTableCache;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.TableMapperProperties;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.DaoPack;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import lombok.Getter;
import lombok.Setter;
import org.teasoft.bee.osql.transaction.Transaction;
import org.teasoft.honey.osql.core.SessionFactory;

import javax.annotation.PostConstruct;

public class IdViewerImpl implements IdViewer {
    @Getter
    @Setter
    protected String fieldCanNotBeNullMessageTemplate = AppConst.FIELD_CAN_NOT_BE_NULL_MESSAGE_TEMPLATE;

    protected String mapperNotFoundMessageTemplate = AppConst.MAPPER_NOT_FOUND_MESSAGE_TEMPLATE;

    @Getter
    @Setter
    protected DaoPack daoPack;

    protected TableBasicMapper<GlobalDataRecord> globalDataRecordMapper;

    protected CoreTableCache coreTableCache;

    protected AppIdTypeConverter idTypeConverter;

    @PostConstruct
    public void init() throws Exception {
        if (daoPack == null) {
            throw new IllegalArgumentException(fieldCanNotBeNullMessageTemplate.formatted("daoPack"));
        }

        TableMapperProperties tableMapperProperties = daoPack.getTableMapperProperties();
        tableMapperProperties.checkProperties();

        coreTableCache = tableMapperProperties.getCoreTableCache();
        idTypeConverter = tableMapperProperties.getIdTypeConverter();
        globalDataRecordMapper = daoPack.getMapper(GlobalDataRecord.class);

        if (globalDataRecordMapper == null) {
            throw new IllegalArgumentException(
                mapperNotFoundMessageTemplate.formatted(GlobalDataRecord.class.getCanonicalName())
            );
        }
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
        String tableName = coreTableCache.getTableNameCache().getKey(idTypeConverter.convert(tableNameId));
        if (tableName != null) {
            appDataType = daoPack.getMapperPackByTableName(tableName).getDataType();
        }

        return appDataType;
    }

    @Override
    public Object getDataById(Object id, DataRequirementPack dataRequirement) {
        return null;
    }

}
