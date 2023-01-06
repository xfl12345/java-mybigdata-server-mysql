package cc.xfl12345.mybigdata.server.mysql.pojo;

import cc.xfl12345.mybigdata.server.common.data.source.pojo.MbdId;

public class MysqlMbdId extends MbdId {
    public MysqlMbdId(Long id) {
        super(id);
        this.theLongValue = id;
    }

    public MysqlMbdId(MbdId id) {
        this(id.getLongValue());
    }

    @Override
    public Class<?> getIdType() {
        return Long.class;
    }

    public static Long getValue(MbdId id) {
        return id == null ? null : id.getLongValue();
    }
}
