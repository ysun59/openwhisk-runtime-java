package com.github.chen0040.tensorflow.classifiers.images;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import com.github.chen0040.tensorflow.classifiers.images.models.inception.InceptionImageClassifier;
import com.github.chen0040.tensorflow.classifiers.images.utils.ResourceUtils;
import com.google.gson.JsonObject;

public class InceptionImageClassifierDemo {

    static InceptionImageClassifier classifier = null;

    public static InceptionImageClassifier init_classifier(){
        InceptionImageClassifier cls = new InceptionImageClassifier();
        try {
            cls.load_model(ResourceUtils.getInputStream("tf_models/tensorflow_inception_graph.pb"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        cls.load_labels(ResourceUtils.getInputStream("tf_models/imagenet_comp_graph_label_strings.txt"));
        return cls;
    }

    public static JsonObject predict(int index) {
        String[] image_names = new String[] { "tiger", "lion", "airplane", "eagle" };
        String file_name = image_names[index];
        String image_path = "images/inception/" + file_name + ".jpg";
        BufferedImage img = null;
        try {
            img = ResourceUtils.getImage(image_path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String predicted_label = classifier.predict_image(img);
        
        JsonObject response = new JsonObject();
        response.addProperty("predicted", predicted_label);
        return response;
    }

//    public static void main(String[] args) throws IOException {
//        long start = System.currentTimeMillis();
//        String[] image_names = new String[] { "tiger", "lion", "airplane", "eagle" };
//        
//        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("START tiger");
//                predict("tiger");
//                System.out.println("STOP tiger");
//            }
//        });
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("START lion");
//                predict("lion");
//                System.out.println("STOP lion");
//            }
//        });
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("START airplane");
//                predict("airplane");
//                System.out.println("STOP airplane");
//            }
//        });
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("START eagle");
//                predict("eagle");
//                System.out.println("STOP eagle");
//            }
//        });
//        executor.shutdown();
//        long end = System.currentTimeMillis();
//        System.out.println("EXEC2: "+(end-start)/1000.);
//    }

     public static JsonObject main(JsonObject args, Map<String, Object> globals, int id) {
         synchronized (globals) {
             if (!globals.containsKey("classifier")){
                 globals.put("classifier", init_classifier());
             }
             classifier = (InceptionImageClassifier) globals.get("classifier");
        }
         return predict(args.getAsJsonPrimitive("index").getAsInt());
     }
}
