package cn.fxbin.mybatis.test;

import cn.fxbin.mybatis.proxy.MapperProxyFactory;

/**
 * UserInfoService
 *
 * @author fxbin
 * @version v1.0
 * @since 2020/3/19 13:35
 */
public class UserInfoService {

    private UserInfoMapper userInfoMapper = MapperProxyFactory.getMapper(UserInfoMapper.class);

    public UserInfo getUserInfo(int id) {
        return userInfoMapper.getUserInfo(id);
    }

    public UserInfo updateUserInfo(String username, int id) {
        int i = 5/0;
        return userInfoMapper.updateUserInfo(username, id);
    }

}
