package ch.ethz.systems;

import com.google.gson.JsonObject;
import java.lang.Thread;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Paths;


public class Sleep{
    private static String readAllBytes(String filePath){
        String content = "";
        try{
            content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
        }
        catch (IOException e) {}
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

    public static int setSlowStart(Map<String, Object> globals){
        int isSlowStart = 0;
        synchronized(globals){
            Object ss = globals.get("slow_start");
            if (ss==null){
                globals.put("slow_start", new Object());
                isSlowStart = 1;
            }
        }
        return isSlowStart;
    }


	private static int run(int time) {
        try{
            Thread.sleep(time);
        }catch(Exception e){}
        return time;
	}

    public static JsonObject main(JsonObject args, Map<String, Object> globals, int id) {
    	int time = 1000;

        //double mem1 = current_utilization_runtime();
        //int ss = 1;
        //int ss = setSlowStart(globals);

    	if (args.has("time")) {
            time = args.getAsJsonPrimitive("time").getAsInt();
    	}
        run(time);

    	JsonObject response = new JsonObject();
        /*
        double mem2 = current_utilization_runtime();
    	response.addProperty("memory", String.format("%f %f", mem1, mem2));
        response.addProperty("slow_start", String.format("%d", ss));
        */
    	response.addProperty("slept", String.format("%d", time));

        return response;

    }

}


