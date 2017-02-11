import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class TestMain {
    public static void main(String args[]){
        String clockType = args[0];
        Logger log = new Logger("Mr.logger", clockType);
        log.runNow(); 
    }
}
