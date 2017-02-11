

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;

public class LogListener implements Runnable {
    /** log's name. */
    private String logName;
    private Queue<TimeStampedMessage> listenQueue;
    public LogListener(String Name, Queue<TimeStampedMessage> Q) {
        this.logName = Name;
        this.listenQueue = Q;
    }
    
    @SuppressWarnings("resource")
    @Override
    public void run() {
        try {
            System.out.println("[" + this.logName + " is listening...]");
            ServerSocket listener = new ServerSocket(18678);
            while (true) {
                Socket socket = null;
                try {
                    socket = listener.accept();
                    System.out.println("[log accepts connection from" + 
                            socket.getRemoteSocketAddress().toString() + " " + socket.getPort() + "]");
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    Thread listenFor = new Thread(new LogListenFor(ois, this.listenQueue));
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
