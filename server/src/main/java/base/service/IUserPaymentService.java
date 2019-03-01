package base.service;

import base.pojo.Behavior;
import base.pojo.MoneyAndTime;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IUserPaymentService {
    List<MoneyAndTime> getIncome(String id);

    List<MoneyAndTime> getPay(@Param("id") String id);

    List<Behavior> getBehavior(@Param("id") String id);
}
