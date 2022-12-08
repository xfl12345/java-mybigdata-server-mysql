// package cc.xfl12345.mybigdata.server.mysql.data.pojo;
//
// import cc.xfl12345.mybigdata.server.common.appconst.AppConst;
// import cc.xfl12345.mybigdata.server.common.appconst.TableCurdResult;
// import cc.xfl12345.mybigdata.server.common.data.source.StringTypeSource;
// import cc.xfl12345.mybigdata.server.common.data.source.pojo.BaseReactiveObject;
// import cc.xfl12345.mybigdata.server.common.data.source.pojo.ReactiveGroup;
// import cc.xfl12345.mybigdata.server.common.data.source.pojo.ReactiveMode;
// import cc.xfl12345.mybigdata.server.common.database.mapper.TableBasicMapper;
// import cc.xfl12345.mybigdata.server.mysql.database.converter.AppIdTypeConverter;
// import cc.xfl12345.mybigdata.server.mysql.database.converter.IdTypeConverter;
// import cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.DaoPack;
// import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
// import cc.xfl12345.mybigdata.server.mysql.database.pojo.GroupContent;
// import cc.xfl12345.mybigdata.server.mysql.database.pojo.GroupRecord;
// import lombok.Getter;
// import lombok.Setter;
// import org.teasoft.bee.osql.*;
// import org.teasoft.honey.osql.core.BeeFactory;
// import org.teasoft.honey.osql.core.ConditionImpl;
//
// import javax.annotation.PostConstruct;
// import java.util.*;
// import java.util.concurrent.ConcurrentHashMap;
// import java.util.concurrent.CopyOnWriteArrayList;
// import java.util.function.Consumer;
//
// public class ReactiveGroupImpl extends AbstractList<Object> implements ReactiveGroup {
//     protected final Object globalId;
//
//     protected final ReactiveMode mode;
//
//     @Getter
//     @Setter
//     protected String fieldCanNotBeNullMessageTemplate = AppConst.FIELD_CAN_NOT_BE_NULL_MESSAGE_TEMPLATE;
//
//     @Getter
//     @Setter
//     protected DaoPack daoPack;
//
//     protected TableBasicMapper<GroupRecord> groupRecordMapper;
//
//     protected TableBasicMapper<GroupContent> groupContentMapper;
//
//     protected AppIdTypeConverter idTypeConverter;
//
//     @Getter
//     @Setter
//     protected StringTypeSource stringTypeSource;
//
//
//     public ReactiveGroupImpl(Object globalId, ReactiveMode mode) {
//         this.globalId = globalId;
//         this.mode = mode;
//
//         if (mode.getLockFlag().isEnable()) {
//             lockRow();
//         }
//
//         if (mode.getCacheFlag().isEnable()) {
//             idValueCache = new ConcurrentHashMap<>();
//             groupContentCache = new CopyOnWriteArrayList<>();
//         }
//     }
//
//     @PostConstruct
//     public void init() throws Exception {
//         if (daoPack == null) {
//             throw new IllegalArgumentException(fieldCanNotBeNullMessageTemplate.formatted("daoPack"));
//         }
//         if (stringTypeSource == null) {
//             throw new IllegalArgumentException(fieldCanNotBeNullMessageTemplate.formatted("stringTypeSource"));
//         }
//
//         groupRecordMapper = daoPack.getMapper(GroupRecord.class);
//         groupContentMapper = daoPack.getMapper(GroupContent.class);
//         idTypeConverter = daoPack.getMapperProperties().getIdTypeConverter();
//
//         // 开启缓存 并 一次性加载全部
//         if (mode.getCacheFlag().isEnable() && mode.getOnDemandFlag().isDisable()) {
//             initGroupRecordCache();
//             initGroupContentCache();
//         }
//     }
//
//     @Override
//     public void destoryInstance() {
//         unlockRow();
//     }
//
//     @Override
//     public Object getGlobalId() {
//         return globalId;
//     }
//
//     /**
//      * ID -> Object (Cache)
//      */
//     protected ConcurrentHashMap<Object, Object> idValueCache;
//
//     protected GroupRecord groupRecordCache;
//
//     protected List<Object> groupContentCache;
//
//     protected void initGroupRecordCache() {
//         if (groupRecordCache == null) {
//             // 加载 record
//             groupRecordCache = groupRecordMapper.selectById(globalId);
//             // 解析 record 里的引用
//             // 解析名称
//             idValueCache.put(
//                 groupRecordCache.getGroupName(),
//                 stringTypeSource.selectById(groupRecordCache.getGroupName())
//             );
//         }
//     }
//
//     protected void initGroupContentCache() {
//         if (groupContentCache == null) {
//             groupContentCache = getItemList();
//         }
//     }
//
//     protected List<Object> getItemList() {
//         // 加载所有成员
//         SuidRich suidRich = BeeFactory.getHoneyFactory().getSuidRich();
//         Condition condition = new ConditionImpl();
//         condition.op(GroupContent.Fields.globalId, Op.eq, globalId);
//         condition.orderBy(GroupContent.Fields.itemIndex, OrderType.ASC);
//         List<GroupContent> contents = suidRich.select(new GroupContent(), condition);
//         List<Object> cacheList = new CopyOnWriteArrayList<>(new Object[contents.size()]);
//         contents.parallelStream().forEach((groupContent) -> {
//             cacheList.set(groupContent.getItemIndex().intValue(), groupContent.getItem());
//         });
//
//         return cacheList;
//     }
//
//     protected boolean checkGroupContentCacheAndInit() {
//         if (mode.getCacheFlag().isEnable()) {
//             if (mode.getOnDemandFlag().isEnable()) {
//                 initGroupContentCache();
//             }
//
//             return true;
//         }
//
//         return false;
//     }
//
//     @Override
//     public String getName() {
//         if (mode.getCacheFlag().isEnable()) {
//             Object groupNameId;
//             if (mode.getOnDemandFlag().isEnable()) {
//                 initGroupRecordCache();
//             }
//
//             groupNameId = groupRecordCache.getGroupName();
//             return (String) idValueCache.get(groupNameId);
//         }
//
//         return stringTypeSource.selectById(
//             groupRecordMapper.selectById(globalId, GroupRecord.Fields.groupName).getGroupName()
//         );
//     }
//
//     @Override
//     public void setName(String name) {
//         Object id;
//         try {
//             id = stringTypeSource.insertAndReturnId(name);
//         } catch (RuntimeException e) {
//             TableCurdResult result = daoPack.getSqlErrorAnalyst().getTableCurdResult(e);
//             if (result.equals(TableCurdResult.DUPLICATE)) {
//                 id = stringTypeSource.selectId(name);
//             } else {
//                 throw e;
//             }
//         }
//
//         SuidRich suidRich = BeeFactory.getHoneyFactory().getSuidRich();
//         Condition condition = new ConditionImpl();
//         condition.setIncludeType(IncludeType.EXCLUDE_BOTH);
//         GroupRecord groupRecord = new GroupRecord();
//         idTypeConverter.injectId2Object(globalId, groupRecord::setGlobalId);
//         idTypeConverter.injectId2Object(id, groupRecord::setGroupName);
//         suidRich.updateById(new GroupRecord(), condition);
//
//         if (mode.getCacheFlag().isEnable()) {
//             Object groupNameId = groupRecordCache.getGroupName();
//             if (groupNameId != null) {
//                 idValueCache.remove(groupNameId);
//             }
//
//             idValueCache.put(id, name);
//         } else {
//             idTypeConverter.injectId2Object(id, groupRecordCache::setGroupName);
//         }
//     }
//
//     @Override
//     public boolean isUniqueItems() {
//         if (mode.getCacheFlag().isEnable()) {
//             if (mode.getOnDemandFlag().isEnable()) {
//                 initGroupRecordCache();
//             }
//
//             return groupRecordCache.getUniqueItems();
//         }
//
//         return groupRecordMapper.selectById(globalId).getUniqueItems();
//     }
//
//     @Override
//     public void setUniqueItems(boolean unique) {
//         SuidRich suidRich = BeeFactory.getHoneyFactory().getSuidRich();
//         Condition condition = new ConditionImpl();
//         condition.setIncludeType(IncludeType.EXCLUDE_BOTH);
//         GroupRecord groupRecord = new GroupRecord();
//         idTypeConverter.injectId2Object(globalId, groupRecord::setGlobalId);
//         groupRecord.setUniqueItems(unique);
//         suidRich.updateById(new GroupRecord(), condition);
//
//         if (mode.getCacheFlag().isEnable()) {
//             if (mode.getOnDemandFlag().isEnable()) {
//                 initGroupRecordCache();
//             }
//         } else {
//             groupRecordCache.setUniqueItems(unique);
//         }
//     }
//
//     protected void lockRow() {
//         SuidRich suidRich = BeeFactory.getHoneyFactory().getSuidRich();
//         Condition condition = new ConditionImpl();
//         condition.forUpdate();
//         GlobalDataRecord globalDataRecord = new GlobalDataRecord();
//         idTypeConverter.injectId2Object(globalId, globalDataRecord::setId);
//         suidRich.selectOne(globalDataRecord);
//     }
//
//     protected void unlockRow() {
//     }
//
//     @Override
//     public int size() {
//         if (checkGroupContentCacheAndInit()) {
//             return groupContentCache.size();
//         }
//
//         SuidRich suidRich = BeeFactory.getHoneyFactory().getSuidRich();
//         Condition condition = new ConditionImpl();
//         condition.op(GroupContent.Fields.globalId, Op.eq, globalId);
//         return suidRich.count(new GroupContent(), condition);
//     }
//
//     @Override
//     public boolean contains(Object o) {
//         if (checkGroupContentCacheAndInit()) {
//
//         }
//
//         return false;
//     }
//
//     @Override
//     public boolean add(Object o) {
//         return false;
//     }
//
//     @Override
//     public boolean remove(Object o) {
//         return false;
//     }
//
//     @Override
//     public boolean containsAll(Collection<?> c) {
//         return false;
//     }
//
//     @Override
//     public boolean addAll(Collection<?> c) {
//         return false;
//     }
//
//     @Override
//     public boolean addAll(int index, Collection<?> c) {
//         return false;
//     }
//
//     @Override
//     public boolean removeAll(Collection<?> c) {
//         return false;
//     }
//
//     @Override
//     public boolean retainAll(Collection<?> c) {
//         return false;
//     }
//
//     @Override
//     public void clear() {
//
//     }
//
//     @Override
//     public Object get(int index) {
//         return null;
//     }
//
//     @Override
//     public Object set(int index, Object element) {
//         return null;
//     }
//
//     @Override
//     public void add(int index, Object element) {
//
//     }
//
//     @Override
//     public Object remove(int index) {
//         return null;
//     }
//
//     @Override
//     public int indexOf(Object o) {
//         return 0;
//     }
//
//     @Override
//     public int lastIndexOf(Object o) {
//         return 0;
//     }
// }
