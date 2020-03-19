package cn.fxbin.mybatis;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DbTools
 *
 * @author fxbin
 * @version v1.0
 * @since 2020/3/18 18:18
 */
public class DbTools {

    private static final DruidDataSource dataSource;

    static{
        dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        dataSource.setUrl("jdbc:mysql://192.168.7.94:3306/customize");
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(10);
    }


    /**
     * getDataSource
     *
     * @author fxbin
     * @since 2020/3/18 18:22
     * @return javax.sql.DataSource
     */
    public static DataSource getDataSource(){
        return dataSource;
    }


    /**
     * getConnection
     *
     * @author fxbin
     * @since 2020/3/18 18:22
     * @return java.sql.Connection
     */
    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public static int executeUpdate(String sql, Object ...args){
        try {
            return executeUpdate(getConnection(),sql,args);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * executeUpdate 增、删、改操作
     *
     * @author fxbin
     * @since 2020/3/19 10:34
     * @param connection java.sql.Connection
     * @param sql sql
     * @param args paramaters
     * @return int
     */
    public static int executeUpdate(Connection connection, String sql, Object ...args) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        int i = 1;
        for (Object arg : args) {
            preparedStatement.setObject(i, arg);
            i++;
        }
        return preparedStatement.executeUpdate();
    }

    public static <T> List<T> executeQuery(String sql, Class<T> classType, Object ...args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            int i = 1;
            for (Object arg : args) {
                preparedStatement.setObject(i,arg);
                i++;
            }
            resultSet = preparedStatement.executeQuery();
            return setMetaData(resultSet,classType);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            release(connection,preparedStatement,resultSet);
        }
        return new ArrayList<T>();
    }


    /**
     * setMetaData 反射将数据库结果转化为实体类
     *
     * @author fxbin
     * @since 2020/3/19 10:43
     * @param rs java.sql.ResultSet
     * @param clazz java.lang.Class
     * @return java.util.List<T>
     */
    private static <T> List<T> setMetaData(ResultSet rs, Class<T> clazz) throws Exception {
        List<T> list = new ArrayList<T>();
        while (rs.next()) {
            T t = clazz.newInstance();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = rsmd.getColumnName(i);
                Field field = clazz.getDeclaredField(columnName);
                field.setAccessible(true);
                field.set(t, rs.getObject(columnName));
            }
            list.add(t);
        }
        return list;
    }


    /**
     * release 释放资源
     *
     * @author fxbin
     * @since 2020/3/19 10:34
     * @param connection java.sql.Connection
     * @param preparedStatement java.sql.PreparedStatement
     * @param resultSet java.sql.ResultSet
     */
    public static void release(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet){
        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(preparedStatement != null){
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



}
