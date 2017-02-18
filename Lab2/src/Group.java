import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Queue;
public class Group {
    private String gname;
    private String myname;
    private int myid;
    private ArrayList<Node> members;
    public Queue<TimeStampedMessage> holdbackQ = new LinkedBlockingQueue<>();
    public ClockService groupClock;
    public Group(String n) {
        this.gname = n;
        this.members = new ArrayList<Node>();
    }
    public void addMember(Node a) {
        if (a != null) {
            this.members.add(a);
        }
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
    	if (members.contains(n)){
    		return true;
    	}
    	else {return false;}
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
    public TimeStampedMessage pollFromHOldBackQ(){
    	int size = holdbackQ.size();
    	for (int i = 0; i<size;i++){
    		TimeStampedMessage msg = holdbackQ.poll();
    		int[] msg_time = msg.getTimeStamps();
    		if (msg.getGroupMessageOrigin().equals(myname)){
    			if (groupClock.getTimeStamp(myid))
    		}
    		
    		if ()
    	}
    	return null;
    }

}
