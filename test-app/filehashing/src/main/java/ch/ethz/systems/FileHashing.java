package ch.ethz.systems;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.io.InputStream;
import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;
import com.google.gson.JsonObject;
import io.minio.MinioClient;

public class FileHashing {

	private static final int size = 2*1024*1024;
    private static final String storage = "http://r630-01:9000";

    private static MinioClient createconn() {
        try {
            return new MinioClient(storage, "keykey", "secretsecret");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // TODO - is this Minioclient thread safe?
	private static String run(MinioClient minioClient, int seed, byte[] buffer) {
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

	private static MinioClient getConn(ConcurrentHashMap<String, Object> cglobals) {
		MinioClient con = null;
		String key = String.format("minio-%d",Thread.currentThread().getId());
    	if (!cglobals.containsKey(key)) {
    		con = createconn();
    		cglobals.put(key, con);
    	} else {
    		con = (MinioClient) cglobals.get(key);
    	}
    	return con;
	}

	private static byte[] getBuffer(ConcurrentHashMap<String, Object> cglobals) {
		byte[] buffer = null;
		String key = String.format("buffer-%d",Thread.currentThread().getId());
    	if (!cglobals.containsKey(key)) {
    		buffer = new byte[size];
    		cglobals.put(key, buffer);
    	} else {
    		buffer = (byte[]) cglobals.get(key);
    	}
    	return buffer;
	}

    public static JsonObject main(JsonObject args, Map<String, Object> globals, int id) {
    	ConcurrentHashMap<String, Object> cglobals = (ConcurrentHashMap<String, Object>) globals;
    	String hash = null;
        long time = System.currentTimeMillis();


        if (args.has("seed")) {
    		hash = run(getConn(cglobals), args.getAsJsonPrimitive("seed").getAsInt(), getBuffer(cglobals));
    	}

    	JsonObject response = new JsonObject();
    	response.addProperty("hash", hash);
    	response.addProperty("time", System.currentTimeMillis() - time);
    	return response;
    }
}


