package cn.fxbin.mybatis.test;

import cn.fxbin.mybatis.annotation.Select;
import cn.fxbin.mybatis.annotation.Update;

/**
 * UserInfoMapper
 *
 * @author fxbin
 * @version v1.0
 * @since 2020/3/19 10:49
 */
public interface UserInfoMapper {

    @Select("select * from user_info where id = ?")
    UserInfo getUserInfo(int id);

    @Update("update user_info set name=? where id = ?")
    UserInfo updateUserInfo(String name, int id);


}
