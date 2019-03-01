package base.pojo;

import com.google.gson.Gson;

public class UserInfo {
    private String id;	// 主键
    private String loginName;	// 登录名
    private String passWord;	// 登录密码
    private String role;  //管理员0 老师1 学生2
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
