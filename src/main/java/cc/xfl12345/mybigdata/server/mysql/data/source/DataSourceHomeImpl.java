package cc.xfl12345.mybigdata.server.mysql.data.source;

import cc.xfl12345.mybigdata.server.common.appconst.AppDataType;
import cc.xfl12345.mybigdata.server.common.appconst.CommonConst;
import cc.xfl12345.mybigdata.server.common.appconst.JsonSchemaKeyWords;
import cc.xfl12345.mybigdata.server.common.data.source.DataSourceHome;
import cc.xfl12345.mybigdata.server.common.data.source.IdDataSource;
import cc.xfl12345.mybigdata.server.common.data.source.ObjectTypeSource;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.*;
import cc.xfl12345.mybigdata.server.common.database.error.DataValidationException;
import cc.xfl12345.mybigdata.server.common.database.error.IllegalDataException;
import cc.xfl12345.mybigdata.server.common.database.error.ProtectedBackupException;
import cc.xfl12345.mybigdata.server.common.database.error.TypeNotMatchException;
import cc.xfl12345.mybigdata.server.mysql.database.mapper.base.CoreTableCache;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataSourceHomeImpl extends DataSourceHome {

    protected ThreadLocal<Boolean> lock4Backup = new ThreadLocal<>();

    @Getter
    @Setter
    protected ObjectMapper jacksonObjectMapper;

    @Getter
    @Setter
    protected JsonSchemaFactory jsonSchemaFactory;

    @Getter
    @Setter
    protected CoreTableCache coreTableCache;

    private IdDataSource idDataSource;

    @Override
    public void init() throws Exception {
        fieldNotNullChecker.check(coreTableCache, "coreTableCache");
        fieldNotNullChecker.check(jacksonObjectMapper, "jacksonObjectMapper");
        fieldNotNullChecker.check(jsonSchemaFactory, "jsonSchemaFactory");

        super.init();

        idDataSource = dataSourceBag.getIdDataSource();
    }

    protected boolean isLock4Backup() {
        return lock4Backup.get() != null && lock4Backup.get();
    }

    protected AppDataType getAppDataType(JsonNode jsonNode) {
        AppDataType dataType = null;
        switch (jsonNode.getNodeType()) {
            case ARRAY -> dataType = AppDataType.Array;
            case BOOLEAN -> dataType = AppDataType.Boolean;
            case NULL -> dataType = AppDataType.Null;
            case NUMBER -> dataType = AppDataType.Number;
            case OBJECT -> dataType = AppDataType.Object;
            case STRING -> dataType = AppDataType.String;
            default -> throw new UnsupportedOperationException(
                "Can not find a type match JSON type of [" + jsonNode.getNodeType() + "]"
            );
        }

        return dataType;
    }

    protected void checkDataType(AppDataType expectDataType, JsonNode data) {
        AppDataType detectedType = getAppDataType(data);
        if (!expectDataType.equals(detectedType)) {
            throw new TypeNotMatchException(expectDataType, data);
        }
    }

    protected Set<ValidationMessage> validate(MbdObject mbdObject) {
        return jsonSchemaFactory
            .getSchema(mbdObject.getSchema().getJsonSchema().getRefSchemaNode(mbdObject.getSchemaPath()))
            .validate(jacksonObjectMapper.valueToTree(mbdObject.getMap()));
    }

    protected void checkMbdObject(MbdObject mbdObject) {
        Set<ValidationMessage> errors = validate(mbdObject);
        if (errors.size() != 0) {
            throw new DataValidationException("JSON schema test failed. Data is illegal.", mbdObject, errors);
        }
    }

    protected IllegalDataException getIllegalDataException4Json(AppDataType dataType, MbdId id) {
        return new IllegalDataException(
            "Unacceptable data type for [" + dataType + "] in JSON.",
            new MbdId[]{id}
        );
    }

    protected void checkAddDataShouldNotHaveId(BaseMbdObject mbdObject) {
        if (mbdObject.getGlobalId() != null) {
            throw new IllegalArgumentException("The data to be added should not have id.");
        }
    }

    @Override
    public MbdId addData(JsonNode data, AppDataType dataType) {
        if (isLock4Backup()) {
            throw new ProtectedBackupException();
        }

        MbdId result;
        try {
            switch (dataType) {
                case Null -> throw new UnsupportedOperationException("Can not add the \"NULL\" constant.");
                case Boolean -> throw new UnsupportedOperationException("Can not add boolean type constant.");
                case Id -> throw new UnsupportedOperationException("Can not add \"ID\" type data.");
                case String -> result = dataSourceBag.getStringTypeSource().insertAndReturnId(data.textValue());
                case Number -> result = dataSourceBag.getNumberTypeSource().insertAndReturnId(data.decimalValue());
                case Array -> {
                    PlainMdbGroup mdbGroup = jacksonObjectMapper.treeToValue(data, PlainMdbGroup.class);
                    checkAddDataShouldNotHaveId(mdbGroup);

                    result = dataSourceBag.getGroupTypeSource().insertAndReturnId(mdbGroup);
                }
                case Object -> {
                    PlainMbdObject mbdObject = jacksonObjectMapper.treeToValue(data, PlainMbdObject.class);
                    checkAddDataShouldNotHaveId(mbdObject);

                    result = dataSourceBag.getObjectTypeSource().insertAndReturnId(mbdObject);
                }
                case JsonSchema -> {
                    PlainMbdJsonSchema mbdJsonSchema = jacksonObjectMapper.treeToValue(data, PlainMbdJsonSchema.class);
                    checkAddDataShouldNotHaveId(mbdJsonSchema);

                    result = dataSourceBag.getJsonSchemaSource().insertAndReturnId(mbdJsonSchema);
                }
                default -> throw new UnsupportedOperationException();
            }
        } catch (JsonProcessingException e) {
            throw new TypeNotMatchException(dataType, data);
        }

        return result;
    }

    @Override
    public AppDataType getDataTypeById(MbdId id) {
        return idDataSource.getDataEnumType(id);
    }

    @Override
    public MbdId getIdByData(JsonNode data) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public BaseMbdObject getMbdDataById(MbdId id, Long recursionDepth) {
        BaseMbdObject result = null;
        if (recursionDepth == null) {
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
                default -> throw new UnsupportedOperationException();
            }
        } else {
            // TODO
            throw new UnsupportedOperationException();
        }

        return result;
    }

    @Override
    public JsonNode getDataById(MbdId id, Long recursionDepth) {
        // TODO 不使用递归，改成大循环迭代
        if (CommonConst.LONG_ZERO.equals(recursionDepth)) {
            return null;
        }

        JsonNode result = null;
        AppDataType dataType = getDataTypeById(id);
        if (recursionDepth == null || recursionDepth == 1) {
            switch (dataType) {
                case Boolean -> {
                    result = coreTableCache.getBooleanById(id) ? BooleanNode.getTrue() : BooleanNode.getFalse();
                }
                case String -> {
                    result = TextNode.valueOf(dataSourceBag.getStringTypeSource().selectById(id));
                }
                case Number -> {
                    result = DecimalNode.valueOf(dataSourceBag.getNumberTypeSource().selectById(id));
                }
                case Array -> {
                    MbdGroup mbdGroup = dataSourceBag.getGroupTypeSource().selectById(id);
                    result = jacksonObjectMapper.valueToTree(mbdGroup.getItems());
                }
                case Object -> {
                    MbdObject mbdObject = dataSourceBag.getObjectTypeSource().selectById(id);
                    result = jacksonObjectMapper.valueToTree(mbdObject.getMap());
                }
                case JsonSchema -> {
                    MbdJsonSchema mbdJsonSchema = dataSourceBag.getJsonSchemaSource().selectById(id);
                    result = mbdJsonSchema.getJsonSchema().getSchemaNode();
                }
                case Null -> result = NullNode.getInstance();
                default -> throw getIllegalDataException4Json(dataType, id);
            }
        } else {
            switch (dataType) {
                case Array -> {
                    MbdGroup mbdGroup = dataSourceBag.getGroupTypeSource().selectById(id);
                    List<MbdId> mbdIdList = mbdGroup.getItems();
                    ArrayNode arrayNode = jacksonObjectMapper.createArrayNode();
                    for (MbdId mbdId : mbdIdList) {
                        arrayNode.add(getDataById(mbdId, recursionDepth - 1));
                    }

                    result = arrayNode;
                }
                case Object -> {
                    MbdObject mbdObject = dataSourceBag.getObjectTypeSource().selectById(id);
                    JsonSchema jsonSchema = mbdObject.getSchema().getJsonSchema();
                    // int childMaxCount = jsonSchema.getRefSchemaNode(mbdObject.getSchemaPath()).size();
                    // Queue<MbdId> collections = new LinkedList<>();
                    Map<String, MbdId> mbdIdMap = mbdObject.getMap();
                    ObjectNode objectNode = jacksonObjectMapper.createObjectNode();
                    for (String fieldName : mbdIdMap.keySet()) {
                        MbdId mbdId = mbdIdMap.get(fieldName);
                        String type = jsonSchema.getRefSchemaNode(
                            mbdObject.getSchemaPath() + '/' + JsonSchemaKeyWords.PROPERTIES + '/' + fieldName
                        ).get("type").asText();
                        AppDataType typeInSchema = AppDataType.getDataTypeByV202012KeyWords(type);
                        switch (typeInSchema) {
                            case Null, Boolean, String, Number, Array, Object -> {
                                objectNode.set(fieldName, getDataById(mbdId, recursionDepth - 1));
                            }
                            default -> throw getIllegalDataException4Json(dataType, id);
                        }
                    }

                    result = objectNode;
                }
                default -> throw getIllegalDataException4Json(dataType, id);
            }
        }

        return result;
    }

    @Override
    public boolean setData(BaseMbdObject baseMbdObject) {
        if (isLock4Backup()) {
            throw new ProtectedBackupException();
        }

        boolean result = false;
        AppDataType dataType = baseMbdObject.getDataType();

        switch (dataType) {
            case Null -> throw new UnsupportedOperationException("Can not update the \"NULL\" constant.");
            case Boolean -> throw new UnsupportedOperationException("Can not update boolean type constant.");
            case String -> {
                MbdString mbdString = (MbdString) baseMbdObject;
                dataSourceBag.getStringTypeSource().updateById(mbdString.getValue(), mbdString.getGlobalId());
                result = true;
            }
            case Number -> {
                MbdNumber mbdNumber = (MbdNumber) baseMbdObject;
                dataSourceBag.getNumberTypeSource().updateById(mbdNumber.getValue(), mbdNumber.getGlobalId());
                result = true;
            }
            case Array -> {
                MbdGroup mbdGroup = (MbdGroup) baseMbdObject;
                dataSourceBag.getGroupTypeSource().updateById(mbdGroup);
                result = true;
            }
            case Object -> {
                MbdObject mbdObject = (MbdObject) baseMbdObject;
                ObjectTypeSource source = dataSourceBag.getObjectTypeSource();
                MbdObject dbData = source.selectById(mbdObject.getGlobalId());
                if (dbData.isEqualsExceptData(mbdObject)) {

                    source.updateById(mbdObject);
                    result = true;
                }
            }
            case JsonSchema -> {
                MbdJsonSchema mbdJsonSchema = (MbdJsonSchema) baseMbdObject;
                dataSourceBag.getJsonSchemaSource().updateById(mbdJsonSchema);
                result = true;
            }
            default -> throw new UnsupportedOperationException();
        }

        return result;
    }

    @Override
    public List<MbdId> deleteData(JsonNode data, Long recursionDepth) {
        if (isLock4Backup()) {
            throw new ProtectedBackupException();
        }

        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteDataById(MbdId id, Long recursionDepth) {
        if (isLock4Backup()) {
            throw new ProtectedBackupException();
        }

        boolean result = false;
        if (recursionDepth == null) {
            switch (getDataTypeById(id)) {
                case Null -> throw new UnsupportedOperationException("Can not delete the \"NULL\" constant.");
                case Boolean -> throw new UnsupportedOperationException("Can not delete boolean type constant.");
                case String -> {
                    dataSourceBag.getStringTypeSource().deleteById(id);
                    result = true;
                }
                case Number -> {
                    dataSourceBag.getNumberTypeSource().deleteById(id);
                    result = true;
                }
                case Array -> {
                    dataSourceBag.getGroupTypeSource().deleteById(id);
                    result = true;
                }
                case Object -> {
                    dataSourceBag.getObjectTypeSource().deleteById(id);
                    result = true;
                }
                case JsonSchema -> {
                    dataSourceBag.getJsonSchemaSource().deleteById(id);
                    result = true;
                }
                default -> throw new UnsupportedOperationException();
            }
        } else {
            // TODO
            throw new UnsupportedOperationException();
        }

        return result;
    }

}
