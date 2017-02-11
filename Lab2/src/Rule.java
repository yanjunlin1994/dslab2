/**
 * Rule class
 * contains send and receive rules.
 * @author Team 3
 */
public class Rule {
	private String action;
	private String src;
	private String dst;
	private String kind;
	private int seqNum;
	private boolean duplicate;
	/**
	 * Rule constructor without parameter.
	 */
	public Rule() {
	    //default settings for a rule.
	    this.seqNum = -1;
	    this.duplicate = false;
	}
	/**
     * Rule constructor with all parameters.
     */
	public Rule(String a, String s, String d, String k, int sN, boolean dup) {
	    this.action = a;
	    this.src = s;
	    this.dst = d;
	    this.kind = k;
	    this.seqNum = sN;
	    this.duplicate = dup;
	}
	public String get_action(){
        return this.action;
    }
	public void set_action(String a){
		this.action = a;
	}
	public String get_src(){
		return this.src;
	}
	public void set_src(String s){
		this.src = s;
	}
	public String get_dst(){
		return this.dst;
	}
	public void set_dst(String d){
		this.dst = d;
	}
	public String get_kind(){
		return this.kind;
	}
	public void set_kind(String k){
		this.kind = k;
	}
	public int get_seqNum(){
		return this.seqNum;
	}
	public void set_seqNum(int se){
		this.seqNum = se;
	}
	public boolean get_duplicate(){
		return this.duplicate;
	}
	public void set_duplicate(boolean du){
		this.duplicate = du;
	}
	/**
	 * Match function to be whether the rules should be applied.
	 * @param msg message to be matched.
	 * @return 0 no match found
	 *         1 match found
	 *         2 should be dropped because of "dropafter"
	 */
	public int match(Message msg){
		if (this.dst!= null && !msg.get_dest().equals(this.dst)){
			return 0;
		}
		if(this.src != null && !msg.get_source().equals(this.src)){
			return 0;
		}
		if (this.kind != null && !msg.get_kind().equals(this.kind)){
			return 0;
		}
		if (this.duplicate != msg.get_duplicate()){
			return 0;
		}		
		if ((this.seqNum != -1) && (msg.get_seqNum() != (this.seqNum))) {
			if ((msg.get_seqNum() > this.seqNum) && (this.action.equals("dropAfter"))){
			    System.out.println("message sequence number larger that 'dropafter' sequence number");
				return 2;
			}
			return 0;
		}
		return 1;
	}
}
