import java.util.Arrays;
import java.io.Serializable;
import java.util.*;

/**
 * 
 * @author Team 3
 *
 */
public class TimeStampedMessage extends Message implements Serializable{
    /* logical timeStamp. */
	private int timeStamp;
	
	/* vector timeStamp. */
	private int[] timeStamps;
	/* the clock's type is either vector or logical.*/
	private String clock_type;
	/* size of vector array.*/
	private int size;
	/* my index in array */
	private int id;
	private boolean ifLog;
	private boolean ifmulc;
	private String groupName;
	
	/**
	 * Constructor using Message class's contructor.
	 */
	public TimeStampedMessage(String s,String d,String k,Object data, boolean isl, boolean ifm) {
		super(s, d, k, data);
		this.ifLog = isl;
		this.ifmulc = ifm;
	}
	public TimeStampedMessage(String s,String d,String k,Object data, boolean dup, int sn, boolean isl) {
        super(s, d, k, data, dup, sn);
        this.ifLog = isl;
    }
	public void setLogicalMes(int st, String ct) {
	    this.timeStamp = st;
	    this.clock_type = ct;
	}
	public void setVectorMes(ClockService csv, int sz, int ID, String ct) {
	    this.size = sz;
	    this.id = ID;
	    this.clock_type = ct;
	    this.timeStamps = new int[sz];
	    if (csv == null) {
	        for (int i = 0; i < this.size; i++) {
	            this.timeStamps[i] = 0;
	        }
	    } else {
	        for (int i = 0; i < this.size; i++) {
	            this.timeStamps[i] = csv.getTimeStamp(i);
	        }
	    }    
    }
	public void setVectorMesCopy(int[] times, int sz, int ID, String ct) {
        this.size = sz;
        this.id = ID;
        this.clock_type = ct;
        this.timeStamps = times.clone();
    }
	/**
	 * set the type either vector or logical.
	 * @param clock the clock type
	 */
	public void setType(String clock){
	    if ((clock.equals("vector")) || (clock.equals("logical"))) {
	        this.clock_type = clock;
	    } else {
	        throw new RuntimeException("error in TimeStampedMessage's setType");
	    }
	}
	public String getType(){
	    if (this.clock_type == null) {
	        throw new RuntimeException("TimeStampedMessage's type was not set");
	    }
		return this.clock_type;
	}
	public void setSize(int s){
		this.size = s;
		
		return;
	}
	public int getSize(){
		return this.size;
	}
	public void setId(int i){
		this.id = i;
		return;
	}
	public int getId(){
		return this.id;
	}
	/**
	 * vector timestamp setter.
	 * Sets the time t in corresponding position i.
	 * @param t timestamp
	 * @param i index
	 */
	public void setTimeStamp(int t, int i){
		if(clock_type.equals("vector")){
			this.timeStamps[i] = t;
		} else {
		    throw new RuntimeException("error in TimeStampedMessage's setTimeStamp");
		}
	}
	/**
     * vector timestamp getter by specific index.
     */
	public int getTimeStamp(int i){
	    if(clock_type.equals("vector")){
	        return timeStamps[i];
        } else {
            throw new RuntimeException("error in TimeStampedMessage's getTimeStamp");
        } 
    }
	/**
	 * logical timestamp setter.
	 */
	public void setTimeStamp(int t){
	    if(clock_type.equals("logical")){
	        this.timeStamp = t;
        } else {
            throw new RuntimeException("error in TimeStampedMessage's setTimeStamp class");
        }
	}
	/**
     * logical timestamp getter.
     */
	public int getTimeStamp(){
	    if(clock_type.equals("logical")){
	        return this.timeStamp;
        } else {
            throw new RuntimeException("error in TimeStampedMessage's setTimeStamp class");
        }
	}
	/**
	 * get the whole array of time stamp.
	 * @return
	 */
	public int[] getTimeStamps(){
		return timeStamps;
	}
	public void set_log(boolean ifl){
        this.ifLog = ifl;
    }
    public boolean get_log(){
        return ifLog;
    }
    public void set_mult(boolean m){
    	this.ifmulc = m;
    }
    public boolean get_mult(){
    	return this.ifmulc;
    }
    public void setGroupName(String gn){
    	this.groupName = gn;
    }
    public String getGroupName(){
    	this.groupName;
    }
	/**
	 * compare method.
	 */
	public int compare(TimeStampedMessage msg1, TimeStampedMessage msg2){
		if (!(msg1.getType().equals(msg2.getType()))) {
		    throw new RuntimeException("two message type different");
		}
		if (msg1.getType().equals("vector")) {
			int[] m1 = msg1.getTimeStamps();
			int[] m2 = msg2.getTimeStamps();
			int flag = 0;
			for (int i = 0; i < m1.length; i++){
				if(flag == 1 && m1[i] < m2[i]) {
				    return 0;
				}
				if(flag == -1 && m1[i] > m2[i]) {
				    return 0;
				}
				
				if(flag == 0 && m1[i] < m2[i]) {
					flag = -1;
				} else if (flag == 0 && m1[i] > m2[i]){
					flag = 1;
				}
			}
			return flag;
		}
		else {
			int m1 = msg1.getTimeStamp();
			int m2 = msg2.getTimeStamp();
			return m1 - m2;
		}
	}
	public String toString() { 
        return  "[clock type:" +  this.clock_type + "]" 
                + "[ifLog:" +  this.ifLog + "]" 
                + "[time stamp:"
                + (this.clock_type.equals("logical") ? this.timeStamp : Arrays.toString(timeStamps))
                + "]"
                + "[NO." + this.get_seqNum() + "]" + "[source]"+ this.get_source() + " [dest]"+ this.get_dest() 
                + " [kind]"+ this.get_kind() + " [content]" + this.get_payload();
    }
	
	public TimeStampedMessage clone(){
	    TimeStampedMessage cl = new TimeStampedMessage(this.get_source(),this.get_dest(), 
	            this.get_kind(), this.get_payload(), true, this.get_seqNum(), false);
	            //clone message will not be send to log
        if (this.clock_type.equals("logical")) {
            cl.setLogicalMes(this.timeStamp, this.clock_type);
        } else if (this.clock_type.equals("vector")) {
            cl.setVectorMesCopy(this.timeStamps, this.size, this.id, this.clock_type);
        }
        return cl;
    }
	public boolean equals(TimeStampedMessage t){
	/*    
		private int timeStamp;
		
		
		private int[] timeStamps;
		
		private String clock_type;
	
		private int size;
		
		private int id;
		private boolean ifLog;
		private boolean ifmulc;
	*/
		if (!this.get_source().equals(t.get_source())){
			return false;
		}
		if (!this.get_dest().equals(t.get_dest())){
			return false;
		}
		if (!this.get_kind().equals(t.get_kind())){
			return false;
		}
		if (!this.get_payload().equals(t.get_payload())){
			return false;
		}
		if (this.timeStamp != t.getTimeStamp()){
			return false;
		}
		if (!this.clock_type.equals(t.getType())){
			return false;
		}
		if (this.get_log()!=t.get_log()){
			return false;
		}
		if (this.get_seqNum()!= t.get_seqNum()){
			return false;
		}
		if (this.get_duplicate()!= t.get_duplicate()){
			return false;
		}
		if (this.ifmulc != t.get_mult()){
			return false;
		}
		if (this.size != t.getSize()){
			return false;
		}
		if (this.id != t.getId()){
			return false;
		}
		for (int i = 0; i< size;i++){
			
		}
	}

}
