package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class AwsUtil {
    
    private static AmazonS3 s3 = null;
    
    private static void initS3(){
        if (s3 == null){
            s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
        }
    }
    
    public static void downloadFromS3(String file_name){
        initS3();
        
        try {
            S3Object o = s3.getObject("hpyindex", "bmk/"+file_name);
            S3ObjectInputStream s3is = o.getObjectContent();
            FileOutputStream fos = new FileOutputStream(new File(file_name));
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3is.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
            s3is.close();
            fos.close();
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    
    public static S3ObjectInputStream getS3Stream(String file_name){
        initS3();
        S3Object o = s3.getObject("hpyindex", "bmk/"+file_name);
        return o.getObjectContent();
    }
}
