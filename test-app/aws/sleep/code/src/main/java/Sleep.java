import java.util.LinkedHashMap;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import util.MemoryUtil;

public class Sleep implements RequestHandler<Object, String> {

	public static void workload() {
		try{
            Thread.sleep(1000);
        } catch(Exception e){}
	}
	
    public String handleRequest(Object data, Context context) {
        LinkedHashMap request = (LinkedHashMap) data;
        //request.get("input")

        double current_utilization = MemoryUtil.current_utilization_runtime();
        
        workload();

        double current_utilization2 = MemoryUtil.current_utilization_runtime();

        String output =  Double.toString(current_utilization)+" "+Double.toString(current_utilization2);

        return output;
    }

}