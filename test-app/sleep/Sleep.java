import com.google.gson.JsonObject;
import java.lang.Thread;
import java.util.Map;

public class Sleep{

	private static int run(int time) {
        try{
            Thread.sleep(time);    
        }catch(Exception e){}
        return time;
	}

    public static JsonObject main(JsonObject args, Map<String, Object> globals, int id) {
    	int time = 1000;

    	if (args.has("time")) {
    		time = run(args.getAsJsonPrimitive("time").getAsInt());
    	}

    	JsonObject response = new JsonObject();
    	response.addProperty("time", String.format("%d", time));
    	return response;
    }

}


