package com.andrechristikan;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.RoutingContext;

public class Response {

    private final Vertx vertx;

    protected JsonObject mainConfigs;
    protected JsonObject responseConfig;

    public Response(Vertx vertx){
        this.vertx = vertx;

        // Config
        this.setConfigs();
    }

    private void setConfigs(){
        SharedData sharedData = this.vertx.sharedData();
        LocalMap<String, JsonObject> jMapData = sharedData.getLocalMap("vertx");
        this.mainConfigs = jMapData.get("configs.main");
        this.responseConfig = this.mainConfigs.getJsonObject("response");
    }

    public HttpServerResponse create(RoutingContext routingContext){
        HttpServerResponse response = routingContext.response();

        this.responseConfig.forEach(action ->{
            response.putHeader(action.getKey(),this.responseConfig.getString(action.getKey()));
        });

        return response;
    }

    public static String DataStructure(int status, String message) {
        JsonObject result = new JsonObject();
        result.put("status", status);
        result.put("message", message);
        return result.toString();
    }

    public static String DataStructure(int status, String message, JsonObject jo) {
        JsonObject result = new JsonObject();
        result.put("status", status);
        result.put("message", message);
        result.put("data", jo);
        return result.toString();
    }

    public static String DataStructure(int status, String message, JsonArray ja) {
        JsonObject result = new JsonObject();
        result.put("status", status);
        result.put("message", message);
        result.put("data", ja);
        return result.toString();
    }

    // for list
    public static String DataStructure(int status, String message, JsonArray ja, int countData, int totalPage) {
        JsonObject result = new JsonObject();
        result.put("status", status);
        result.put("message", message);
        result.put("countData", countData);
        result.put("totalPage", totalPage);
        result.put("data", ja);
        return result.toString();
    }
}
