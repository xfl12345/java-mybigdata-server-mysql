package cc.xfl12345.mybigdata.server.mysql.data.source.impl;

import cc.xfl12345.mybigdata.server.common.data.source.DataSource;
import cc.xfl12345.mybigdata.server.common.data.source.GroupTypeSource;
import cc.xfl12345.mybigdata.server.common.data.source.StringTypeSource;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdGroup;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.PlainMdbGroup;
import cc.xfl12345.mybigdata.server.common.pojo.ReactiveMode;
import cc.xfl12345.mybigdata.server.mysql.data.pojo.ReactiveNoCacheMbdGroup;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.raw.AbstractTripleLayerTableRawDataSource;
import cc.xfl12345.mybigdata.server.mysql.data.source.base.AbstractBeeTripleLayerTableDataSource;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GroupContent;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GroupRecord;
import lombok.Getter;
import lombok.Setter;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.OrderType;
import org.teasoft.honey.osql.core.ConditionImpl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.IntStream;

public class GroupTypeSourceImpl
    extends AbstractBeeTripleLayerTableDataSource<MbdGroup, GroupRecord, GroupContent>
    implements GroupTypeSource {
    @Getter
    @Setter
    protected StringTypeSource stringTypeSource;

    @Override
    protected DataSource<MbdGroup> generateRawImpl() {
        return new AbstractTripleLayerTableRawDataSource<>(globalDataRecordDataSource, firstMapper, secondMapper) {
            @Override
            public Object selectId(MbdGroup mbdGroup) {
                return null;
            }

            @Override
            public Class<MbdGroup> getValueType() {
                return MbdGroup.class;
            }

            // @Override
            // protected GroupRecord getFirstPojo(MbdGroup objects) {
            //     return GroupRecord.builder()
            //         .globalId(idTypeConverter.convert(objects.getGlobalId()))
            //         .groupName(idTypeConverter.convert(stringTypeSource.insert4IdOrGetId(objects.getName())))
            //         .uniqueItems(objects.isUniqueItems())
            //         .build();
            // }

            @Override
            protected GroupRecord getFirstPojo(Object globalId, MbdGroup objects) {
                return GroupRecord.builder()
                    .globalId(idTypeConverter.convert(globalId))
                    .groupName(idTypeConverter.convert(stringTypeSource.insert4IdOrGetId(objects.getName())))
                    // .groupName(2L)
                    .uniqueItems(objects.isUniqueItems())
                    .build();
            }

            @Override
            protected List<GroupContent> getSecondPojo(Object globalId, MbdGroup objects) {
                List<Object> itemList = objects.getItems();
                int arrayLength = itemList.size();
                List<GroupContent> groupContentList = new ArrayList<>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    GroupContent groupContent = new GroupContent();
                    groupContent.setGlobalId(idTypeConverter.convert(globalId));
                    groupContent.setItemIndex((long) i);
                    groupContent.setItem(idTypeConverter.convert(itemList.get(i)));
                    groupContentList.add(groupContent);
                }

                return groupContentList;
            }

            @Override
            protected MbdGroup getValue(GroupRecord groupRecord, List<GroupContent> groupContents) {
                PlainMdbGroup mbdGroup = new PlainMdbGroup();
                mbdGroup.setGlobalId(groupRecord.getGlobalId());
                mbdGroup.setName(stringTypeSource.selectById(groupRecord.getGroupName()));
                mbdGroup.setUniqueItems(groupRecord.getUniqueItems());
                mbdGroup.setItems(groupContents.parallelStream().map(
                    item -> (Object) item.getItem()
                ).toList());

                return mbdGroup;
            }

            @Override
            protected List<MbdGroup> getValue(List<GroupRecord> groupRecords, List<GroupContent> groupContents) {
                int arrayLength = groupRecords.size();
                MbdGroup[] resultArray = new MbdGroup[arrayLength];

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
                        .get(groupRecord.getGlobalId()).values().parallelStream().toList();
                    resultArray[i] = getValue(groupRecord, groupContentList);
                });

                return Arrays.asList(resultArray);
            }

            @Override
            protected Condition getEqualIdCondition(Object id) {
                return new ConditionImpl().op(GroupContent.Fields.globalId, Op.eq, id);
            }

            @Override
            protected Condition getEqualIdAndSortCondition(Object id) {
                return getEqualIdCondition(id).orderBy(GroupContent.Fields.itemIndex, OrderType.ASC);
            }

            @Override
            protected Condition getEqualIdCondition(List<Object> idList) {
                return new ConditionImpl().op(GroupContent.Fields.globalId, Op.in, idList);
            }
        };
    }

    @Override
    public MbdGroup getReactiveMbdGroup(Object globalId, ReactiveMode mode) {
        MbdGroup result;

        if (mode.getCacheFlag().isDisable()) {
            ReactiveNoCacheMbdGroup group = new ReactiveNoCacheMbdGroup(globalId, mode.getLockFlag().getBoolean());
            group.setGroupContentMapper(secondMapper);
            group.setIdTypeConverter(idTypeConverter);
            group.setStringTypeSource(stringTypeSource);
            result = group;
        } else {
            throw new UnsupportedOperationException();
        }

        return result;
    }

    @Override
    public Class<MbdGroup> getValueType() {
        return MbdGroup.class;
    }
}
