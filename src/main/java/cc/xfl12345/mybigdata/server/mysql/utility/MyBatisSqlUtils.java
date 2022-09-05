package cc.xfl12345.mybigdata.server.mysql.utility;

import com.mysql.cj.PreparedQuery;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

public class MyBatisSqlUtils {
    public static String getSql(PreparedStatement preparedStatement) {
        String sql;
        com.mysql.cj.jdbc.ClientPreparedStatement unwarpedPreparedStatement;
        try {
            unwarpedPreparedStatement = preparedStatement.unwrap(com.mysql.cj.jdbc.ClientPreparedStatement.class);
            sql = ((PreparedQuery) unwarpedPreparedStatement.getQuery()).asSql();
        } catch (Exception e) {
            try {
                Method method = preparedStatement.getClass().getMethod("asSql");
                sql = (String) method.invoke(preparedStatement);
            } catch (Exception exception) {
                sql = preparedStatement.toString();
            }
        }
        return sql;
    }

    /**
     * 给定实体类和mapper xml里的id，构造完整的sql语句
     *
     * @param sqlSessionFactory Mybatis 的 SqlSessionFactory
     * @param daoClass          mapper xml 里指定的 dao接口
     * @param id                mapper xml 里 dao接口 的 函数 的 id
     * @param parameterObject   数据库表对应的实体类
     * @return sql语句
     * @throws Exception
     * @
     */
    public static String getSql(
        SqlSessionFactory sqlSessionFactory,
        Class<?> daoClass,
        String id,
        Object parameterObject) throws Exception {
        id = daoClass.getName() + "." + id;
        Configuration configuration = sqlSessionFactory.getConfiguration();
        MappedStatement mappedStatement = configuration.getMappedStatement(id);
        BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
        ParameterHandler parameterHandler = configuration.newParameterHandler(mappedStatement, parameterObject, boundSql);
        Connection connection = sqlSessionFactory.openSession().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());
        connection.close();
        parameterHandler.setParameters(preparedStatement);
        String sql = getSql(preparedStatement);
        preparedStatement.close();
        return sql;
    }

    /**
     * 给定实体类和mapper xml里的id，构造完整的sql语句
     *
     * @param sqlSessionFactory Mybatis 的 SqlSessionFactory
     * @param daoClass          mapper xml 里指定的 dao接口
     * @param id                mapper xml 里 dao接口 的 函数 的 id
     * @param parameters        一堆数据库表对应的实体类
     * @return sql语句
     * @throws Exception
     * @
     */
    public static String getBatchInsertSql(
        SqlSessionFactory sqlSessionFactory,
        Class<?> daoClass,
        String id,
        ArrayList<?> parameters) throws Exception {
        Object parameterObject = parameters.get(0);
        id = daoClass.getName() + "." + id;
        Configuration configuration = sqlSessionFactory.getConfiguration();
//        configuration.setCacheEnabled(false);
        MappedStatement mappedStatement = configuration.getMappedStatement(id);
        BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
        ParameterHandler parameterHandler = configuration.newParameterHandler(mappedStatement, parameterObject, boundSql);

        //获取 PreparedStatement
        SqlSession sqlSession = sqlSessionFactory.openSession();
//        sqlSession.clearCache();
        Connection connection = sqlSession.getConnection();
        String sql = boundSql.getSql();
        String parameterStatement = sql.substring(sql.lastIndexOf('('));
        PreparedStatement parameterPreparedStatement = connection.prepareStatement(parameterStatement);

        StringBuilder stringBuilder = new StringBuilder(sql.substring(0, sql.lastIndexOf('(')));
        int itemCount = parameters.size();
        if (itemCount == 1) {
            parameterHandler.setParameters(parameterPreparedStatement);
            stringBuilder.append(getSql(parameterPreparedStatement));
        } else {
            int i = 0;
            for (; i < itemCount; i++) {
                parameterObject = parameters.get(i);
                parameterHandler = configuration.newParameterHandler(
                    mappedStatement,
                    parameterObject,
                    mappedStatement.getBoundSql(parameterObject)
                );
                parameterHandler.setParameters(parameterPreparedStatement);
                stringBuilder.append(getSql(parameterPreparedStatement));
                stringBuilder.append(',');
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append(";");
        parameterPreparedStatement.close();
        connection.close();
        return stringBuilder.toString();
    }


}
