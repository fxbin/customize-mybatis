package cn.fxbin.mybatis.test;

import cn.fxbin.mybatis.proxy.MapperProxyFactory;
import cn.fxbin.mybatis.proxy.TransactionProxyFactory;

/**
 * MybatisTest
 *
 * @author fxbin
 * @version v1.0
 * @since 2020/3/19 10:49
 */
public class MybatisTest {

    public static void main(String[] args) {
        UserInfoService userInfoService = TransactionProxyFactory.getCglibProxy(new UserInfoService());
        UserInfo userInfo = userInfoService.getUserInfo(1);
        System.out.println(userInfo.toString());
        try {
            userInfoService.updateUserInfo("小*",1);
        }catch (Exception e){
            e.printStackTrace();
        }
        UserInfo userInfo1 = userInfoService.getUserInfo(1);
        System.out.println(userInfo1.toString());
    }

}
