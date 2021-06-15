package ch.ethz.systems;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import java.io.StringWriter;
import java.io.PrintWriter;
public class Login {
    private static final String db = "mongodb://172.17.0.1";
    private static MongoClient createconn() {
        try {
            return new MongoClient("172.17.0.1", 27017);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
        public static boolean login(String username, String password) {
                MongoClient mc = createconn();
                DB database = mc.getDB("mydatabase");
                DBCollection collection = database.getCollection("customers");
                DBObject query = new BasicDBObject("username", username);
                DBCursor cursor = collection.find(query);
                DBObject found = cursor.one();
                System.out.println("test");
		if (found == null) {
                  return false;
                }
                return ((String)cursor.one().get("password")).equals(password);
        }
    
    public static JsonObject main(JsonObject args) {
        JsonObject response = new JsonObject();
        long time = System.currentTimeMillis();
        if (login(args.getAsJsonPrimitive("username").getAsString(), args.getAsJsonPrimitive("password").getAsString())) {
                response.addProperty("succeeded", "true");
        } else {
                response.addProperty("succeeded", "false");
       }
                
        response.addProperty("time", System.currentTimeMillis() - time);
        return response;
    }
    
    public static void main(String[] args) throws Exception {
        System.out.println(login("username1", "password1"));
        System.out.println(login("username1", "password2"));
    }
}