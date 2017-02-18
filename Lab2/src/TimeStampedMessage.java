import java.util.Arrays;
import java.io.Serializable;
import java.util.*;

/**
 * 
 * @author Team 3
 *
 */
public class TimeStampedMessage extends Message implements Serializable{
/* list of the variables in TimeTampedMessage class
 *    private String source;
      private String dest;
      private String kind;
      private Object payload;  
      private int seqNum;
      private boolean duplicate;
      private int timeStamp;
      private int[] timeStamps;
      private String clock_type;
      private int size;
      private int id;
      private boolean ifLog;
      private boolean ifmulc;
      private String groupName;
      private String groupMessageOrigin;
    */
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
	private String groupMessageOrigin;
	
	/**
	 * Constructor using Message class's contructor.
	 */
	public TimeStampedMessage(String s,String d,String k,Object data, boolean isl, boolean ifm) {
		super(s, d, k, data);
		this.ifLog = isl;
		this.ifmulc = ifm;
	}
	public TimeStampedMessage(String s,String d,String k,Object data, boolean dup, int sn, boolean isl, boolean ifm) {
        super(s, d, k, data, dup, sn);
        this.ifLog = isl;
        this.ifmulc = ifm;
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
	//-----------wrapper getter
	public int getMyTimeStamp() {
	    if(clock_type.equals("logical")) {
	        return this.getLogicalTimeStamp();
	    } else if (clock_type.equals("vector")) {
	        return this.getMyVectorTimeStamp();
	    } else {
	        throw new RuntimeException("error in TimeStampedMessage's getTimeStamp() class");
	    }
	}
	//------------logical clock------------
	/**
	 * logical timestamp setter.
	 */
	public void setLogicalTimeStamp(int t){
	    if(clock_type.equals("logical")){
	        this.timeStamp = t;
        } else {
            throw new RuntimeException("error in TimeStampedMessage's setLogicalTimeStamp class");
        }
	}
	/**
     * logical timestamp getter.
     */
	public int getLogicalTimeStamp(){
	    if(clock_type.equals("logical")){
	        return this.timeStamp;
        } else {
            throw new RuntimeException("error in TimeStampedMessage's getLogicalTimeStamp() class");
        }
	}
	//------------vector clock------------
	/**
	 * get the whole array of time stamp.
	 * @return
	 */
	public int[] getVectorTimeStamps(){
	    if(clock_type.equals("vector")){
	        return this.timeStamps;
	    } else {
	        throw new RuntimeException("error in TimeStampedMessage's getVectorTimeStamps() class");
	    }	
	}
	/**
     * vector timestamp getter by specific index.
     */
    public int getMyVectorTimeStamp(){
        if(clock_type.equals("vector")){
            return this.timeStamps[this.id];
        } else {
            throw new RuntimeException("error in TimeStampedMessage's getMyVectorTimeStamp()");
        } 
    }
	/**
     * vector timestamp getter by specific index.
     */
    public int getVectorTimeStamp(int i){
        if(clock_type.equals("vector")){
            return this.timeStamps[i];
        } else {
            throw new RuntimeException("error in TimeStampedMessage's getVectorTimeStamp");
        } 
    }
    /**
     * vector timestamp setter.
     * Sets the time t in corresponding position i.
     * @param t timestamp
     * @param i index
     */
    public void setVectorTimeStamp(int i, int t){
        if(clock_type.equals("vector")){
            this.timeStamps[i] = t;
        } else {
            throw new RuntimeException("error in TimeStampedMessage's setVectorTimeStamp");
        }
    }
	//--------------log flag--------------
	public void set_log(boolean ifl){
        this.ifLog = ifl;
    }
    public boolean get_log(){
        return this.ifLog;
    }
    //---------------group-----------------
    public void setGroupNameAndGroupMessageOrigin(String gn, String gmo) {
        setGroupName(gn);
        setGroupMessageOrigin(gmo);
    }
    public void setGroupName(String gn){
    	this.groupName = gn;
    }
    public String getGroupName(){
    	return this.groupName;
    }
    public void setGroupMessageOrigin(String gmo){
        this.groupMessageOrigin = gmo;
    }
    public String getGroupMessageOrigin(){
        return this.groupMessageOrigin;
    }
    public void set_mult(boolean m){
        this.ifmulc = m;
    }
    public boolean get_mult(){
        return this.ifmulc;
    }
	/**
	 * compare method.
	 */
	public int compare(TimeStampedMessage msg1, TimeStampedMessage msg2){
		if (!(msg1.getType().equals(msg2.getType()))) {
		    throw new RuntimeException("two message type different");
		}
		if (msg1.getType().equals("vector")) {
			int[] m1 = msg1.getVectorTimeStamps();
			int[] m2 = msg2.getVectorTimeStamps();
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
			int m1 = msg1.getLogicalTimeStamp();
			int m2 = msg2.getLogicalTimeStamp();
			return m1 - m2;
		}
	}
	public String toString() { 
        return  "[clock type:" +  this.clock_type + "]" 
                + "[ifLog:" +  this.ifLog + "]" 
                + "[ifmultc:" +  this.ifmulc + "]" 
                + "[Groupname:" +  this.groupName + "]"  
                + "[Group message origin:" +  this.groupMessageOrigin + "]" 
                + "[time stamp:"
                + (this.clock_type.equals("logical") ? this.timeStamp : Arrays.toString(timeStamps))
                + "]"
                + "[NO." + this.get_seqNum() + "]" + "[source]"+ this.get_source() + " [dest]"+ this.get_dest() 
                + " [kind]"+ this.get_kind() + " [content]" + this.get_payload();
    }
	
	public TimeStampedMessage clone(){
	    TimeStampedMessage cl = new TimeStampedMessage(this.get_source(),this.get_dest(), 
	            this.get_kind(), this.get_payload(), true, this.get_seqNum(), this.ifLog,this.ifmulc);
	    if (this.getGroupName() != null) {
	        cl.setGroupName(this.groupName);
	    }
        if (this.clock_type.equals("logical")) {
            cl.setLogicalMes(this.timeStamp, this.clock_type);
        } else if (this.clock_type.equals("vector")) {
            cl.setVectorMesCopy(this.timeStamps, this.size, this.id, this.clock_type);
        }
        return cl;
    }
	public TimeStampedMessage cloneMultiCast(){
		
	    TimeStampedMessage cl = new TimeStampedMessage(this.get_source(),this.get_dest(), 
	            this.get_kind(), this.get_payload(), this.get_duplicate(), this.get_seqNum(), this.ifLog,this.ifmulc);
	    cl.setGroupName(this.groupName);
        if (this.clock_type.equals("logical")) {
            cl.setLogicalMes(this.timeStamp, this.clock_type);
        } else if (this.clock_type.equals("vector")) {
            cl.setVectorMesCopy(this.timeStamps, this.size, this.id, this.clock_type);
        }
        return cl;
    }
	public int hashCode() {
        return this.getMyTimeStamp() + this.groupMessageOrigin.hashCode();
    }

	public boolean equals(Object o) {
	    System.out.println("[enter TSM:equals]");
	    TimeStampedMessage t = (TimeStampedMessage)o;
		if (!(this.get_dest().equals(t.get_dest()))){
		    throw new RuntimeException("destination wrong");
		}
		if (!(this.get_kind().equals(t.get_kind()))){
			return false;
		}
		if (!(this.get_payload().equals(t.get_payload()))){
			return false;
		}
		if (!(this.clock_type.equals(t.getType()))){
			return false;
		}
		if (this.clock_type.equals("logical")) {
		    if (this.timeStamp != t.getLogicalTimeStamp()) {
		        return false;
		    }
		} else if (this.clock_type.equals("vector")) {
		    if (!(Arrays.equals(this.getVectorTimeStamps(), t.getVectorTimeStamps()))) {
		        return false;
		    }
		} else {
		    throw new RuntimeException("error type");    
		}
		if (this.ifmulc != t.get_mult()){
			return false;
		}
		if (!(this.groupName.equals(t.getGroupName()))) {
		    return false;
		}
		if (!(this.groupMessageOrigin.equals(t.getGroupMessageOrigin()))) {
            return false;
        }
		return true;
	}

}
