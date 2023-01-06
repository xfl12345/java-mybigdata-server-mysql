package cc.xfl12345.mybigdata.server.mysql.data.source.impl;


import cc.xfl12345.mybigdata.server.common.appconst.AppDataType;
import cc.xfl12345.mybigdata.server.common.data.source.DataSource;
import cc.xfl12345.mybigdata.server.common.data.source.JsonSchemaSource;
import cc.xfl12345.mybigdata.server.common.data.source.StringTypeSource;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdJsonSchema;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.PlainMbdJsonSchema;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableMapper;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.AbstractBeeDoubleLayerTableDataSource;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.raw.AbstractIndependentTableRawDataSource;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.TableSchemaRecord;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;
import com.networknt.schema.JsonSchemaFactory;
import lombok.Getter;
import lombok.Setter;
import org.teasoft.bee.osql.Condition;

import java.util.LinkedHashMap;
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
    protected DataSource<MbdJsonSchema> generateRawImpl() {
        DataSource<?> myself = this;
        return new AbstractIndependentTableRawDataSource<MbdJsonSchema, TableSchemaRecord, Condition>() {
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
                return TableSchemaRecord.builder()
                    .globalId(MysqlMbdId.getValue(mbdJsonSchema.getGlobalId()))
                    .schemaName(MysqlMbdId.getValue(stringTypeSource.selectIdOrInsert4Id(mbdJsonSchema.getName())))
                    .build();
            }

            @Override
            public LinkedHashMap<MbdJsonSchema, MbdId> selectBatchId(List<MbdJsonSchema> mbdJsonSchemas) {
                // TODO support this feature
                throw new UnsupportedOperationException();
                // return null;
            }

            @Override
            public AppDataType getDataEnumType() {
                return AppDataType.JsonSchema;
            }

        };
    }

    @Override
    public Class<MbdJsonSchema> getValueType() {
        return MbdJsonSchema.class;
    }

    @Override
    protected Class<TableSchemaRecord> getPojoClass() {
        return TableSchemaRecord.class;
    }
}
