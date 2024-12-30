package cc.xfl12345.mybigdata.server.mysql.data.source.impl;

import cc.xfl12345.mybigdata.server.common.data.source.GroupTypeSource;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdGroup;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.PlainMdbGroup;
import cc.xfl12345.mybigdata.server.common.pojo.ReactiveMode;
import cc.xfl12345.mybigdata.server.mysql.data.pojo.ReactiveNoCacheMbdGroup;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.AbstractBeeTripleLayerTableDataSource;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GroupContent;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GroupRecord;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;
import org.teasoft.bee.osql.api.Condition;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.OrderType;
import org.teasoft.honey.osql.core.ConditionImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

public class GroupTypeSourceImpl
    extends AbstractBeeTripleLayerTableDataSource<MbdGroup, GroupRecord, GroupContent>
    implements GroupTypeSource {

    @Override
    public MbdId selectId(MbdGroup mbdGroup) {
        // TODO support this feature
        throw new UnsupportedOperationException();
        // return null;
    }

    @Override
    public MbdGroup selectById(MbdId globalId) {
        GroupRecord firstPojo = firstMapper.selectById(globalId);
        List<GroupContent> secondPojoList = secondMapper.selectByCondition(
            getEqualIdCondition(globalId).orderBy(GroupContent.Fields.itemIndex, OrderType.ASC)
        );

        return getValue(firstPojo, secondPojoList);
    }

    @Override
    public LinkedHashMap<MbdGroup, MbdId> selectBatchId(List<MbdGroup> mbdGroups) {
        // TODO support this feature
        throw new UnsupportedOperationException();
        // return null;
    }

    @Override
    public Class<MbdGroup> getValueType() {
        return MbdGroup.class;
    }

    @Override
    protected GroupRecord getFirstPojo(MbdId globalId, MbdGroup objects) {
        return GroupRecord.builder()
            .globalId(MysqlMbdId.getValue(globalId))
            .groupName(MysqlMbdId.getValue(stringTypeSource.selectIdOrInsert4Id(objects.getName())))
            .uniqueItems(objects.isUniqueItems())
            .build();
    }

    @Override
    protected List<GroupContent> getSecondPojo(MbdId globalId, MbdGroup objects) {
        List<MbdId> itemList = objects.getItems();
        int arrayLength = itemList.size();
        List<GroupContent> groupContentList = new ArrayList<>(arrayLength);
        for (int i = 0; i < arrayLength; i++) {
            GroupContent groupContent = new GroupContent();
            groupContent.setGlobalId(MysqlMbdId.getValue(globalId));
            groupContent.setItemIndex((long) i);
            groupContent.setItem(MysqlMbdId.getValue(itemList.get(i)));
            groupContentList.add(groupContent);
        }

        return groupContentList;
    }

    @Override
    protected MbdGroup getValue(GroupRecord groupRecord, List<GroupContent> groupContents) {
        PlainMdbGroup mbdGroup = new PlainMdbGroup();
        mbdGroup.setGlobalId(new MbdId(groupRecord.getGlobalId()));
        mbdGroup.setName(stringTypeSource.selectById(new MysqlMbdId(groupRecord.getGroupName())));
        mbdGroup.setUniqueItems(groupRecord.getUniqueItems());
        mbdGroup.setItems(groupContents.parallelStream().map(
            item -> new MbdId(item.getItem())
        ).toList());

        return mbdGroup;
    }

    @Override
    protected LinkedHashMap<MbdId, MbdGroup> getValue(LinkedHashMap<MbdId, GroupRecord> firstPojoCollection, List<GroupContent> secondPojoList) {
        int arrayLength = firstPojoCollection.size();

        // 先给 GroupContent 根据 id 分开来，随便排个序
        // id -> Map<index, GroupContent>
        Map<MbdId, ConcurrentSkipListMap<Long, GroupContent>> categorizedContent = new ConcurrentHashMap<>(arrayLength);
        secondPojoList.parallelStream().forEach(groupContent -> {
            MbdId id = new MysqlMbdId(groupContent.getGlobalId());
            ConcurrentSkipListMap<Long, GroupContent> list =
                categorizedContent.putIfAbsent(id, new ConcurrentSkipListMap<>());
            if (list == null) {
                list = categorizedContent.get(id);
            }
            list.put(groupContent.getItemIndex(), groupContent);
        });

        return firstPojoCollection.entrySet().parallelStream().collect(Collectors.toMap(
            Map.Entry::getKey,
            kv -> {
                MbdId id = kv.getKey();
                GroupRecord groupRecord = kv.getValue();

                PlainMdbGroup mdbGroup = new PlainMdbGroup();
                mdbGroup.setGlobalId(id);
                mdbGroup.setName(stringTypeSource.selectById(new MysqlMbdId(groupRecord.getGroupName())));
                mdbGroup.setUniqueItems(groupRecord.getUniqueItems());

                Map<Long, GroupContent> groupContentIndexMap = categorizedContent.get(id);
                List<MbdId> mbdIdList = new ArrayList<>(groupContentIndexMap.size());
                for (int i = 0; i < groupContentIndexMap.size(); i++) {
                    mbdIdList.add(new MysqlMbdId(groupContentIndexMap.get((long) i).getGlobalId()));
                }

                mdbGroup.setItems(mbdIdList);

                return mdbGroup;
            },
            (key1, key2) -> key2,
            LinkedHashMap::new
        ));


        // // 并行处理
        // MbdGroup[] resultArray = new MbdGroup[arrayLength];
        // IntStream.range(0, arrayLength).parallel().forEach(i -> {
        //     GroupRecord groupRecord = groupRecords.get(i);
        //     List<GroupContent> groupContentList = categorizedContent
        //         .get(groupRecord.getGlobalId())
        //         .values()
        //         .parallelStream()
        //         .toList();
        //     resultArray[i] = getValue(groupRecord, groupContentList);
        // });
        //
        // return Arrays.asList(resultArray);
    }

    @Override
    protected Condition getEqualIdCondition(MbdId id) {
        return new ConditionImpl().op(GroupContent.Fields.globalId, Op.eq, MysqlMbdId.getValue(id));
    }

    @Override
    protected Condition getEqualIdCondition(List<MbdId> idList) {
        return new ConditionImpl().op(
            GroupContent.Fields.globalId,
            Op.in,
            idList.parallelStream().map(MysqlMbdId::getValue).toList()
        );
    }

    @Override
    protected MbdId getTableNameId(Class<?> pojoClass) {
        return coreTableCache.getTableNameId(pojoClass);
    }

    @Override
    public MbdGroup getReactiveMbdGroup(MbdId globalId, ReactiveMode mode) {
        MbdGroup result = null;

        if (mode.getCacheFlag().isDisable()) {
            ReactiveNoCacheMbdGroup group = new ReactiveNoCacheMbdGroup(new MysqlMbdId(globalId), mode.getLockFlag().getBoolean());
            group.setGroupContentMapper(secondMapper);
            group.setStringTypeSource(stringTypeSource);
            result = group;
        } else {
            throw new UnsupportedOperationException();
        }

        return result;
    }


}
