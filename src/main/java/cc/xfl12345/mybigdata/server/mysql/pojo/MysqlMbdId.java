package cc.xfl12345.mybigdata.server.mysql.pojo;

import cc.xfl12345.mybigdata.server.common.pojo.MbdId;

public class MysqlMbdId extends MbdId<Long> {
    public MysqlMbdId(Long id) {
        super(id);
        this.theLongValue = id;
    }

    public MysqlMbdId(Object id) {
        super(null);
        if (id instanceof Long theLong) {
            this.idRawValue = theLong;
            this.theLongValue = theLong;
        } else if (id != null) {
            throw new IllegalArgumentException("Param type should be '" + getOriginType().getCanonicalName() + "' !");
        }
    }

    public MysqlMbdId(MbdId<?> id) {
        this(id.getLongValue());
    }

    @Override
    public Class<Long> getOriginType() {
        return Long.class;
    }

    public static Long getValue(MbdId<?> id) {
        return id == null ? null : id.getLongValue();
    }
}
