import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * MessagePasser who in charge of sending and receiving message.
 * @author Team 3
 */
public class MessagePasser {
    private Configuration myConfig;
    private String myName;
    private Queue<TimeStampedMessage> sendDelayQueue;
    private Queue<TimeStampedMessage> receiveQueue;
    private Queue<TimeStampedMessage> receiveDelayQueue;
    /*Clock type for this process can be logical or vector*/
    private String myClock;
    /*Clock service for this process*/
    private ClockService clockservice;
    /*number of nodes in the system*/
    private int size;
    /* my id (define the position in vector clock */
    private int id;
    private Set<TimeStampedMessage> receivedSet;
    /**
     * MessagePasser constructor.
     * initialize local name, clock name
     * send queue, receive queue， receive delay queue and configuration file.
     * start listening on a new thread.
     * start receiving on a new thread.
     */
    public MessagePasser(String configuration_filename, String local_name, String clock_name) {
        this.myName = local_name;
        this.myClock = clock_name;
        this.sendDelayQueue = new ArrayDeque<TimeStampedMessage>(10);
        this.receiveQueue = new LinkedList<TimeStampedMessage>();
        this.receiveDelayQueue = new ArrayDeque<TimeStampedMessage>(10);
        this.receivedSet = new HashSet<TimeStampedMessage>();
        this.myConfig = new Configuration(configuration_filename);
        this.size = myConfig.get_NodeMap().keySet().size();
        this.id = myConfig.get_NodeMap().get(this.myName).get_nodeID();
        System.out.println("I am " + this.myName + ", my ID is: " + this.id);
        
        /* Use the clock factory to generate clock service. */
        ClockFactory factory = new ClockFactory(this);
        this.clockservice = factory.getClockService();
        ArrayList<Group> groups = myConfig.getGroups(myName);
        for (Group group : groups){
        	group.setClock(this.clockservice);
        	group.setMyname(this.myName);
        	group.setMyID(this.id);
        }
        Thread listen = new Thread(new Listener(myConfig, myName, receiveQueue, receiveDelayQueue));
        listen.start(); 
//        Thread receive = new Thread(new Receive(receiveQueue, clockservice, this.myConfig));
//        receive.start(); 
    }
    
    
    
    public void runNow(){
        while (true) {           
            TimeStampedMessage newMes = this.enterParameter(myName);
           
            if (newMes == null) {
                continue;
            }
            if (newMes.get_dest().equals("Q")&&newMes.get_kind().equals("d")){
            	co_deliver();
            	continue;
            }
            /* increment my clock */
            clockservice.increment();
            if (this.myClock.equals("vector")){
                newMes.setVectorMes(clockservice, this.size, this.id, myClock);
            } else if (this.myClock.equals("logical")) {
                newMes.setLogicalMes(clockservice.getTimeStamp(), myClock);
            }
            System.out.println("check clockservice in send" + clockservice);
            
            /* send to log function */
            if (newMes.get_log()){
                sendToLog(newMes);
            }
 
            if (newMes.get_mult()) {
                /* multicast the message */
                co_multicast(newMes);
            } else {
                normal_send(newMes);        
            } 
        }
    }
    
