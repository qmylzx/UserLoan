package base.service;

import base.pojo.UserInfo;

public interface IUserInfoService {
    UserInfo selectUser(String loginName,String passWord);
}
