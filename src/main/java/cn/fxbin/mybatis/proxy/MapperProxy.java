package cn.fxbin.mybatis.proxy;

import cn.fxbin.mybatis.DbTools;
import cn.fxbin.mybatis.annotation.Select;
import cn.fxbin.mybatis.annotation.Update;
import cn.fxbin.mybatis.test.UserInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * MapperProxy mybatis代理
 *
 * @author fxbin
 * @version v1.0
 * @since 2020/3/19 10:44
 */
public class MapperProxy implements InvocationHandler {

    /**
     * invoke
     *
     * @author fxbin
     * @since 2020/3/19 10:58
     * @param proxy 代理类
     * @param method 目标对象方法
     * @param args method parameters
     * @return java.lang.Object
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("进入cn.fxbin.mybatis.proxy.MapperProxy.invoke");
        Class<?> returnType = method.getReturnType();
        if (method.isAnnotationPresent(Select.class)) {
            Select select = method.getAnnotation(Select.class);
            String sql = select.value();
            System.out.println("sql print ===> " +sql);
            System.out.println("execute sql return type ===> " + returnType);
            //执行数据库的jdbc操作获取信息
            return selectOne(sql, returnType, args);
        } else if (method.isAnnotationPresent(Update.class)) {
            Update update = method.getAnnotation(Update.class);
            String sql = update.value();
            System.out.println("sql print ===> " +sql);
            System.out.println("execute sql return type ===> " + returnType);

        }
        return new UserInfo(1, "test", "test");
    }

    private int update(String sql, Object ...args) throws SQLException {
        Connection connection = TransactionManager.getConnection();
        return DbTools.executeUpdate(connection, sql, args);
    }

    private <T> T selectOne(String sql, Class<T> returnType,Object ...args){
        List<T> list = selectList(sql, returnType, args);
        if(list.size() == 0){
            return null;
        }
        return list.get(0);
    }

    private <T> List<T> selectList(String sql, Class<T> returnType,Object ...args){
        return DbTools.executeQuery(sql, returnType, args);
    }

}
