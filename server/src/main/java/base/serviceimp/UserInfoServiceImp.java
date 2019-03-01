package base.serviceimp;

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

    @Override
    public int registerUser(String loginName, String passWord) {
        return iUserInfoDao.registerUser(loginName,passWord);
    }

    @Override
    public int updateUser(String loginName, String passWord, String id) {
        return iUserInfoDao.updateUser(loginName,passWord,id);
    }
}
