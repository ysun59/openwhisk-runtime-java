package ch.ethz.systems;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import com.google.gson.JsonObject;
import io.minio.MinioClient;

public class Thumbnail {

    private static final String storage = "http://r630-01:9000";

    private static MinioClient createconn() {
        try {
            return new MinioClient(storage, "keykey", "secretsecret");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // TODO - is this Minioclient thread safe?
	private static String run(MinioClient minioClient, int seed) {
		String input = String.format("img-%d.jpeg", seed);
		String output = String.format("img-%d-thumbnail.jpeg", seed);
		try {
			InputStream stream = minioClient.getObject("files", input);
			BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
			
			img.createGraphics().drawImage(ImageIO.read(stream).getScaledInstance(100, 100, Image.SCALE_SMOOTH), 0, 0, null);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, "jpg", baos);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());			
			minioClient.putObject("files", output, bais, "image/jpeg");
			stream.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
        return output;
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

    public static JsonObject main(JsonObject args, Map<String, Object> globals, int id) {
    	ConcurrentHashMap<String, Object> cglobals = (ConcurrentHashMap<String, Object>) globals;
    	String hash = null;
        long time = System.currentTimeMillis();


        if (args.has("seed")) {
    		hash = run(getConn(cglobals), args.getAsJsonPrimitive("seed").getAsInt());
    	}

    	JsonObject response = new JsonObject();
    	response.addProperty("hash", hash);
    	response.addProperty("time", System.currentTimeMillis() - time);
    	return response;
    }
}


