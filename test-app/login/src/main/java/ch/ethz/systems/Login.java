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


public class Login {

    private static final String db = "mongodb://r630-01:27017";

    private static MongoClient createconn() {
        try {
            return new MongoClient(new MongoClientURI(db));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
	private static MongoClient getConn(ConcurrentHashMap<String, Object> cglobals) {
		MongoClient con = null;
		String key = String.format("mongo-%d",Thread.currentThread().getId());
    	if (!cglobals.containsKey(key)) {
    		con = createconn();
    		cglobals.put(key, con);
    	} else {
    		con = (MongoClient) cglobals.get(key);
    	}
    	return con;
	}
	
	public static boolean login(ConcurrentHashMap<String, Object> globals, String username, String password) {
		MongoClient mc = getConn(globals);
		DB database = mc.getDB("mydatabase");
		DBCollection collection = database.getCollection("customers");
		DBObject query = new BasicDBObject("username", username);
		DBCursor cursor = collection.find(query);
		return ((String)cursor.one().get("password")).equals(password);
	}
    
    public static JsonObject main(JsonObject args, Map<String, Object> globals, int id) {
    	ConcurrentHashMap<String, Object> cglobals = (ConcurrentHashMap<String, Object>) globals;
    	JsonObject response = new JsonObject();
        long time = System.currentTimeMillis();

        if (login(cglobals, args.getAsJsonPrimitive("username").getAsString(), args.getAsJsonPrimitive("password").getAsString())) {
        	response.addProperty("succeeded", "true");
        } else {
        	response.addProperty("succeeded", "false");
        }
            	
    	response.addProperty("time", System.currentTimeMillis() - time);
    	return response;
    }
    
    public static void main(String[] args) throws Exception {
    	ConcurrentHashMap<String, Object> cglobals = new ConcurrentHashMap<String, Object>();
    	System.out.println(login(cglobals, "username1", "password1"));
    	System.out.println(login(cglobals, "username1", "password2"));
    }
}


