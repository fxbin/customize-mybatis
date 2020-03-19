package cn.fxbin.mybatis.test;

/**
 * UserInfo
 *
 * @author fxbin
 * @version v1.0
 * @since 2020/3/19 10:49
 */
public class UserInfo {

    private Integer id;

    private String username;

    private String email;

    public UserInfo() {
    }

    public UserInfo(Integer id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
