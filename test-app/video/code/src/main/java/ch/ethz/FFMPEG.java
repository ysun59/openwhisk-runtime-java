package ch.ethz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

import io.minio.MinioClient;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

public class FFMPEG {

	public static MinioClient minioClient = null;

	private static void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {

		try (FileOutputStream outputStream = new FileOutputStream(file)) {

			int read;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		}

	}

	public static void init_classifier() {
		try {
			// cls.load_model(ResourceUtils.getInputStream("tf_models/tensorflow_inception_graph.pb"));
			minioClient = new MinioClient("http://r630-02:9000", "keykey", "secretsecret");
			InputStream is = minioClient.getObject("video", "ffmpeg");
			copyInputStreamToFile(is, new File("ffmpeg"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void ffmpeg(String fileName) throws Exception{
		FFmpeg ffmpeg = new FFmpeg("ffmpeg");

		FFmpegBuilder builder = new FFmpegBuilder()

		  .setInput(fileName)     // Filename, or a FFmpegProbeResult
		  .overrideOutputFiles(true) // Override the output if it exists

		  .addOutput("out"+fileName)   // Filename for the destination
		    .setFormat("mp4")        // Format is inferred from filename, or can be set
		    .setVideoResolution(640, 480) // at 640x480 resolution

		    .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
		    .done();

		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);

		// Run a one-pass encode
		executor.createJob(builder).run();
		new File("out"+fileName).delete();
	}

	public static void transform(int id) {
		try {
			InputStream is = minioClient.getObject("video", "1.mp4");
			String fileName = Integer.toString(id) + ".mp4";
			copyInputStreamToFile(is, new File(fileName));
			ffmpeg(fileName);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws IOException {
		HashMap<String, Object> m = new HashMap<String, Object>();
		main(null, m, 0);
	}

	public static JsonObject main(JsonObject args, Map<String, Object> globals, int id) {
		boolean slow_start = true;
		synchronized (globals) {
			if (!globals.containsKey("minio")) {
				init_classifier();
				globals.put("minio", minioClient);
			} else {
				minioClient = (MinioClient) globals.get("minio");
				slow_start = false;
			}
		}

		transform(id);
		JsonObject response = new JsonObject();
		response.addProperty("slow_start", slow_start ? 1 : 0);
		return response;
	}
}
