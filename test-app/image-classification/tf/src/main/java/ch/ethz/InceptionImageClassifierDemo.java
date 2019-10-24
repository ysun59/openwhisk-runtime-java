package ch.ethz;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.xmlpull.v1.XmlPullParserException;

import com.google.gson.JsonObject;

import ch.ethz.images.models.inception.InceptionImageClassifier;
import ch.ethz.images.utils.ResourceUtils;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidArgumentException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.NoResponseException;

public class InceptionImageClassifierDemo {

    public static InceptionImageClassifier classifier = null;
    public static MinioClient minioClient;
    

    public static void init_classifier(){
        classifier = new InceptionImageClassifier();
        try {
            //cls.load_model(ResourceUtils.getInputStream("tf_models/tensorflow_inception_graph.pb"));
             minioClient = new MinioClient("http://r630-02:9000", "keykey", "secretsecret");
            InputStream is = minioClient.getObject("machinelearning", "tensorflow_inception_graph.pb");
            classifier.load_model(is);
            is = minioClient.getObject("machinelearning", "imagenet_comp_graph_label_strings.txt");
            classifier.load_labels(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static JsonObject predict(int index) {
        init_classifier();
        String[] image_names = new String[] { "tiger", "lion", "airplane", "eagle" };
        String file_name = image_names[index];
        String image_path = file_name + ".jpg";
        BufferedImage img = null;
        
        try {
            img = ResourceUtils.getImage(minioClient.getObject("images", image_path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        String predicted_label = classifier.predict_image(img);
        
        JsonObject response = new JsonObject();
        response.addProperty("predicted", predicted_label);
        
        return response;
    }

    public static void main(String[] args) throws IOException {
        init_classifier();
        long start = System.currentTimeMillis();
        
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("START tiger");
                predict(0);
                System.out.println("STOP tiger");
            }
        });
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("START lion");
//                predict(1);
//                System.out.println("STOP lion");
//            }
//        });
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("START airplane");
//                predict(2);
//                System.out.println("STOP airplane");
//            }
//        });
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("START eagle");
//                predict(3);
//                System.out.println("STOP eagle");
//            }
//        });
        executor.shutdown();
        long end = System.currentTimeMillis();
        System.out.println("EXEC2: "+(end-start)/1000.);
    }

     public static JsonObject main(JsonObject args, Map<String, Object> globals, int id) {
         synchronized (globals) {
             if (!globals.containsKey("classifier")){
                 init_classifier();
                 globals.put("classifier", classifier);
                 globals.put("minio", minioClient);
             } else {
                 classifier = (InceptionImageClassifier) globals.get("classifier");
                 minioClient = (MinioClient) globals.get("minio");
             }
             
        }
         return predict(args.getAsJsonPrimitive("index").getAsInt());
     }
}
