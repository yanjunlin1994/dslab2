import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
/**
 * Main test class.
 * initializes the MessagePasser and make it run.
 * @author Team 3
 *
 */
public class MainTest {
	public static void main(String args[]){
	    String configFileName = args[0];
	    String myName = args[1];
	    String myclock = args[2];
	    MessagePasser mp = new MessagePasser(configFileName, myName, myclock);
	    mp.runNow();
	}	
}
