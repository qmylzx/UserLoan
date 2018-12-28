package base.dao;

import base.pojo.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserInfoDao {
    UserInfo selectUser(@Param("loginName") String loginName,@Param("passWord")  String passWord);
}
