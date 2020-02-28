/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.controller;

import com.andrechristikan.helper.ParserHelper;
import com.andrechristikan.verticle.LoginVerticle;
import io.vertx.core.Vertx;
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
    private final String eventBusService = "login";
    private final Vertx vertx;
    
    protected JsonObject systemMessages;
    protected JsonObject responseMessages;
    protected JsonObject mainConfigs;
    protected JsonObject eventBusServiceConfigs;
    
    public LoginController(Vertx vertx){
        this.vertx = vertx;
        this.logger = LoggerFactory.getLogger(LoginVerticle.class);
        this.parser = new ParserHelper();
        
        // Message
        SharedData sharedData = this.vertx.sharedData();
        LocalMap<String, JsonObject> jMapData = sharedData.getLocalMap("vertx");
        this.systemMessages = jMapData.get("messages.system").getJsonObject(this.eventBusService);
        this.responseMessages = jMapData.get("messages.response").getJsonObject(this.eventBusService);
        this.mainConfigs = jMapData.get("configs.main");
        this.eventBusServiceConfigs = jMapData.get("configs.eventbusservice");
        
        this.logger.info(this.systemMessages.getJsonObject("controller").getString("create"));
    }
    
    public void login(RoutingContext ctx) {
        this.logger.info(this.systemMessages.getJsonObject("controller").getJsonObject("login").getString("start"));
        
        ctx.response().setStatusCode(200);
        
        this.logger.info(this.systemMessages.getJsonObject("controller").getJsonObject("login").getString("end"));
        ctx.response().end("abcd");
}
    
}
