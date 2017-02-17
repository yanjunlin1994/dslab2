import java.util.Arrays;
import java.io.Serializable;
import java.util.*;

/**
 * 
 * @author Team 3
 *
 */
public class GroupMessage extends TimeStampedMessage implements Serializable{
    
    public GroupMessage (String s,String d,String k,Object data, boolean isl, ) {
        super(s, d, k, data);
        TimeStampedMessage(String s,String d,String k,Object data, boolean isl) 
        this.ifLog = false;
    }
    
}