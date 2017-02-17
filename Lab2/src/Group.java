import java.util.ArrayList;

public class Group {
    private String gname;
    private int size;
    private ArrayList<Node> members;
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
    public void updateSize() {
        this.size = members.size();
    }
    public int getSize() {
        return this.size;
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

}
