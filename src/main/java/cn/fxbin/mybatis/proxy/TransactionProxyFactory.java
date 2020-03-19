package cn.fxbin.mybatis.proxy;

import net.sf.cglib.proxy.Enhancer;

/**
 * TransactionProxyFactory
 *
 * @author fxbin
 * @version v1.0
 * @since 2020/3/19 12:00
 */
public class TransactionProxyFactory {

    public static <T> T getCglibProxy(Object o){
        Enhancer enhancer = new Enhancer();
        // 设置代理类的父类
        enhancer.setSuperclass(o.getClass());
        // 设置回调对象
        enhancer.setCallback(new TransactionProxy());
        // 创建代理对象
        return (T) enhancer.create();
    }

}
