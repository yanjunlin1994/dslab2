/*This is the factory class that generate either logical clock or vector clock. Depends on the cmdline input when starting a process
 * for example if type java "-jar Lab1.jar config.yaml logical" in command line, a logical clock will be generated.*
 * 
 */
public class ClockFactory {
	private MessagePasser mp;
	public ClockFactory(MessagePasser m){
		this.mp = m;
	}
	public ClockService getClockService() {
		if (mp.getClock().equals("logical")) {
			return new LogicalClock();
		}
		else if (mp.getClock().equals("vector")) {
			return new VectorClock(mp.getSize(), mp.getId());
		}
		else {
			throw new RuntimeException("clock type error");
		}
	}
}
