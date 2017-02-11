import java.util.Queue;
/**
 * Receive class to receive message from receive queue.
 * @author Team 3
 *
 */
public class Receive implements Runnable{
    private Queue<TimeStampedMessage> receiveQueue;
    private ClockService clockservice;
    
    public Receive(Queue<TimeStampedMessage> receiveQ, ClockService cs) {
        this.receiveQueue = receiveQ;
        this.clockservice = cs;
    }
    @SuppressWarnings("resource")
    @Override
    public void run(){
        try {
            while (true) {
                try {
                    TimeStampedMessage receMes = receive();
                    if (receMes != null) {
                        System.out.println("[Receive] receive from queue: ");
                        System.out.println(receMes.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } 
            }
        } catch(Exception e) {
            e.printStackTrace();
        } 
    }  
    public synchronized TimeStampedMessage receive(){
        TimeStampedMessage msg = null;
        if (!receiveQueue.isEmpty()){
            msg = receiveQueue.poll();
            this.clockservice.Synchronize(msg);
            System.out.println("check clockservice in receive" + "("+ clockservice +")");
        }
        return msg;
    }
}
