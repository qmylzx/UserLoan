package base.dao;

import base.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserDao {
    List<User> showAll();
    User getUser(@Param("id")String id);
    int addWhite(@Param("id")String id,@Param("userId")String userId);
    int removeWhite(@Param("userId")String userId);
    List<User> getWhite(@Param("id")String id);
}
