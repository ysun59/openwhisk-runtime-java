import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Sleep implements RequestHandler<Object, String> {

    private static String readAllBytes(String filePath){
        String content = "";
        try{
            content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
        }
        catch (IOException e) {
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            throw new RuntimeException(sStackTrace);
        }
        return content;
    }

    public static double current_utilization_runtime(){
        String meminfo = readAllBytes("/proc/meminfo").replace("\n", "");
        //total
        Pattern p = Pattern.compile("MemTotal: *(.*?) kB");
        Matcher m = p.matcher(meminfo);
        m.find();
        long memory_total = Long.parseLong(m.group(1));
        
        //free
        p = Pattern.compile("MemAvailable: *(.*?) kB");
        m = p.matcher(meminfo);
        m.find();
        long memory_free = Long.parseLong(m.group(1));
        return ((memory_total - memory_free)/1000.);
        //return (long)((memory_total)/1000.);
    }

    public String handleRequest(Object data, Context context) {
        LinkedHashMap request = (LinkedHashMap) data;
        //request.get("input")

        double current_utilization = current_utilization_runtime();
        
        try{
            byte[] tmp_array = new byte[10000*1024];
            Random r = new Random();
            tmp_array[r.nextInt()%1000] = (byte) (r.nextInt()%1000); 
            System.out.println();
            System.out.println(Arrays.hashCode(tmp_array));
            Thread.sleep(1000);
        } catch(Exception e){}

        double current_utilization2 = current_utilization_runtime();

        String output =  Double.toString(current_utilization)+" "+Double.toString(current_utilization2);

        return output;
    }

}