package base.dao;

import base.pojo.MoneyAndTime;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface IPayDao {
    List<MoneyAndTime> getPay(@Param("id") String id);
}
