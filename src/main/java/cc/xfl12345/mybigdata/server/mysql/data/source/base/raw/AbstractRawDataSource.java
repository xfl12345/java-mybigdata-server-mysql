// package cc.xfl12345.mybigdata.server.mysql.data.source;
//
// import cc.xfl12345.mybigdata.server.common.data.source.DataSource;
// import cc.xfl12345.mybigdata.server.common.database.mapper.TableMapper;
// import org.teasoft.bee.osql.Condition;
//
// import java.util.List;
//
// public abstract class AbstractRawDataSource<Value, Pojo> implements DataSource<Value> {
//     protected TableMapper<Pojo, Condition> mapper;
//
//     public AbstractRawDataSource(TableMapper<Pojo, Condition> mapper) {
//         this.mapper = mapper;
//     }
//
//     protected abstract String[] getSelectContentFieldOnly();
//
//     protected abstract Value getValue(Pojo pojo);
//
//     protected abstract Pojo getPojo(Value value);
//
//     @Override
//     public Object insert4IdOrGetId(Value value) {
//         // 由于不是原子操作，所以理应禁止使用。
//         throw new UnsupportedOperationException();
//     }
//
//     // @Override
//     // public Object insertAndReturnId(Value value) {
//     //     throw new UnsupportedOperationException();
//     //     // CommonGlobalDataRecord globalDataRecord = globalDataRecordDataSource
//     //     //     .getNewRegisteredInstance(new Date(), mapper.getPojoType());
//     //     // Object id = globalDataRecord.getId();
//     //     // try {
//     //     //     mapper.insert(getPojo(id, value));
//     //     //     return id;
//     //     // } catch (RuntimeException runtimeException) {
//     //     //     globalDataRecordDataSource.deleteById(id);
//     //     //     throw runtimeException;
//     //     // }
//     // }
//     //
//     // @Override
//     // public long insert(Value value) {
//     //     throw new UnsupportedOperationException();
//     //     // CommonGlobalDataRecord globalDataRecord = globalDataRecordDataSource
//     //     //     .getNewRegisteredInstance(new Date(), mapper.getPojoType());
//     //     // Object id = globalDataRecord.getId();
//     //     // try {
//     //     //     return mapper.insert(getPojo(id, value));
//     //     // } catch (RuntimeException runtimeException) {
//     //     //     globalDataRecordDataSource.deleteById(id);
//     //     //     throw runtimeException;
//     //     // }
//     // }
//     //
//     // @Override
//     // public long insertBatch(List<Value> values) {
//     //     throw new UnsupportedOperationException();
//     //     // List<CommonGlobalDataRecord> globalDataRecords = globalDataRecordDataSource
//     //     //     .getNewRegisteredInstances(new Date(), getPojoType(), values.size());
//     //     // List<Pojo> pojoList = new ArrayList<>(values.size());
//     //     // for (int i = 0; i < values.size(); i++) {
//     //     //     pojoList.add(getOuterContext().getPojo(globalDataRecords.get(i).getId(), values.get(i)));
//     //     // }
//     //     //
//     //     // try {
//     //     //     return mapper.insertBatch(pojoList);
//     //     // } catch (RuntimeException runtimeException) {
//     //     //     globalDataRecordDataSource.deleteBatchById(
//     //     //         globalDataRecords.parallelStream().map(CommonGlobalDataRecord::getId).toList()
//     //     //     );
//     //     //     throw runtimeException;
//     //     // }
//     // }
//
//
//     @Override
//     public Object insertAndReturnId(Value value) {
//         return mapper.insert(getPojo(value));
//     }
//
//     @Override
//     public long insert(Value value) {
//         throw new UnsupportedOperationException();
//         // CommonGlobalDataRecord globalDataRecord = globalDataRecordDataSource
//         //     .getNewRegisteredInstance(new Date(), mapper.getPojoType());
//         // Object id = globalDataRecord.getId();
//         // try {
//         //     return mapper.insert(getPojo(id, value));
//         // } catch (RuntimeException runtimeException) {
//         //     globalDataRecordDataSource.deleteById(id);
//         //     throw runtimeException;
//         // }
//     }
//
//     @Override
//     public long insertBatch(List<Value> values) {
//         throw new UnsupportedOperationException();
//         // List<CommonGlobalDataRecord> globalDataRecords = globalDataRecordDataSource
//         //     .getNewRegisteredInstances(new Date(), getPojoType(), values.size());
//         // List<Pojo> pojoList = new ArrayList<>(values.size());
//         // for (int i = 0; i < values.size(); i++) {
//         //     pojoList.add(getOuterContext().getPojo(globalDataRecords.get(i).getId(), values.get(i)));
//         // }
//         //
//         // try {
//         //     return mapper.insertBatch(pojoList);
//         // } catch (RuntimeException runtimeException) {
//         //     globalDataRecordDataSource.deleteBatchById(
//         //         globalDataRecords.parallelStream().map(CommonGlobalDataRecord::getId).toList()
//         //     );
//         //     throw runtimeException;
//         // }
//     }
//
//     @Override
//     public Object selectId(Value value) {
//         return mapper.selectId(getPojo(value));
//     }
//
//     @Override
//     public Value selectById(Object globalId) {
//         return getValue(mapper.selectById(globalId, getSelectContentFieldOnly()));
//     }
//
//     @Override
//     public List<Value> selectBatchById(List<Object> globalIdList) {
//         return mapper.selectBatchById(globalIdList, getSelectContentFieldOnly())
//             .parallelStream().map(this::getValue).toList();
//     }
//
//     @Override
//     public void updateById(Value value, Object globalId) {
//         mapper.updateById(getPojo(value), globalId);
//     }
//
//     @Override
//     public void deleteById(Object globalId) {
//         mapper.deleteById(globalId);
//     }
//
//     @Override
//     public void deleteBatchById(List<Object> globalIdList) {
//         mapper.deleteBatchById(globalIdList);
//     }
// }
