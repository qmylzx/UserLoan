package base.service.imp;

import base.dao.IUserInfoDao;
import base.pojo.UserInfo;
import base.service.IUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImp implements IUserInfoService {
    @Autowired
    IUserInfoDao iUserInfoDao;

    @Override
    public UserInfo selectUser(String loginName,String passWord) {
        return iUserInfoDao.selectUser(loginName,passWord);
    }
}
