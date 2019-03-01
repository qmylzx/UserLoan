package base.utils;

import base.utils.thrift.ResultService;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;


public class Thrift {
     public static double getResult(int id){
        TTransport tTransport = new TFramedTransport(new TSocket("localhost",9999),600);
        TProtocol protocol = new TCompactProtocol(tTransport);
        ResultService.Client client = new ResultService.Client(protocol);
        double res = -1;
        try {
            tTransport.open();
            res = client.getPredict(id);
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            tTransport.close();
        }
        return res;
    }

    public static void main(String[] args) {
        System.out.println(Thrift.getResult(55602));
    }
}
