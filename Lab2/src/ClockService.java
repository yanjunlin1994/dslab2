
public interface ClockService {
	public void increment();
	public void increment(int i);
	public int getTimeStamp();
	public int getTimeStamp(int i);
	public void Synchronize(TimeStampedMessage msg);
	public String toString();
	public int get_size();
	public int get_id();
	public String get_type();
	
}
