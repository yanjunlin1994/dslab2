
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
public class Logger {
    String myname;
    List<TimeStampedMessage> messageList;
    Queue<TimeStampedMessage> messageQueue;
    String clockType;
    public Logger(String name, String ct){
        this.myname = name;
        this.messageList = new ArrayList<TimeStampedMessage>();
        this.messageQueue = new ArrayDeque<TimeStampedMessage>();
        this.clockType = ct;
        Thread listner = new Thread(new LogListener(myname, messageQueue));
        listner.start();
    }
    public void runNow() {
        while (true){
            InputStreamReader isrd = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isrd);
            try {
                String input = br.readLine();
                if (input.equals("p")) {
                    this.print();
                } else {
                    continue;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }  
        }  
    }
    public synchronized void print(){
        String FILENAME = "./logfile.txt";
        BufferedWriter bw = null;
        FileWriter fw = null;
        
        while (!this.messageQueue.isEmpty()){   
            this.messageList.add(this.messageQueue.poll());
        }
        if (messageList.size() > 0) {
            Collections.sort(messageList, new CompareByTimeStamp());
            try {
                fw = new FileWriter(FILENAME);
                bw = new BufferedWriter(fw);
                for (int i = 0; i<messageList.size()-1;i++){    
                    for (int j = i+1;j<messageList.size();j++){
                        if (isConcurrent(clockType, messageList.get(i), messageList.get(j))){
                            bw.write(">>>>>> These two message are concurrent>>>>\n");
                            bw.write(messageList.get(i).toString());
                            bw.newLine();
                            bw.write(messageList.get(j).toString());
                            bw.newLine();
                            bw.write("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
                        }
                    }
                }
                int[] Mark = new int[messageList.size()];
                for (int i = 0; i<messageList.size();i++){
                    if(Mark[i]!=1){
                        ArrayList<TimeStampedMessage> seqList = new ArrayList<TimeStampedMessage>();
                        seqList.add(messageList.get(i));
                        for (int j = i + 1;j<messageList.size();j++){
                            if (!isConcurrent(clockType, seqList.get(seqList.size()-1),messageList.get(j))){
                                seqList.add(messageList.get(j));
                                Mark[j] = 1;
                            }
                        }
                        bw.write(">>>>> These messages are sequential>>>>>>>>\n");
                        for (TimeStampedMessage msg : seqList){
                            bw.write(msg.toString());
                            bw.newLine();
                        }
                        //Arrays.fill(Mark, 0);
                        bw.write("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
                    }
                }     
            }  catch(Exception e) {

                e.printStackTrace();

            } finally {
                try {

                    if (bw != null)
                        bw.close();

                    if (fw != null)
                        fw.close();

                } catch (IOException ex) {

                    ex.printStackTrace();

                }
                
            }
            
            
            /*
            if (!ifbefC) {
                System.out.println(messageList.get(i));
            }
            */
            
            
//            Iterator<TimeStampedMessage> itr = messageList.iterator();
//            System.out.println("----------log's sorted file list---------");
//            while (itr.hasNext()) {
//                System.out.println(itr.next());
//            }
//            System.out.println("---------log's sorted file list end----------");
        }
        System.out.println("write to file succeed");
        
    }
    private boolean isConcurrent(String clockt, TimeStampedMessage t1, TimeStampedMessage t2) {
        if (clockt.equals("vector")) {
            int[] m1 = t1.getTimeStamps();
            int[] m2 = t2.getTimeStamps();
            int flag = 0;
            for (int i = 0; i < m1.length; i++) {
                if(flag == 1 && m1[i] < m2[i]) {
                    return true;
                }
                if(flag == -1 && m1[i] > m2[i]) {
                    return true;
                }
                if(flag == 0 && m1[i] < m2[i]) {
                    flag = -1;
                } else if (flag == 0 && m1[i] > m2[i]){
                    flag = 1;
                }
            }
            return false; 
        } else {
            int ma = t1.getTimeStamp();
            int mb = t2.getTimeStamp();
            return ((ma == mb)? true: false);
        }
        
    }
}
