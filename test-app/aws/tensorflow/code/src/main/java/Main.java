
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import com.amazonaws.services.lambda.runtime.Context;

import images.models.inception.InceptionImageClassifier;
import images.utils.ResourceUtils;
import util.AwsUtil;
import util.MemoryUtil;

public class Main {

	public static InceptionImageClassifier classifier = null;
	
	public static void init_classifier() {
		classifier = new InceptionImageClassifier();
		try {
			classifier.load_model(AwsUtil.getS3Stream("tensorflow_inception_graph.pb"));
			classifier.load_labels(AwsUtil.getS3Stream("imagenet_comp_graph_label_strings.txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void predict(int index) {
		if (classifier == null) {
			init_classifier();
		}
		String[] image_names = new String[] { "tiger", "lion", "airplane", "eagle" };
		String file_name = image_names[index];
		String image_path = file_name + ".jpg";
		BufferedImage img = null;

		try {
			img = ResourceUtils.getImage(AwsUtil.getS3Stream(image_path));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String predicted_label = classifier.predict_image(img);
	}

	public String handleRequest(Object data, Context context) {
        LinkedHashMap request = (LinkedHashMap) data;
        //request.get("input")

        double current_utilization = MemoryUtil.current_utilization_runtime();
        init_classifier();
        double current_utilization2 = MemoryUtil.current_utilization_runtime();
        predict(0);
        double current_utilization3 = MemoryUtil.current_utilization_runtime();
        String output =  Double.toString(current_utilization)+" "+Double.toString(current_utilization2)
            +" "+Double.toString(current_utilization3);

        return output;
    }
	
	public static void main(String[] args) throws IOException {
		Runtime rt = Runtime.getRuntime();
		int concurrency = 1;
		int number_of_tasks = 100;

		final long free = rt.freeMemory();

		long start = System.currentTimeMillis();
		init_classifier();
		long free_after_model = rt.freeMemory();
		System.out.println("Memory for the model: " + Double.toString((free - free_after_model) / 1000000.));
		long end = System.currentTimeMillis();
		System.out.println("Init time: " + (end - start) / 1000.);

		start = System.currentTimeMillis();
		ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newFixedThreadPool(concurrency);
		Future[] futures = new Future[number_of_tasks];
		for (int i = 0; i < number_of_tasks; i++) {
			futures[i] = tpe.submit(() -> {
				predict(0);
				System.out.println("Memory for the model: " + Double.toString((free - rt.freeMemory()) / 1000000.));
				return 0;
			});
		}
		for (int i = 0; i < number_of_tasks; i++) {
			try {
				futures[i].get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

		end = System.currentTimeMillis();
		System.out.println("Memory for the model: " + Double.toString((free - rt.freeMemory()) / 1000000.));
		System.out.println("EXE time: " + (end - start) / 1000.);

		tpe.shutdown();
	}

}
