/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.verticle;

import com.andrechristikan.helper.ParserHelper;
import com.andrechristikan.services.LoginService;
import com.andrechristikan.services.implement.LoginServiceImplement;
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
    private final String service;
    
    protected JsonObject systemMessages;
    protected JsonObject serviceConfigs;
    
    public LoginVerticle(){
        this.logger = LoggerFactory.getLogger(LoginVerticle.class);
        this.service = "login";
    }
    
    @Override
    public void start(Promise<Void> promise) throws Exception {
        
        //Config
        this.serviceConfigs = config().getJsonObject("service").getJsonObject(this.service);
        String eventBusServiceName = this.serviceConfigs.getString("address");
        
        // Message
        SharedData sharedData = this.vertx.sharedData();
        LocalMap<String, JsonObject> jMapData = sharedData.getLocalMap("vertx");
        this.systemMessages = jMapData.get("messages.system").getJsonObject("service").getJsonObject(this.service);
     
        this.logger.info(this.systemMessages.getString("start").replace("#eventBusServiceName", eventBusServiceName));
        ServiceBinder binder = new ServiceBinder(this.vertx);
        binder.setAddress(eventBusServiceName).register(LoginService.class, new LoginServiceImplement(this.vertx));
        this.logger.info(this.systemMessages.getString("end").replace("#eventBusServiceName", eventBusServiceName));

        promise.complete();
    }
}
