/**
 * 
 * @author Team 3
 *
 */
public class LogicalClock implements ClockService {
	private int time;
	public LogicalClock() {
	    this.time = 1;
	}
	@Override
	public void increment() {
		this.time++;
	}

	@Override
	public int getTimeStamp() {
		return time;
	}

	@Override
	public void Synchronize(TimeStampedMessage msg) {
		time = Math.max(time, msg.getLogicalTimeStamp());
		time++;
	}
	public String toString(){
		return "[TimeStamp: "+ time + "]";
	}

	@Override
	public int getTimeStamp(int i) {
	    throw new RuntimeException("error in LogicalClock class's getTimeStamp(int i) method");
	}

	public int compare(int m1, int m2) {
		return m1 - m2;
	}
	public int get_size(){
		return 1;
	}
	public int get_id(){
		return 0;
	}
	public String get_type(){
		return "logical";
	}

}
