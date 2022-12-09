package cc.xfl12345.mybigdata.server.mysql.database.mapper.impl.bee.config;


import cc.xfl12345.mybigdata.server.common.pojo.ClassDeclaredInfo;
import cc.xfl12345.mybigdata.server.common.pojo.MbdId;
import cc.xfl12345.mybigdata.server.common.pojo.PropertyHelper;
import cc.xfl12345.mybigdata.server.mysql.pojo.MysqlMbdId;
import cc.xfl12345.mybigdata.server.mysql.pojo.PojoInfo;

import java.util.function.Supplier;

public class BeeTableMapperConfigGenerator {
    public static <Pojo> BeeTableMapperConfig<Pojo> getConfig(PojoInfo pojoInfo) {
        ClassDeclaredInfo classDeclaredInfo = pojoInfo.getClassDeclaredInfo();
        String idFieldName = classDeclaredInfo.getJpaIdFields().get(0);
        PropertyHelper propertyHelper = classDeclaredInfo.getPropertyInfoMap().get(idFieldName).getPropertyHelper();

        return new BeeTableMapperConfig<>() {
            @Override
            public String getTableName() {
                return pojoInfo.getTableName();
            }

            @Override
            public String getIdFieldName() {
                return idFieldName;
            }

            @Override
            public MysqlMbdId getId(Pojo pojo) {
                return new MysqlMbdId(propertyHelper.justGet(pojo));
            }

            @Override
            public void setId(Pojo pojo, MbdId<?> id) {
                propertyHelper.justSet(pojo, MysqlMbdId.getValue(id));
            }

            @SuppressWarnings("unchecked")
            @Override
            public Pojo getNewPojoInstance() {
                return (Pojo) pojoInfo.getNewPoInstance();
            }
        };
    }
}
