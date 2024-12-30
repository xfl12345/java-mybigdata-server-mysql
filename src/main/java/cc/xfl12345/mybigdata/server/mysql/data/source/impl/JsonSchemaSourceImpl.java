package cc.xfl12345.mybigdata.server.mysql.data.source.impl;


import cc.xfl12345.mybigdata.server.common.data.source.JsonSchemaSource;
import cc.xfl12345.mybigdata.server.common.data.source.StringTypeSource;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdJsonSchema;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.PlainMbdJsonSchema;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableMapper;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.AbstractBeeDoubleLayerTableDataSource;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.TableSchemaRecord;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;
import com.networknt.schema.JsonSchemaFactory;
import lombok.Getter;
import lombok.Setter;
import org.teasoft.bee.osql.api.Condition;
import org.teasoft.bee.osql.Op;
import org.teasoft.honey.osql.core.ConditionImpl;

import java.util.List;


public class JsonSchemaSourceImpl
    extends AbstractBeeDoubleLayerTableDataSource<MbdJsonSchema, TableSchemaRecord>
    implements JsonSchemaSource {

    @Getter
    @Setter
    protected StringTypeSource stringTypeSource;

    @Getter
    @Setter
    protected JsonSchemaFactory jsonSchemaFactory;

    @Override
    public void init() throws Exception {
        fieldNotNullChecker.check(stringTypeSource, String.class);
        fieldNotNullChecker.check(jsonSchemaFactory, "jsonSchemaFactory");
        super.init();
    }

    @Override
    protected Class<TableSchemaRecord> getPojoClass() {
        return TableSchemaRecord.class;
    }

    @Override
    protected String getIdFieldName() {
        return TableSchemaRecord.Fields.globalId;
    }

    @Override
    protected MbdId getId(TableSchemaRecord tableSchemaRecord) {
        return new MysqlMbdId(tableSchemaRecord.getGlobalId());
    }

    @Override
    protected Condition getSelectBatchIdCondition(List<MbdJsonSchema> values) {
        return new ConditionImpl()
            .selectField(getIdFieldName())
            .op(
                TableSchemaRecord.Fields.jsonSchema,
                Op.in,
                values.parallelStream().map(item -> item.getJsonSchema().getSchemaNode().toString()).toList()
            );
    }

    @Override
    protected TableMapper<TableSchemaRecord, Condition> getTableMapper() {
        return mapper;
    }

    private final String[] fieldNames4Select = new String[]{
        TableSchemaRecord.Fields.schemaName,
        TableSchemaRecord.Fields.jsonSchema
    };

    @Override
    protected String[] getFieldNames4Select() {
        return fieldNames4Select;
    }

    @Override
    protected MbdJsonSchema getValue(TableSchemaRecord tableSchemaRecord) {
        PlainMbdJsonSchema mbdJsonSchema = new PlainMbdJsonSchema();
        mbdJsonSchema.setGlobalId(new MysqlMbdId(tableSchemaRecord.getGlobalId()));
        mbdJsonSchema.setName(stringTypeSource.selectById(new MbdId(tableSchemaRecord.getSchemaName())));
        mbdJsonSchema.setJsonSchema(jsonSchemaFactory.getSchema(tableSchemaRecord.getJsonSchema()));

        return mbdJsonSchema;
    }

    @Override
    protected TableSchemaRecord getPojo(MbdJsonSchema mbdJsonSchema) {
        return getPojo(mbdJsonSchema.getGlobalId(), mbdJsonSchema);
    }

    @Override
    protected TableSchemaRecord getPojo(MbdId globalId, MbdJsonSchema mbdJsonSchema) {
        return TableSchemaRecord.builder()
            .globalId(MysqlMbdId.getValue(globalId))
            .schemaName(MysqlMbdId.getValue(stringTypeSource.selectIdOrInsert4Id(mbdJsonSchema.getName())))
            .build();
    }

    @Override
    protected MbdId getTableNameId(Class<?> pojoClass) {
        return coreTableCache.getTableNameId(pojoClass);
    }

    @Override
    public Class<MbdJsonSchema> getValueType() {
        return MbdJsonSchema.class;
    }
}
