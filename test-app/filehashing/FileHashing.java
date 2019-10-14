import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

import com.google.gson.JsonObject;

public class FileHashing {

	private static final int size = 1024;

	private static int run(int seed) {
		try {
			return Arrays.hashCode(Files.readAllBytes(Paths.get(String.format("/tmp/file-%d.dat", seed))));	
		} catch(IOException e) {
			e.printStackTrace();
			return 0;
		}
	}	

public static JsonObject main(JsonObject args) {
    	int hash = 0;

    	if (args.has("seed")) {
    		hash = run(args.getAsJsonPrimitive("seed").getAsInt());
    	}

    	JsonObject response = new JsonObject();
    	response.addProperty("hash", String.format("%d", hash));
    	return response;
    }
}
