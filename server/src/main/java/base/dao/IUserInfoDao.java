package base.dao;

import base.pojo.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserInfoDao  {
    UserInfo selectUser(@Param("loginName") String loginName,@Param("passWord")  String passWord);
    int registerUser(@Param("loginName") String loginName,@Param("passWord")  String passWord);
    int updateUser(@Param("loginName") String loginName,@Param("passWord")  String passWord,@Param("id")String id);
}
