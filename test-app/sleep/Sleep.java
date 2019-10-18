import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.io.InputStream;
import com.google.gson.JsonObject;
import io.minio.MinioClient;
import java.lang.Thread;

public class Sleep{

	private static int run(int time) {
        try{
            Thread.sleep(time);    
        }catch(Exception e){}
        return time;
	}

    public static JsonObject main(JsonObject args) {
    	int time = 0;

    	if (args.has("time")) {
    		time = run(args.getAsJsonPrimitive("time").getAsInt());
    	}

    	JsonObject response = new JsonObject();
    	response.addProperty("time", String.format("%d", time));
    	return response;
    }
}


