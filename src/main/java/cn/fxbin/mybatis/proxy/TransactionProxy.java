package cn.fxbin.mybatis.proxy;

import cn.fxbin.mybatis.DbTools;
import cn.fxbin.mybatis.annotation.Transaction;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * TransactionProxy
 *
 * @author fxbin
 * @version v1.0
 * @since 2020/3/19 11:58
 */
public class TransactionProxy implements MethodInterceptor {


    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("========= Cglib 动态处理开始 =========");

        Object result = null;

        // 判断方法是否添加 Transaction ,如果没有则不开启事务
        if (!method.isAnnotationPresent(Transaction.class)) {
            result = methodProxy.invokeSuper(o, objects);
        } else {
            Connection connection = TransactionManager.getConnection();
            System.out.println(connection.getClass());
            if (connection != null) {
                try {
                    connection.setAutoCommit(false);
                    result = methodProxy.invokeSuper(o, objects);
                    // 提交事务
                    connection.commit();
                    return result;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    if (connection != null) {
                        try {
                            connection.rollback();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                } finally {
                    if (connection != null) {
                        DbTools.release(connection, null, null);
                    }
                }
            }
        }

        System.out.println("========= Cglib 动态处理结束 ========= ");
        return result;
    }
}
