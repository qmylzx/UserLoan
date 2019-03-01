package base.service;

import base.pojo.User;

import java.util.List;

public interface IUserService {
    List<User> showAll();
    User getUser(String id);
    int addWhite(String id,String userId);
    int removeWhite(String userId);
    List<User> getWhite(String id);
}
