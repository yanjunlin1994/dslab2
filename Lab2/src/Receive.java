import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;
/**
 * Receive class to receive message from receive queue.
 * @author Team 3
 *
 */
public class Receive implements Runnable{
    private Queue<TimeStampedMessage> receiveQueue;
    private ClockService clockservice;
    private Configuration myConfig;
    
    public Receive(Queue<TimeStampedMessage> receiveQ, ClockService cs, Configuration conf) {
        this.receiveQueue = receiveQ;
        this.clockservice = cs;
        this.myConfig = conf;
    }
    @SuppressWarnings("resource")
    @Override
    public void run(){
        try {
            while (true) {
                try {
                    TimeStampedMessage receMes = receive();
                    if (receMes != null) {
                        //System.out.println("[Receive] receive from queue: ");
                        //System.out.println(receMes.toString());
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
            if (msg.get_log()){
            	TimeStampedMessage toLogMessage =  new TimeStampedMessage(msg.get_source(),msg.get_dest(),
            			"received msg","received data", true);
//            	toLogMessage.set_log(true);
            	toLogMessage.setVectorMes(clockservice, clockservice.get_size(), clockservice.get_id(), clockservice.get_type());
            	sendToLog(msg);
            }
            System.out.println("check clockservice in receive" + "("+ clockservice +")");
        }
        return msg;
    }
	private void sendToLog(TimeStampedMessage newMes) {	    
	    if (newMes == null) {
	        System.out.println("Message is empty, can't send it");
	        return;
	    }
        ObjectOutputStream os = null;
        os = myConfig.get_LoggerOS();
        if (os != null) {
            try {
                os.writeObject(newMes);
            } catch (IOException e) {
                myConfig.set_LoggerOS(null);
                os = null;
            }
        } 
        if (os == null) {
            Socket sck = null;
            try {
                //TODO:load the log information in configuration
            	String log_IP = myConfig.getLogger().get_ip();
            	int log_port = myConfig.getLogger().get_port();
                sck = new Socket(log_IP,log_port);
                os = new ObjectOutputStream(sck.getOutputStream());
                myConfig.set_LoggerOS(os);
                os.writeObject(newMes);
            } catch (IOException e) {
                if (sck != null) {
                    try {
                    	
                        System.out.println("set the log os to null");
                        os.close();
                        myConfig.set_LoggerOS(null);
                        
//                        sck.close();
                    } catch (Exception nestedE) {
                        nestedE.printStackTrace();   
                    }
                } else {
                    e.printStackTrace();
                }
                
            }   
        }   
    }
}
