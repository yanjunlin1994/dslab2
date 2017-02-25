import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Queue;
public class Group {
    private String gname;
    private String myname;
    private int myid;
    private int selfcount;
    private ArrayList<Node> members;
    public Queue<TimeStampedMessage> holdbackQ; 
    public ClockService groupClock;
    public Group(String n) {
        this.gname = n;
        this.members = new ArrayList<Node>();
        this.holdbackQ = new LinkedBlockingQueue<>();
        this.selfcount = 0;
    }
    public void addMember(Node a) { 
        if (a != null) {
            this.members.add(a);
        }
    }
    public void setmyNameIDClock(ClockService csclone, String mn, int ID) {
        this.groupClock = csclone;
        this.myname = mn;
        this.myid = ID;
    }
    /**
     * update the size of group.
     * according to the number of members in the group.
     */
    public int getSize() {
        return this.members.size();
    }
    public String getName() {
        return this.gname;
    }
    public void setMyname(String n){
    	this.myname = n;
    }
    public String getMyname(){
    	return this.myname;
    }
    public void setMyID(int i){
    	this.myid = i;
    }
    public int getMyID(){
    	return this.myid;
    }
    public void changeName(String ne) {
        this.gname = ne;
    }
    public boolean hasMember(String n){
        for (Node m: members) {
            if (m.get_name().equals(n)) {
                return true;
            }
        }
    	return false;
    }
    public void setClock(ClockService s){
    	this.groupClock = s;
    }
    public ClockService getClock(){
    	return this.groupClock;
    }
    public Node getMember(String mn) {
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).get_name().equals(mn)) {
                return members.get(i);
            }
        }
        return null;
    }
    public ArrayList<Node> getMembers() {
        return this.members;
    }
    public void addToHoldBackQ(TimeStampedMessage mes) {
        this.holdbackQ.offer(mes);
        
    }
    public TimeStampedMessage pollFromHoldBackQ(){
        System.out.println("[enter pollFromHoldBackQ]");
    	int size = holdbackQ.size();
//    	System.out.println("[enter pollFromHoldBackQ] size" + size);
    	for (int i = 0; i<size;i++){
    		TimeStampedMessage msg = holdbackQ.poll();

    		int[] msg_time = msg.getVectorTimeStamps();
    		if (msg.getGroupMessageOrigin().equals(myname)){
//    		    System.out.println("[enter pollFromHoldBackQ] myname " + myname+ " myid: "+myid);
//    		    System.out.println("[enter pollFromHoldBackQ] gc " + selfcount);
//    		    System.out.println("[enter pollFromHoldBackQ] msgc " + msg_time[myid]);
    			if ((this.selfcount+1) == msg_time[myid]){
    			    this.selfcount++;
    				return msg;
    			}
    		}
    		
    		else if (groupClock.getTimeStamp(msg.getId())+1==msg_time[msg.getId()]){
    			boolean valid = true;
    			for (Node member : this.getMembers()){
    				int member_id = member.get_nodeID();
    				if (member_id!=msg.getId() && msg_time[member_id]>groupClock.getTimeStamp(member_id)){
    				    //whether add back to hold back Q??
    					valid = false;
    					addToHoldBackQ(msg);
    					break;
    				}
    			}
    			if (valid){
    				groupClock.increment(msg.getId());
    				return msg;
    			}
    		}
    		else {
    			addToHoldBackQ(msg);
    		}
    	}
    	return null;
    }

}