    /**
     * Send message to a particular destination.
     * @param dest destination
     * @param kind message kind
     * @param data the data in message
     */
    private void send(TimeStampedMessage newMes) {
       // System.out.println("[MessagePasser class: send function]");
        if (newMes == null) {
            System.out.println("Message is empty, can't send it");
            return;
        }
        ObjectOutputStream os = null;
        os = myConfig.get_OSMap(newMes.get_dest());
        if (os != null) {
            try {
                //System.out.println("[MessagePasser class: send function: using exsiting output stream.]");
                System.out.println("[send]message to be send is:" + newMes);
                os.writeObject(newMes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //System.out.println("[MessagePasser class: send function: create new output stream...]");
            Node me = myConfig.getNode(myName);
            Node he = myConfig.getNode(newMes.get_dest());
            Socket sck = null;
            try {
                sck = new Socket(he.get_ip(), he.get_port());
//                sck = new Socket("localhost", he.get_port());
//                System.out.println("succeed");
                os = new ObjectOutputStream(sck.getOutputStream());
                myConfig.add_OSMap(newMes.get_dest(), os);
                System.out.println("[send]message to be send is:" + newMes);
                os.writeObject(newMes);
            } catch (IOException e) {
                if (sck != null) {
                    try {
                        sck.close();
                    } catch (Exception nestedE) {
                        nestedE.printStackTrace();   
                    }
                } else {
                    e.printStackTrace();
                }
                
            }   
        }   
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
                System.out.println("[sendToLog]message to be send is:" + newMes);
                os.writeObject(newMes);
            } catch (IOException e) {
                myConfig.set_LoggerOS(null);
                os = null;
            }
        } 
        if (os == null) {
            Socket sck = null;
            try {
                String log_IP = myConfig.getLogger().get_ip();
                int log_port = myConfig.getLogger().get_port();
                sck = new Socket(log_IP,log_port);
                os = new ObjectOutputStream(sck.getOutputStream());
                myConfig.set_LoggerOS(os);
                System.out.println("[sendToLog]message to be send is:" + newMes);
                os.writeObject(newMes);
            } catch (IOException e) {
                if (sck != null) {
                    try {               
                        System.out.println("set the log os to null");
                        os.close();
                        myConfig.set_LoggerOS(null);
                    } catch (Exception nestedE) {
                        nestedE.printStackTrace();   
                    }
                } else {
                    e.printStackTrace();
                }
                
            }   
        }   
    }
    public void co_multicast(TimeStampedMessage msg){
    	Group group = myConfig.get_groupMap().get((msg.getGroupName()));
    	ClockService groupClock = group.getClock();
    	for (int i = 0; i<size;i++){
    		int time = groupClock.getTimeStamp(i);
    		if (i == id){
    			time++;
    			groupClock.increment(i);
    		}
    		msg.setVectorTimeStamp(i,time);
    	}
    	group.setClock(groupClock);
    	myConfig.get_groupMap().put(msg.getGroupName(),group);
    	b_multicast(msg);
    }
    /**
     * basic multicast
     * @param nm
     */
    public void b_multicast(TimeStampedMessage nm) {
        
        Group sendGroup = myConfig.groupMap.get(nm.getGroupName());
        for (Node a: sendGroup.getMembers()) {
            /* clone message and set correct destination */
            TimeStampedMessage multicastMes = nm.cloneMultiCast();
            multicastMes.set_dest(a.get_name());
            /* deal with node's sequence number */
            multicastMes.set_seqNum(myConfig.getNode(multicastMes.get_dest()).get_seqN());
            myConfig.getNode(multicastMes.get_dest()).incre_seqN();

            String checkResult = check(multicastMes); 
            if (checkResult != null) {
                if (checkResult.equals("drop")) {
                    continue;
                } else if (checkResult.equals("duplicate")) {
                    TimeStampedMessage clone = multicastMes.clone();
                    send(multicastMes);
                    send(clone);
                    while (!sendDelayQueue.isEmpty()){
                        TimeStampedMessage msg = sendDelayQueue.poll();
                        send(msg);
                    }
                } else if (checkResult.equals("delay")){
                    sendDelayQueue.offer(multicastMes);   
                } else {
                    System.out.println("[ATTENTION]abnormal checkResult" + checkResult); 
                }
            }
            else {
                //send directly
                send(multicastMes);
                while (!sendDelayQueue.isEmpty()){
                    TimeStampedMessage msg = sendDelayQueue.poll();
                    send(msg);
                }
            }   
        }
    }
    /**
     * normal send.
     * not multicast
     * @param newMes
     */
    public void normal_send(TimeStampedMessage newMes) {
        newMes.set_seqNum(myConfig.getNode(newMes.get_dest()).get_seqN());
        myConfig.getNode(newMes.get_dest()).incre_seqN();
        String checkResult = check(newMes); 
        if (checkResult != null) {
            if (checkResult.equals("drop")) {
                return;
            } else if (checkResult.equals("duplicate")) {
                TimeStampedMessage clone = newMes.clone();
                send(newMes);
                send(clone);
                while (!sendDelayQueue.isEmpty()){
                    TimeStampedMessage msg = sendDelayQueue.poll();
                    send(msg);
                }
            } else if (checkResult.equals("delay")){
                sendDelayQueue.offer(newMes);   
            } else {
                System.out.println("[ATTENTION]abnormal checkResult" + checkResult); 
            }
        }
        else {
            //send directly
            send(newMes);
            while (!sendDelayQueue.isEmpty()){
                TimeStampedMessage msg = sendDelayQueue.poll();
                send(msg);
            }
        } 
    }
    //b_multicast end
    /**
     * Receive
     * @return
     */
    public synchronized TimeStampedMessage receive(){
        System.out.println("[1st layer]receive()");
        TimeStampedMessage msg = null;
        if (!receiveQueue.isEmpty()){
            msg = receiveQueue.poll();
            this.clockservice.Synchronize(msg);
            System.out.println("[1st layer]receive()" + " clock service" + this.clockservice);
            if (msg.get_log()){
                TimeStampedMessage toLogMessage =  new TimeStampedMessage(msg.get_source(),msg.get_dest(),
                        "[RECORD TST]","[RECORD TST]", true, msg.get_mult());//just to see my time stamp
                toLogMessage.setVectorMes(clockservice, clockservice.get_size(), clockservice.get_id(), clockservice.get_type());
                sendToLog(msg);
            }
        }
        return msg;
    }
    /**
     * Call receive().
     * @return
     */
    public TimeStampedMessage b_deliver(){
        System.out.println("[2st layer]b_deliver()");
        return this.receive();
    }
    /**
     * Call b_deliver().
     * @return
     */
    public TimeStampedMessage r_deliver() {
        System.out.println("[3st layer]r_deliver()");
        TimeStampedMessage msg;
        while ((msg = b_deliver()) != null) {
            if (!receivedSet.contains(msg)) {// this message is received for the first time
                receivedSet.add(msg);
                if (!(msg.get_source().equals(this.myName))) {
                    b_multicast(msg);
                } else {
                    System.out.println("[3st layer]r_deliver() sender receive message from sender");
                    return msg;
                }
    //            myConfig.get_groupMap().get(msg.getGroupName()).addToHoldBackQ(msg);// store the message to the group holdback queue. 
            } else if (msg.get_source().equals(this.myName)){
                System.out.println("[3st layer]r_deliver() receive message from myself");
                return msg;
            }
        }
        return null;
    }
    
    
    /**
     * check input message against rules in rule list.
     * @return actions should be taken.
     */
    private String check(TimeStampedMessage newMes) {
        System.out.println("[check send message]");
        for (Rule r : myConfig.sendRules) {
            int result = r.match(newMes);
            if (result == 1) {
                if (r.get_action().equals("dropAfter")){
                    return null;
                }
                return r.get_action();
            }
            if (result == 2){
                //due to drop after, the after message will be dropped.
                return "drop";
            }
        }
        return null;
    }
    public String getClock(){
        return this.myClock;
    }
    public int getSize(){
        return this.size;
    }
    public int getId(){
        return this.id;
    }
    /**
     * Construct the message from input parameters.
     * @return the message constructed from input parameters.
     */
    private TimeStampedMessage enterParameter(String localName) {
        System.out.println("destination/kind/content/iflog/ifmulticast");
        InputStreamReader isrd = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isrd);
        String[] inputParam = null;
        try {
            String temp = br.readLine();
            inputParam = temp.split("/");
            if (inputParam.length < 5) {
                System.out.println("illegal input");
                return null;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }   
        try {
            TimeStampedMessage newM = new TimeStampedMessage(localName, inputParam[0],
                    inputParam[1], inputParam[2],
                    inputParam[3].equals("T")? true:false,
                    inputParam[4].equals("T")? true:false);
            if (inputParam[4].equals("T")) {
                newM.setGroupNameAndGroupMessageOrigin(inputParam[0], localName);
            }
            return newM;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}