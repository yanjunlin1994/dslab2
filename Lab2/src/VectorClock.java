import java.util.*;
public class VectorClock implements ClockService {
	private int[] times;
	private int id;
	/**
	 * Constructor.
	 * @param size size of vector
	 * @param i my own id
	 */
	public VectorClock(int size, int i){
		times = new int[size];
		id = i;
		times[id] = 1;
	}
	/**
	 * Increments the time in its own position in array.
	 */
	@Override
	public void increment() {
		(this.times[id])++;
		return;
	}
	/**
     * Returns time in my own position in array.
     */
	@Override
	public int getTimeStamp() {
		return times[id];
	}
	/**
	 * Synchronizes all its vector array.
	 */
	@Override
	public void Synchronize(TimeStampedMessage msg) {
		for (int i = 0; i < times.length; i++){
			times[i] = Math.max(msg.getTimeStamp(i),times[i]);
		}
		//increments after taking the maximum value
		times[id]++;
		return;
	}
	/**
	 * Gets ith element's time stamp.
	 */
	@Override
	public int getTimeStamp(int i) {
		return times[i];
	}
	public String toString(){
        return "[TimeStamp: "+ Arrays.toString(this.times) + "]";
    }
	/**
	 * vector clock doesn't need this method.
	 * if accidentally used, throw an exception.
	 */
	/**
	 * Returns:
	 * flag = 1; m1 > m2;
	 * flag = 0; m1 and m2 can not be compared;
	 * flag = -1; m1 < m2;
	 * 
	 */
	public int compare(int[] m1, int[] m2) {
		int flag = 0;
		for (int i = 0;i < m1.length; i++){
			if(flag == 1 && m1[i] < m2[i]) {
			    return 0;
			}
			if(flag == -1 && m1[i] > m2[i]) {
			    return 0;
			}
			if(flag == 0 && m1[i] < m2[i]){
				flag = -1;
			} else if (flag == 0 && m1[i] > m2[i]){
				flag = 1;
			}
		}
		return flag;
	}
}
