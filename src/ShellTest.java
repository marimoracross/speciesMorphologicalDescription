
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellTest {
        public static void main(String[] args) throws java.io.IOException, java.lang.InterruptedException {
    
        	String[] command ={ "bash", "-c", "echo '(6-) 8-15 4- ( -17 ) × ( 3    - ) 4-9 ( -10 ) cm' | sed 's/\\([0-9]\\) *- *)/\\1\\)/g'"};
        	ProcessBuilder pb = new ProcessBuilder(command);
        	pb.redirectErrorStream(true);
        	Process process = pb.start();
        	process.waitFor();
        	
        	
           	BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); //Get the output
        	
            String line;
            while((line=reader.readLine())!= null){
                System.out.println(line);
                System.out.flush();
            }
            reader.close();
        	
        }
    }