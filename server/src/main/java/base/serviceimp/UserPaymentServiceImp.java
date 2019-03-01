package base.serviceimp;

import base.dao.IBehaviorDao;
import base.dao.IIncomeDao;
import base.dao.IPayDao;
import base.pojo.Behavior;
import base.pojo.MoneyAndTime;

import base.service.IUserPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserPaymentServiceImp implements IUserPaymentService {
    @Autowired
    IIncomeDao iIncomeDao;

    @Autowired
    IPayDao iPayDao;

    @Autowired
    IBehaviorDao iBehaviorDao;

    @Override
    public List<MoneyAndTime> getIncome(String id) {
        return iIncomeDao.getIncome(id);
    }

    @Override
    public List<MoneyAndTime> getPay(String id) {
        return iPayDao.getPay(id);
    }

    @Override
    public List<Behavior> getBehavior(String id) {
        return iBehaviorDao.getBehavior(id);
    }
}
