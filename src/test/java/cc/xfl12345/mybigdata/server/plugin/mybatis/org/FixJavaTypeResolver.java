package cc.xfl12345.mybigdata.server.plugin.mybatis.org;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;

public class FixJavaTypeResolver extends JavaTypeResolverDefaultImpl {

    @Override
    protected FullyQualifiedJavaType overrideDefaultType(IntrospectedColumn column, FullyQualifiedJavaType defaultType) {
        FullyQualifiedJavaType answer = null;

        switch (column.getJdbcType()) {
            case Types.TINYINT:
                answer = new FullyQualifiedJavaType(Short.class.getName());
                break;
            case Types.SMALLINT:
                answer = new FullyQualifiedJavaType(Integer.class.getName());
                break;
            case Types.INTEGER:
                answer = new FullyQualifiedJavaType(Long.class.getName());
                break;
            case Types.BIGINT:
                answer = new FullyQualifiedJavaType(BigInteger.class.getName());
                break;
            case Types.FLOAT:
                answer = new FullyQualifiedJavaType(Double.class.getName());
                break;
            case Types.DOUBLE:
            case Types.DECIMAL:
                answer = new FullyQualifiedJavaType(BigDecimal.class.getName());
            default:
                break;
        }

        return answer == null ? super.overrideDefaultType(column, defaultType) : answer;
    }
}
