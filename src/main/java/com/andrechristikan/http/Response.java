package com.andrechristikan.http;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;

public class Response {

    protected static JsonObject mainConfigs;
    protected static JsonObject responseConfig;
    
    private JsonObject data = new JsonObject();
    private HttpServerResponse httpResponse;

    public Response(Vertx vertx){
        this.setConfigs(vertx);
    }

    private void setConfigs(Vertx vertx){
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, JsonObject> jMapData = sharedData.getLocalMap("vertx");
        mainConfigs = jMapData.get("configs.main");
        responseConfig = mainConfigs.getJsonObject("response");
    }

    public void create(HttpServerResponse httpResponse){
        
        responseConfig.forEach(action ->{
            httpResponse.putHeader(action.getKey(),responseConfig.getString(action.getKey()));
        });
        
        this.httpResponse = httpResponse;
    }
    
    public void response(int code){
        this.httpResponse.setStatusCode(code);
        this.httpResponse.end(this.data.toString());
    }

    public void dataStructure(int status, String message) {
        JsonObject result = new JsonObject();
        result.put("status", status);
        result.put("message", message);
        this.data = result;
    }

    public void dataStructure(int status, String message, JsonObject jo) {
        JsonObject result = new JsonObject();
        result.put("status", status);
        result.put("message", message);
        result.put("data", jo);
        this.data = result;
    }

    public void dataStructure(int status, String message, JsonArray ja) {
        JsonObject result = new JsonObject();
        result.put("status", status);
        result.put("message", message);
        result.put("data", ja);
        this.data = result;
    }

    // for list
    public void dataStructure(int status, String message, JsonArray ja, int countData, int totalPage) {
        JsonObject result = new JsonObject();
        result.put("status", status);
        result.put("message", message);
        result.put("countData", countData);
        result.put("totalPage", totalPage);
        result.put("data", ja);
        this.data = result;
    }
}
