package cn.fxbin.mybatis.proxy;

import cn.fxbin.mybatis.proxy.MapperProxy;

import static java.lang.reflect.Proxy.newProxyInstance;

/**
 * MapperProxyFactory 实例化代理类
 *
 * @author fxbin
 * @version v1.0
 * @since 2020/3/19 10:45
 */
public class MapperProxyFactory {

    public static <T> T getMapper(Class<T> mapperInterface) {
        return (T) newProxyInstance(mapperInterface.getClassLoader(), new Class[] {mapperInterface}, new MapperProxy()) ;
    }

}
