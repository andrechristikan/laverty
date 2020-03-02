/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.http.controller;

import com.andrechristikan.http.Response;
import com.andrechristikan.helper.ParserHelper;
import com.andrechristikan.services.LoginService;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Syn-User
 */
public class LoginController {
    
    private final Logger logger;
    private final ParserHelper parser;
    private final String service;
    private final Vertx vertx;
    private final Response settingResponse;

    private LoginService loginService;

    protected JsonObject systemMessages;
    protected JsonObject responseMessages;
    protected JsonObject mainConfigs;
    protected JsonObject serviceConfigs;
    
    public LoginController(Vertx vertx){

        // init
        this.logger = LoggerFactory.getLogger(LoginController.class);
        this.parser = new ParserHelper();
        this.service = "login";
        this.vertx = vertx;
        this.settingResponse = new Response(this.vertx);
        
        // Message & Config
        this.setConfigs();
        this.setMessages();

        // Proxy
        this.setProxy();
    }

    private void setConfigs(){
        SharedData sharedData = this.vertx.sharedData();
        LocalMap<String, JsonObject> jMapData = sharedData.getLocalMap("vertx");
        this.mainConfigs = jMapData.get("configs.main");
        this.serviceConfigs = jMapData.get("configs.service").getJsonObject(this.service);
    }

    private void setMessages(){
        SharedData sharedData = this.vertx.sharedData();
        LocalMap<String, JsonObject> jMapData = sharedData.getLocalMap("vertx");
        this.systemMessages = jMapData.get("messages.system").getJsonObject("service").getJsonObject(this.service).getJsonObject("controller");
        this.responseMessages = jMapData.get("messages.response").getJsonObject("service").getJsonObject(this.service).getJsonObject("controller");
    }

    private void setProxy(){
        String eventBusServiceName = this.serviceConfigs.getString("address");
        this.loginService = LoginService.createProxy(this.vertx,eventBusServiceName);

        this.logger.info(this.systemMessages.getString("create").replace("#eventBusServiceName", eventBusServiceName));

    }
    
    public void login(RoutingContext ctx) {
        this.logger.info(this.systemMessages.getJsonObject(this.service).getString("start"));

        HttpServerResponse response = this.settingResponse.create(ctx);

        this.loginService.login(funct -> {
            if(funct.succeeded()){
                response.setStatusCode(200);
                response.end(funct.result());
            }else{
                response.setStatusCode(500);
                response.end(funct.cause().getMessage());
            }
        });

        this.logger.info(this.systemMessages.getJsonObject(this.service).getString("end"));

}
    
}
