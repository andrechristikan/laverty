package com.andrechristikan.http;

import com.andrechristikan.core.CoreHelper;
import com.andrechristikan.helper.DatabaseHelper;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.LoggerFactory;

public class Response extends CoreHelper {

    private JsonObject data = new JsonObject();
    private HttpServerResponse httpResponse;

    public Response(Vertx vertx){
        super(vertx);
        logger = LoggerFactory.getLogger(DatabaseHelper.class);
    }

    public void create(HttpServerResponse httpResponse){
        
        confAsJsonObject("main.response").forEach(action ->{
            httpResponse.putHeader(action.getKey(),confAsJsonObject("main.response").getString(action.getKey()));
        });
        
        this.httpResponse = httpResponse;
    }

    public void response(int code){
        this.httpResponse.setStatusCode(code);
        this.httpResponse.end(this.data.toString());
    }

    public void response(int code, String data){
        this.httpResponse.setStatusCode(code);
        this.httpResponse.end(data);
    }

    public void dataStructure(String status, String message) {
        JsonObject result = new JsonObject();
        result.put("status", status);
        result.put("message", message);
        this.data = result;
    }

    public void dataStructure(String status, String message, JsonObject jo) {
        JsonObject result = new JsonObject();
        result.put("status", status);
        result.put("message", message);
        result.put("data", jo);
        this.data = result;
    }

    public void dataStructure(String status, String message, JsonArray ja) {
        JsonObject result = new JsonObject();
        result.put("status", status);
        result.put("message", message);
        result.put("data", ja);
        this.data = result;
    }

    // for list
    public void dataStructure(String status, String message, JsonArray ja, String countData, String totalPage) {
        JsonObject result = new JsonObject();
        result.put("status", status);
        result.put("message", message);
        result.put("countData", countData);
        result.put("totalPage", totalPage);
        result.put("data", ja);
        this.data = result;
    }

    public static JsonObject dataStructureJson(String status, String message) {
        JsonObject result = new JsonObject();
        result.put("status", status);
        result.put("message", message);
        return result;
    }

    public static JsonObject dataStructureJson(String status, String message, JsonObject jo) {
        JsonObject result = new JsonObject();
        result.put("status", status);
        result.put("message", message);
        result.put("data", jo);
        return result;
    }

    public static JsonObject dataStructureJson(String status, String message, JsonArray ja) {
        JsonObject result = new JsonObject();
        result.put("status", status);
        result.put("message", message);
        result.put("data", ja);
        return result;
    }

    // for list
    public static JsonObject dataStructureJson(String status, String message, JsonArray ja, String countData, String totalPage) {
        JsonObject result = new JsonObject();
        result.put("status", status);
        result.put("message", message);
        result.put("countData", countData);
        result.put("totalPage", totalPage);
        result.put("data", ja);

        return result;
    }


    public static String dataStructureAsString(String status, String message) {
        JsonObject result = new JsonObject();
        result.put("status", status);
        result.put("message", message);
        return result.toString();
    }

    public static String dataStructureAsString(String status, String message, JsonObject jo) {
        JsonObject result = new JsonObject();
        result.put("status", status);
        result.put("message", message);
        result.put("data", jo);
        return result.toString();
    }

    public static String dataStructureAsString(String status, String message, JsonArray ja) {
        JsonObject result = new JsonObject();
        result.put("status", status);
        result.put("message", message);
        result.put("data", ja);
        return result.toString();
    }

    // for list
    public static String dataStructureAsString(String status, String message, JsonArray ja, String countData, String totalPage) {
        JsonObject result = new JsonObject();
        result.put("status", status);
        result.put("message", message);
        result.put("countData", countData);
        result.put("totalPage", totalPage);
        result.put("data", ja);

        return result.toString();
    }

}
