/**
 * Node class.
 * A node contains its name, port, ip address and the sequence number.
 *
 */
public class Node {
	private String name;
	private int port;
	private String ip;
	private int seqN;
	public Node() {
        this.seqN = 0;
    }
	/**
	 * Node constructor without sequence number.
	 * constructs using name, port and ip address
	 * and the default seqN is 0;
	 */
	public Node(String n, int p, String i) {
	    this.name = n;
	    this.port = p;
	    this.ip = i;
	    this.seqN = 0;
	}
	public String get_name(){
		return this.name;
	}
	public void set_name(String n){
		this.name = n;
		return;
	}
	public int get_port(){
		return this.port;
	}
	public void set_port(Integer p){
		this.port = p;
		return;
	}
	public String get_ip(){
		return this.ip;
	}
	public void set_ip(String i){
		this.ip = i;
		return;
	}
	public void set_seqN(int num){
        this.seqN = num;
        return;
    }
	/**
	 * increment the sequence number when accessed.
	 */
	public void incre_seqN(){
        this.seqN =  this.seqN + 1;
        return;
    }
	public int get_seqN(){
        return this.seqN;
    }
	
}
