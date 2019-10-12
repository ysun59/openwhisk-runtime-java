import java.util.Arrays;
import java.util.Random;

import com.google.gson.JsonObject;

public class Sort {

	private static final int size = 1024*1024;
	
	private static int run(int seed) {
		int[] array = new int[size];
		array[Math.abs(seed) % size] = seed;
		/*
		Random generator = new Random();
		generator.setSeed(seed);
		for (int i = 0; i < size; i++) {
			array[i] = generator.nextInt();
		}*/
		
		//Arrays.sort(array);
		
		return Arrays.hashCode(array);
		
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
