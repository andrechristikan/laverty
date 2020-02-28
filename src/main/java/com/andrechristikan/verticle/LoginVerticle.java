/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.verticle;

import com.andrechristikan.Server;
import com.andrechristikan.helper.ParserHelper;
import com.andrechristikan.services.LoginService;
import com.andrechristikan.services.impl.LoginServiceImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.serviceproxy.ServiceBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Syn-User
 */
public class LoginVerticle extends AbstractVerticle{
    
    private final Logger logger;
    private final ParserHelper parser;
    private final String eventBusService = "login";
    
    protected JsonObject systemMessages;
    protected JsonObject responseMessages;
    protected JsonObject mainConfigs;
    protected JsonObject eventBusServiceConfigs;
    
    public LoginVerticle(){
        this.logger = LoggerFactory.getLogger(LoginVerticle.class);
        this.parser = new ParserHelper();
    }
    
    @Override
    public void start(Promise<Void> promise) throws Exception {
        
        //Config
        this.mainConfigs = config().getJsonObject("main");
        this.eventBusServiceConfigs = config().getJsonObject("eventbusservice").getJsonObject(this.eventBusService);
        String eventBusServiceName = this.eventBusServiceConfigs.getString("address");
        
        // Message
        SharedData sharedData = this.vertx.sharedData();
        LocalMap<String, JsonObject> jMapData = sharedData.getLocalMap("vertx");
        this.systemMessages = jMapData.get("messages.system").getJsonObject(this.eventBusService);
        this.responseMessages = jMapData.get("messages.response").getJsonObject(this.eventBusService);
  
     
        this.logger.info(this.systemMessages.getString("start").replace("#eventBusServiceName", eventBusServiceName));
        ServiceBinder binder = new ServiceBinder(this.vertx);
        binder.setAddress(eventBusServiceName).register(LoginService.class, new LoginServiceImpl());
        this.logger.info(this.systemMessages.getString("end").replace("#eventBusServiceName", eventBusServiceName));
        
        
    }
}
