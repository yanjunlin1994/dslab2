import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Queue;
public class Group {
    private String gname;
    private String myname;
    private ArrayList<Node> members;
    public Queue<TimeStampedMessage> holdbackQ = new LinkedBlockingQueue<>();
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
    public void changeName(String ne) {
        this.gname = ne;
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

}
