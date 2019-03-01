package base.serviceimp;

import base.dao.IUserDao;
import base.pojo.User;
import base.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImp implements IUserService {
    @Autowired
    IUserDao iUserDao;

    @Override
    public List<User> showAll() {
        return iUserDao.showAll();
    }

    @Override
    public User getUser(String id) {
        return iUserDao.getUser(id);
    }

    @Override
    public int addWhite(String id, String userId) {
        return iUserDao.addWhite(id,userId);
    }

    @Override
    public int removeWhite(String userId) {
        return iUserDao.removeWhite(userId);
    }

    @Override
    public List<User> getWhite(String id) {
        return iUserDao.getWhite(id);
    }
}
