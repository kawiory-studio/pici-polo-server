package pl.dom3k.picipolo.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Class containing logic behind connections with users - ServerSocket and Thread Pool.
 * Created by Januszek on 2016-05-10.
 * @author Kacper Jawoszek
 */
@SuppressWarnings("InfiniteLoopStatement")
public class Connector {

    private static LinkedList<SingleRequest> readyPool;
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "unused", "FieldCanBeLocal"})
    private static LinkedList<SingleRequest> activePool;
    private static Thread mainThread = null;
    private static final Object monitor = new Object();

    /**
     * Method containing initialization of thread pool/ServerSocket and infinite loop handling sockets.
     */
    public static void runServerSocket(){
        mainThread = Thread.currentThread();
        readyPool = new LinkedList<>();
        for (int i =0;i<5;i++){
            SingleRequest a = new SingleRequest();
            readyPool.push(a);
            a.start();
        }
        activePool = new LinkedList<>();
        ServerSocket sS = null;
        try {
            sS = new ServerSocket(5432);
            System.out.println("Server ready.");
            Socket s;
            while(true){
                try {
                    s = sS.accept();
                    SingleRequest sR;
                    while ((sR=getFreeRequest())==null){
                        try {
                            synchronized (monitor) {
                                monitor.wait(5000);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    sR.setRequest(s);
                    sR.wake();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if (sS!=null) try {
                sS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reset given thread and put into pool.
     * @param sR thread to reset.
     */
    public static synchronized void addFreeRequest(SingleRequest sR){
        try {
            readyPool.addLast(sR.resetRequest());
        } catch (IOException e) {
            readyPool.addLast(new SingleRequest());
            e.printStackTrace();
        }
    }

    /**
     * Get thread to assign task.
     * @return free thread.
     */
    public static synchronized SingleRequest getFreeRequest(){
        return readyPool.pollFirst();
    }

    public static Thread getMainThread(){
        return mainThread;
    }

    /**
     * Wake main thread.
     */
    public static void notifyMonitor(){
        synchronized (monitor){
            monitor.notifyAll();
        }
    }
}
