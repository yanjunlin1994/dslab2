import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Queue;
/**
 * Listen for all possible clients.
 *
 */
public class Listener implements Runnable{
    /** configuration file. */
    private Configuration myConfig;
    /** listener's name. */
    private String localName;
    private Queue<TimeStampedMessage> listenQueue;
    private Queue<TimeStampedMessage> listenDelayQueue;
    public Listener(Configuration config, String Name, Queue<TimeStampedMessage> receiveQueue, Queue<TimeStampedMessage> receiveDelayQueue) {
        this.myConfig = config;
        this.localName = Name;
        this.listenQueue = receiveQueue;
        this.listenDelayQueue = receiveDelayQueue;
    }
    
    @SuppressWarnings("resource")
    @Override
    public void run(){
        try {
            System.out.println("[listening...]");
            ServerSocket listener = new ServerSocket((myConfig.getNode(localName).get_port()));
            while (true) {
                Socket socket = null;
                try {
                    socket = listener.accept();
                    System.out.println("[accept connection from" + 
                            socket.getRemoteSocketAddress().toString() + " " + socket.getPort() + "]");
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    Thread listenFor = new Thread(new ListenFor(ois, listenQueue,listenDelayQueue,myConfig));
                    listenFor.start();
                } catch (IOException e) {
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (Exception nestedE) {
                            nestedE.printStackTrace();   
                        }
                    } else {
                        e.printStackTrace();
                    }
                    
                } 
            }
        } catch(IOException e) {
            e.printStackTrace();
        } 
    }
}
