
import java.util.Comparator;

public class CompareByTimeStamp implements Comparator<TimeStampedMessage>{

    @Override
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
            return Integer.compare(m1, m2);
        }
    }

}

