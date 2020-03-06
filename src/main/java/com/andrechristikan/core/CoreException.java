/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.core;

import com.andrechristikan.http.Response;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.slf4j.Logger;

/**
 *
 * @author Syn-User
 */
public class CoreException {
    
    protected static String service;
    protected static JsonObject systemMessages;
    protected static JsonObject responseMessages;
    protected static Logger logger;
    protected static Response response;
    
    protected CoreException(Vertx vertx){
        this.setMessages(vertx);
    }
    
    private void setMessages(Vertx vertx){
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, JsonObject> jMapData = sharedData.getLocalMap("vertx");
        systemMessages = jMapData.get("messages.system").getJsonObject("exception").getJsonObject(service);
        responseMessages = jMapData.get("messages.response").getJsonObject("exception");
    }
    
    protected static String systemMessage(String path){
        String splitPath[] = path.split(".");
        String message = "";
        JsonObject jMessage = new JsonObject();
        
        for(int i = 0; i < splitPath.length; i++){
            
            if(i == (splitPath.length-1)){
                message = jMessage.getString(splitPath[i]);
            }else{
                jMessage = systemMessages.getJsonObject(splitPath[i]);
            }
        }
        
        return message;
        
    }
    
    protected static String responseMessage(String path){
        String splitPath[] = path.split(".");
        String message = "";
        JsonObject jMessage = new JsonObject();
        
        for(int i = 0; i < splitPath.length; i++){
            
            if(i == (splitPath.length-1)){
                message = jMessage.getString(splitPath[i]);
            }else{
                jMessage = responseMessages.getJsonObject(splitPath[i]);
            }
        }
        
        return message;
        
    }
}
