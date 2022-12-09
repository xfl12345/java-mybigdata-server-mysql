package cc.xfl12345.mybigdata.server.mysql.data.source.impl;

import cc.xfl12345.mybigdata.server.common.data.source.DataSource;
import cc.xfl12345.mybigdata.server.common.data.source.GroupTypeSource;
import cc.xfl12345.mybigdata.server.common.data.source.StringTypeSource;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.CommonMbdId;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.CommonMdbGroup;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdGroup;
import cc.xfl12345.mybigdata.server.common.pojo.MbdId;
import cc.xfl12345.mybigdata.server.common.pojo.ReactiveMode;
import cc.xfl12345.mybigdata.server.mysql.data.pojo.ReactiveNoCacheMbdGroup;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.AbstractBeeTripleLayerTableDataSource;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.MysqlMbdGroup;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.raw.AbstractTripleLayerTableRawDataSource;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GroupContent;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GroupRecord;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;
import lombok.Getter;
import lombok.Setter;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.OrderType;
import org.teasoft.honey.osql.core.ConditionImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.IntStream;

public class GroupTypeSourceImpl
    extends AbstractBeeTripleLayerTableDataSource<CommonMdbGroup, GroupRecord, GroupContent>
    implements GroupTypeSource {
    @Getter
    @Setter
    protected StringTypeSource stringTypeSource;

    @Override
    protected DataSource<CommonMdbGroup> generateRawImpl() {
        return new AbstractTripleLayerTableRawDataSource<>(globalDataRecordDataSource, firstMapper, secondMapper) {
            @Override
            public MbdId<?> selectId(CommonMdbGroup mbdGroup) {
                return null;
            }

            @Override
            public Class<CommonMdbGroup> getValueType() {
                return CommonMdbGroup.class;
            }

            @Override
            protected GroupRecord getFirstPojo(MbdId<?> globalId, CommonMdbGroup objects) {
                return GroupRecord.builder()
                    .globalId(MysqlMbdId.getValue(globalId))
                    .groupName(MysqlMbdId.getValue(stringTypeSource.insert4IdOrGetId(objects.getName())))
                    .uniqueItems(objects.isUniqueItems())
                    .build();
            }

            @Override
            protected List<GroupContent> getSecondPojo(MbdId<?> globalId, CommonMdbGroup objects) {
                List<CommonMbdId> itemList = objects.getItems();
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
            protected CommonMdbGroup getValue(GroupRecord groupRecord, List<GroupContent> groupContents) {
                CommonMdbGroup mbdGroup = new CommonMdbGroup();
                mbdGroup.setGlobalId(new CommonMbdId(groupRecord.getGlobalId()));
                mbdGroup.setName(stringTypeSource.selectById(new MysqlMbdId(groupRecord.getGroupName())));
                mbdGroup.setUniqueItems(groupRecord.getUniqueItems());
                mbdGroup.setItems(groupContents.parallelStream().map(
                    item -> new CommonMbdId(item.getItem())
                ).toList());

                return mbdGroup;
            }

            @Override
            protected List<CommonMdbGroup> getValue(List<GroupRecord> groupRecords, List<GroupContent> groupContents) {
                int arrayLength = groupRecords.size();
                CommonMdbGroup[] resultArray = new CommonMdbGroup[arrayLength];

                // 先给 GroupContent 根据 id 分开来，随便排个序
                Map<Object, ConcurrentSkipListMap<Long, GroupContent>> categorizedContent = new ConcurrentHashMap<>(arrayLength);
                groupContents.parallelStream().forEach(groupContent -> {
                    Object id = groupContent.getGlobalId();
                    ConcurrentSkipListMap<Long, GroupContent> list =
                        categorizedContent.putIfAbsent(id, new ConcurrentSkipListMap<>());
                    if (list == null) {
                        list = categorizedContent.get(id);
                    }
                    list.put(groupContent.getItemIndex(), groupContent);
                });

                // 并行处理
                IntStream.range(0, arrayLength).parallel().forEach(i -> {
                    GroupRecord groupRecord = groupRecords.get(i);
                    List<GroupContent> groupContentList = categorizedContent
                        .get(groupRecord.getGlobalId())
                        .values()
                        .parallelStream()
                        .toList();
                    resultArray[i] = getValue(groupRecord, groupContentList);
                });

                return Arrays.asList(resultArray);
            }

            @Override
            protected Condition getEqualIdCondition(MbdId<?> id) {
                return new ConditionImpl().op(GroupContent.Fields.globalId, Op.eq, MysqlMbdId.getValue(id));
            }

            @Override
            protected Condition getEqualIdAndSortCondition(MbdId<?> id) {
                return getEqualIdCondition(id).orderBy(GroupContent.Fields.itemIndex, OrderType.ASC);
            }

            @Override
            protected Condition getEqualIdCondition(List<MbdId<?>> idList) {
                return new ConditionImpl().op(
                    GroupContent.Fields.globalId,
                    Op.in,
                    idList.parallelStream().map(MysqlMbdId::getValue).toList()
                );
            }
        };
    }

    @Override
    public MbdGroup<MbdId<?>> getReactiveMbdGroup(CommonMbdId globalId, ReactiveMode mode) {
        MbdGroup<MbdId<?>> result;

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
