import java.io.ObjectInputStream;
import java.util.Queue;
import java.io.IOException;
/**
 * Listen for a specific client.
 *
 */
public class ListenFor implements Runnable{
    /** the Object Input Stream. */
    private ObjectInputStream ois;
    private Queue<TimeStampedMessage> listenQueue;
    private Queue<TimeStampedMessage> listenDelayQueue;
    private Configuration myConfig;
    private String senderName;
    public ListenFor(ObjectInputStream oistream, Queue<TimeStampedMessage> listenQ, Queue<TimeStampedMessage> listendqueue, Configuration c) {
        this.ois = oistream;
        this.listenQueue = listenQ;
        this.listenDelayQueue = listendqueue;
        this.myConfig = c;
    }
    @Override
    public synchronized void run() {
        while(true) {
            try {
                TimeStampedMessage newMes = (TimeStampedMessage)ois.readObject();
                senderName = newMes.get_source();
                //System.out.println("new message listen BUT NOT receive:");
                //System.out.println(newMes.toString());   
           
                String checkResult = checkReceiveRule(newMes,myConfig);
                if (checkResult != null) {
                    if (checkResult.equals("drop")) {
                        continue;
                    } else if (checkResult.equals("delay")) {
                        listenDelayQueue.offer(newMes);   
                    } else if (checkResult.equals("duplicate")) {
                        listenQueue.offer(newMes);
                        listenQueue.offer((TimeStampedMessage)newMes.clone());
	                    while (!listenDelayQueue.isEmpty()){
	                        TimeStampedMessage msg = listenDelayQueue.poll();
	                        listenQueue.offer(msg);
	                    }
                        
                    } 
                    else {
                        System.out.println("[ATTENTION] abnormal checkResult" + checkResult); 
                    }
                }
                else {
                    listenQueue.offer(newMes);
                    while (!listenDelayQueue.isEmpty()){
                        TimeStampedMessage msg = listenDelayQueue.poll();
                        listenQueue.offer(msg);
                    }
               
                }
            } catch (IOException | ClassNotFoundException e) {
                if (ois != null) {
                    try {
                        System.out.println("close the object input stream and the socket");
                        ois.close(); 
                        myConfig.OSMap.remove(senderName);
                        return;
                    } catch (Exception nestedE) {
                        nestedE.printStackTrace();   
                    }
                } else {
                    e.printStackTrace();
                } 
            } 
        }
    }
    public String checkReceiveRule(TimeStampedMessage newMes ,Configuration myConfig){
        for (Rule r : myConfig.receiveRules) {
            int result = r.match(newMes);
            if (result == 1) {
                if (r.get_action().equals("dropAfter")){
                    return null;
                }
                return r.get_action();
            }
            if (result == 2){
                return "drop";
            }
        }
        return null;
    }

}
