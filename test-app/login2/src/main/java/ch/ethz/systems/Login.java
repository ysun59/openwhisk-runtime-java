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

import java.util.List;
import java.util.ArrayList;


public class Login {

    private static final String db = "mongodb://172.17.0.1:27017";

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
		System.out.println("login function");
		MongoClient mc = getConn(globals);
		System.out.println("1");
		DB database = mc.getDB("mydatabase");
		System.out.println("2");
		DBCollection collection = database.getCollection("customers");
		System.out.println("3");

/*
                List<DBObject> dbList = new ArrayList<DBObject>();
                BasicDBObject doc = new BasicDBObject();
                doc.put("username","sysy2");
                doc.put("password","112");
                dbList.add(doc);
                collection.insert(dbList);
**/

		DBObject query = new BasicDBObject("username", username);
		System.out.println("4");
		DBCursor cursor = collection.find(query);
		System.out.println("5");
		System.out.println("cursor is: " + cursor);
		System.out.println("cursor.one is: " + cursor.one());
/**		return false;
                if (cursor == null) {
                        System.out.println("cursor null");
                }
                DBObject found = cursor.one();
                if (found == null) {
                  return false;
                }
		return false;*/
		String res = (String)cursor.one().get("password");
		System.out.println("6");
		System.out.println("res cursor.one.get(password) is "+res);
		return ((String)cursor.one().get("password")).equals(password);
	}


 
//  public static JsonObject main(JsonObject args) {
    public static JsonObject main(JsonObject args, Map<String, Object> globals, int id) {
	System.out.println("hello 1");
    	ConcurrentHashMap<String, Object> cglobals = (ConcurrentHashMap<String, Object>) globals;
        JsonObject response = new JsonObject();
        long time = System.currentTimeMillis();
	System.out.println("time is "+ time);
	System.out.println("username is "+ args.getAsJsonPrimitive("username").getAsString());
	System.out.println("password is "+ args.getAsJsonPrimitive("password").getAsString());
//      if (login(args.getAsJsonPrimitive("username").getAsString(), args.getAsJsonPrimitive("password").getAsString())) {
        if (login(cglobals, args.getAsJsonPrimitive("username").getAsString(), args.getAsJsonPrimitive("password").getAsString())) {
		System.out.println("if 1");
                response.addProperty("succeeded", "true");
        } else {
		System.out.println("if 2");
                response.addProperty("succeeded", "false");
       }
                
        response.addProperty("time", System.currentTimeMillis() - time);
        return response;
    }
    
    public static void main(String[] args) throws Exception {
	System.out.println("hello 2");
    	ConcurrentHashMap<String, Object> cglobals = new ConcurrentHashMap<String, Object>();
//        System.out.println(login("username1", "password1"));
//        System.out.println(login("username1", "password2"));
    	System.out.println(login(cglobals, "username1", "password1"));
    	System.out.println(login(cglobals, "username1", "password2"));
    }
}
