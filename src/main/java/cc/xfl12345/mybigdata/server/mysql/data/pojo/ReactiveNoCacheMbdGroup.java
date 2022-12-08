package cc.xfl12345.mybigdata.server.mysql.data.pojo;

import cc.xfl12345.mybigdata.server.common.appconst.CURD;
import cc.xfl12345.mybigdata.server.common.appconst.DefaultSingleton;
import cc.xfl12345.mybigdata.server.common.data.source.StringTypeSource;
import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdGroup;
import cc.xfl12345.mybigdata.server.common.database.mapper.TableBasicMapper;
import cc.xfl12345.mybigdata.server.common.pojo.AffectedRowsCountChecker;
import cc.xfl12345.mybigdata.server.common.pojo.FieldNotNullChecker;
import cc.xfl12345.mybigdata.server.mysql.appconst.CoreTableNames;
import cc.xfl12345.mybigdata.server.mysql.database.converter.AppIdTypeConverter;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GlobalDataRecord;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GroupContent;
import cc.xfl12345.mybigdata.server.mysql.database.pojo.GroupRecord;
import lombok.Getter;
import lombok.Setter;
import org.teasoft.bee.osql.*;
import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.ConditionImpl;

import javax.annotation.PostConstruct;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ReactiveNoCacheMbdGroup implements MbdGroup {
    protected final Object globalId;

    protected boolean lockFlag = false;

    protected ReactiveList reactiveList;

    @Override
    public void setItems(List<Object> items) {
        reactiveList.clear();
        reactiveList.addAll(items);
    }

    @Override
    public List<Object> getItems() {
        return reactiveList;
    }

    @Getter
    @Setter
    protected FieldNotNullChecker fieldNotNullChecker = DefaultSingleton.FIELD_NOT_NULL_CHECKER;

    @Getter
    @Setter
    protected AffectedRowsCountChecker affectedRowsCountChecker = DefaultSingleton.AFFECTED_ROWS_COUNT_CHECKER;

    @Getter
    @Setter
    protected StringTypeSource stringTypeSource;

    @Getter
    @Setter
    protected TableBasicMapper<GroupRecord> groupRecordMapper;

    @Getter
    @Setter
    protected TableBasicMapper<GroupContent> groupContentMapper;

    @Getter
    @Setter
    protected AppIdTypeConverter idTypeConverter;


    public ReactiveNoCacheMbdGroup(Object globalId, boolean lockFlag) {
        this.globalId = globalId;
        this.lockFlag = lockFlag;

        if (lockFlag) {
            lockRow();
        }

        reactiveList = new ReactiveList();
    }

    @PostConstruct
    public void init() throws Exception {
        fieldNotNullChecker.check(groupRecordMapper, GroupRecord.class);
        fieldNotNullChecker.check(groupContentMapper, GroupContent.class);
        fieldNotNullChecker.check(idTypeConverter, "idTypeConverter");
        fieldNotNullChecker.check(stringTypeSource, String.class);
    }

    @Override
    public void destoryInstance() {
        unlockRow();
    }

    @Override
    public Object getGlobalId() {
        return globalId;
    }

    protected SuidRich getSuidRich() {
        return BeeFactory.getHoneyFactory().getSuidRich();
    }

    protected void checkAffectedRowShouldBeOne(long affectedRowsCount, CURD operation) {
        affectedRowsCountChecker.checkAffectedRowShouldBeOne(affectedRowsCount, operation, CoreTableNames.GROUP_CONTENT);
    }

    protected void checkAffectedRowsCountDoesNotMatch(long affectedRowsCount, long expectAffectedRowsCount, CURD operation) {
        affectedRowsCountChecker.checkAffectedRowsCountDoesNotMatch(affectedRowsCount, expectAffectedRowsCount, operation, CoreTableNames.GROUP_CONTENT);
    }

    protected List<Object> getItemList() {
        // 加载所有成员
        SuidRich suidRich = getSuidRich();
        Condition condition = new ConditionImpl();
        condition.op(GroupContent.Fields.globalId, Op.eq, globalId);
        condition.orderBy(GroupContent.Fields.itemIndex, OrderType.ASC);
        List<GroupContent> contents = suidRich.select(new GroupContent(), condition);
        List<Object> cacheList = new CopyOnWriteArrayList<>(new Object[contents.size()]);
        contents.parallelStream().forEach(groupContent -> {
            cacheList.set(groupContent.getItemIndex().intValue(), groupContent.getItem());
        });

        return cacheList;
    }

    @Override
    public String getName() {
        return stringTypeSource.selectById(
            groupRecordMapper.selectById(globalId, GroupRecord.Fields.groupName).getGroupName()
        );
    }

    @Override
    public void setName(String name) {
        Object id = stringTypeSource.insert4IdOrGetId(name);

        SuidRich suidRich = getSuidRich();
        Condition condition = new ConditionImpl();
        condition.setIncludeType(IncludeType.EXCLUDE_BOTH);
        GroupRecord groupRecord = new GroupRecord();
        groupRecord.setGlobalId(idTypeConverter.convert(globalId));
        groupRecord.setGroupName(idTypeConverter.convert(id));
        suidRich.updateById(new GroupRecord(), condition);
    }

    @Override
    public boolean isUniqueItems() {
        return groupRecordMapper.selectById(globalId).getUniqueItems();
    }

    @Override
    public void setUniqueItems(boolean unique) {
        SuidRich suidRich = getSuidRich();
        Condition condition = new ConditionImpl();
        condition.setIncludeType(IncludeType.EXCLUDE_BOTH);
        GroupRecord groupRecord = new GroupRecord();
        groupRecord.setGlobalId(idTypeConverter.convert(globalId));
        groupRecord.setUniqueItems(unique);
        suidRich.updateById(new GroupRecord(), condition);
    }

    protected void lockRow() {
        SuidRich suidRich = getSuidRich();
        Condition condition = new ConditionImpl();
        condition.forUpdate();
        GlobalDataRecord globalDataRecord = new GlobalDataRecord();
        globalDataRecord.setId(idTypeConverter.convert(globalId));
        suidRich.selectOne(globalDataRecord);
    }

    protected void unlockRow() {
    }


    public class ReactiveList extends AbstractList<Object> {
        @Override
        public int size() {
            SuidRich suidRich = getSuidRich();
            Condition condition = new ConditionImpl();
            condition.op(GroupContent.Fields.globalId, Op.eq, globalId);
            return suidRich.count(new GroupContent(), condition);
        }

        @Override
        public boolean contains(Object id) {
            return getSuidRich().selectOne(GroupContent.builder().item(idTypeConverter.convert(id)).build()) != null;
        }

        @Override
        public boolean add(Object id) {
            int affectedRowCount = getSuidRich().insert(
                GroupContent.builder()
                    .globalId(idTypeConverter.convert(globalId))
                    .itemIndex((long) size())
                    .item(idTypeConverter.convert(id))
                    .build()
            );

            return affectedRowCount == 1;
        }

        @Override
        public boolean remove(Object id) {
            SuidRich suidRich = getSuidRich();
            GroupContent content = suidRich.selectOne(
                GroupContent.builder()
                    .globalId(idTypeConverter.convert(globalId))
                    .item(idTypeConverter.convert(id))
                    .build()
            );

            if (content != null) {
                int affectedRowCount = suidRich.delete(content);
                if (affectedRowCount == 1) {
                    Condition condition = new ConditionImpl();
                    condition.op(GroupContent.Fields.itemIndex, Op.greatThan, content.getItemIndex());
                    condition.setAdd(GroupContent.Fields.itemIndex, -1);
                    affectedRowCount = suidRich.update(new GroupContent(), condition);
                    return affectedRowCount == 1;
                }
            }

            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            // TODO
            return false;
        }

        @Override
        public boolean addAll(Collection<?> c) {
            List<GroupContent> contents = new ArrayList<>(c.size());

            int i = size();
            for (Object item : c) {
                contents.add(GroupContent.builder()
                    .itemIndex((long) i)
                    .item(idTypeConverter.convert(item))
                    .build()
                );
                i++;
            }

            long affectedRowsCount = getSuidRich().insert(contents);

            affectedRowsCountChecker.checkAffectedRowsCountDoesNotMatch(
                affectedRowsCount,
                c.size(),
                CURD.CREATE,
                CoreTableNames.GROUP_CONTENT
            );

            return true;
        }

        @Override
        public boolean addAll(int index, Collection<?> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public Object get(int index) {
            return null;
        }

        @Override
        public Object set(int index, Object element) {
            return null;
        }

        @Override
        public void add(int index, Object element) {


        }

        @Override
        public Object remove(int index) {
            return null;
        }

        @Override
        public int indexOf(Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }


        @Override
        public List<Object> subList(int fromIndex, int toIndex) {
            return super.subList(fromIndex, toIndex);
        }
    }
}
