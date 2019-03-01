package base.utils.thrift;

import org.apache.thrift.TException;

public class ResultServiceImpl implements ResultService.Iface{
    @Override
    public double getPredict(int id) throws DataException, TException {
        System.out.println("getPredict");
        return 0;
    }
}
