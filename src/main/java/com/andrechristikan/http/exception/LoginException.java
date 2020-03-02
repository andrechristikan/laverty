/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.http.exception;

import com.andrechristikan.http.Response;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
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
public class LoginException {
    
    private final Vertx vertx;
    private final Response settingResponse;
    private final Logger logger;
    private final String service;

    protected JsonObject systemMessages;
    protected JsonObject responseMessages;
    
    public LoginException(Vertx vertx){
        this.vertx = vertx;
        this.settingResponse = new Response(vertx);
        this.logger = LoggerFactory.getLogger(LoginException.class);
        this.service = "login";

        // Set Message
        this.setMessages();
    }

    private void setMessages(){
        SharedData sharedData = this.vertx.sharedData();
        LocalMap<String, JsonObject> jMapData = sharedData.getLocalMap("vertx");
        this.systemMessages = jMapData.get("messages.system").getJsonObject("exception").getJsonObject(this.service);
        this.responseMessages = jMapData.get("messages.response").getJsonObject("exception");
    }
    
    public final void handler(RoutingContext ctx){
        this.logger.info(this.systemMessages.getString("start"));
    
        String authorization = ctx.request().headers().get(HttpHeaders.AUTHORIZATION);
        
        if(authorization == null || authorization.trim().equals("")){
            HttpServerResponse response = this.settingResponse.create(ctx);
            String responseData = Response.DataStructure(1, this.responseMessages.getString("login"));

            this.logger.info(this.systemMessages.getString("fail") + " " +this.responseMessages.getString("login"));

            response.setStatusCode(ctx.response().getStatusCode());
            response.end(responseData);
        }else{
            ctx.next();
        }

        this.logger.info(this.systemMessages.getString("end"));

    }
}
