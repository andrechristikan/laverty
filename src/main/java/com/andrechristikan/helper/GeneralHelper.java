package com.andrechristikan.helper;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;

public class GeneralHelper {

    public static JsonObject setMessages(Vertx vertx){
        JsonObject messages = new JsonObject();
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, JsonObject> jMapData = sharedData.getLocalMap("vertx");
        messages.put("system",jMapData.get("messages.system"));
        messages.put("response",jMapData.get("messages.response"));

        return messages;
    }

    public static JsonObject setConfigs(Vertx vertx){
        JsonObject configs = new JsonObject();
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, JsonObject> jMapData = sharedData.getLocalMap("vertx");
        configs.put("main",jMapData.get("configs.main"));
        configs.put("service",jMapData.get("configs.service"));


        return configs;
    }

    public static String trans(String path, JsonObject messages){
        String splitPath[] = path.split("\\.");
        String message = "";
        JsonObject jMessage = messages;

        for(int i = 0; i < splitPath.length; i++){

            if(i == (splitPath.length-1)){
                message = jMessage.getString(splitPath[i]);
            }else{
                jMessage = jMessage.getJsonObject(splitPath[i]);
            }
        }

        return message;
    }

    public static String conf(String path, JsonObject configs){
        String splitPath[] = path.split("\\.");
        String config = "";
        JsonObject jConfig = configs;

        for(int i = 0; i < splitPath.length; i++){
            if(i == (splitPath.length-1)){
                config = jConfig.getString(splitPath[i]);
            }else{
                jConfig = jConfig.getJsonObject(splitPath[i]);
            }
        }
        return config;
    }

    public static JsonArray confAsJsonArray(String path, JsonObject configs){
        String splitPath[] = path.split("\\.");
        JsonArray config = new JsonArray();
        JsonObject jConfig = configs;

        for(int i = 0; i < splitPath.length; i++){
            if(i == (splitPath.length-1)){
                config = jConfig.getJsonArray(splitPath[i]);
            }else{
                jConfig = jConfig.getJsonObject(splitPath[i]);
            }
        }
        return config;
    }

    public static JsonObject confAsJsonObject(String path, JsonObject configs){
        String splitPath[] = path.split("\\.");
        JsonObject jConfig = configs;

        for(int i = 0; i < splitPath.length; i++){
            jConfig = jConfig.getJsonObject(splitPath[i]);
        }
        return jConfig;
    }

}
