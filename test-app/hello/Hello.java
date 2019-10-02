import com.google.gson.JsonObject;

public class Hello {

    static {
        System.out.println("Hello running statics CL:" + Hello.class.getClassLoader());
    }

    public static int iteration = 0;

    public static JsonObject main(JsonObject args) {
            String name = "stranger";
            if (args.has("name"))
                name = args.getAsJsonPrimitive("name").getAsString();
            JsonObject response = new JsonObject();
            response.addProperty("greeting", String.format("Name: %s Iteration: %d", name, iteration++));
            return response;
        }
}
