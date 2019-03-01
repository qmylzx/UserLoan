package base.utils;

import base.utils.thrift.ResultService;
import base.utils.thrift.ResultServiceImpl;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;

public class Server {
    public static void main(String[] args) throws Exception {
        TNonblockingServerSocket socket = new TNonblockingServerSocket(9999);

        THsHaServer.Args arg = new THsHaServer.Args(socket).minWorkerThreads(2).maxWorkerThreads(4);

        ResultService.Processor<ResultServiceImpl> processor = new ResultService.Processor<ResultServiceImpl>(new ResultServiceImpl());

        arg.protocolFactory(new TCompactProtocol.Factory()); //协议层(application )  TCompactProtocol二进制压缩

        arg.transportFactory(new TFramedTransport.Factory());//client same  传输层对象

        arg.processorFactory(new TProcessorFactory(processor));

        TServer tServer = new THsHaServer(arg); //HsHa  半同步半异步

        System.out.println("start");
        tServer.serve();
    }
}
