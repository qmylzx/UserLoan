package base.service;

import base.pojo.UserInfo;

public interface IUserInfoService {
    UserInfo selectUser(String loginName,String passWord);
    int registerUser(String loginName,String passWord);
    int updateUser(String loginName,String passWord,String id);
}
