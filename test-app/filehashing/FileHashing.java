import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.io.InputStream;
import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;
import com.google.gson.JsonObject;
import io.minio.MinioClient;

public class FileHashing {

	private static final int size = 1024;
	private static final byte[] buffer = new byte[size];

    private static MinioClient init() {
        try {
            return new MinioClient("http://10.1.212.71:9000", "keykey", "secretsecret");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // TODO - is this Minioclient thread safe?
	private static String run(MinioClient minioClient, int seed) {
		try {
			InputStream stream = minioClient.getObject("files", String.format("file-%d.dat", seed));
            for (int bytesread = 0;
                 bytesread < size;
                 bytesread += stream.read(buffer, bytesread, size - bytesread));
            stream.close();
            return DatatypeConverter.printHexBinary(MessageDigest.getInstance("MD5").digest(buffer));
		} catch(Exception e) {
			e.printStackTrace();
		}
        return null;
	}

    public static JsonObject main(JsonObject args, Map<String, Object> globals, int id) {
    	String hash = null;
        MinioClient minioClient;
        long time = System.currentTimeMillis();

        //System.out.println(String.format("time %d id %d", globals.get("time"), id));

        synchronized (globals) {
            if (!globals.containsKey("minio")) {
                minioClient = init();
                globals.put("minio", minioClient);
            } else {
                minioClient = (MinioClient) globals.get("minio");
            }
        }

        if (args.has("seed")) {
    		hash = run(minioClient, args.getAsJsonPrimitive("seed").getAsInt());
    	}

    	JsonObject response = new JsonObject();
    	response.addProperty("hash", hash);
    	response.addProperty("time", System.currentTimeMillis() - time);
    	return response;
    }
}


