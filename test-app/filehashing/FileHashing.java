import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.io.InputStream;
import com.google.gson.JsonObject;
import io.minio.MinioClient;

public class FileHashing {

	private static final int size = 1024;
	private static final byte[] buffer = new byte[size];

	private static int run(int seed) {
		try {
 			MinioClient minioClient = new MinioClient("http://r630-02:9000", "keykey", "secretsecret");
			InputStream stream = minioClient.getObject("mydata", String.format("file-%d.dat", seed));
			int bytesread = 0;
			while ((bytesread = stream.read(buffer, bytesread, size - bytesread)) >= 0) ;
			return Arrays.hashCode(buffer);
		} catch(Exception e) {
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


