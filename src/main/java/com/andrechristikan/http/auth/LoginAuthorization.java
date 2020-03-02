/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.http.auth;

import com.andrechristikan.helper.JwtHelper;
import com.andrechristikan.helper.ParserHelper;
import com.andrechristikan.http.Response;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Syn-User
 */
public class LoginAuthorization{
    
    private final Logger logger;
    private final ParserHelper parser;
    private final JwtHelper jwtHelper;
    private final String service;
    private final String serviceMessage;
    private final Vertx vertx;
    private final Response settingResponse;
    
    protected JsonObject systemMessages;
    protected JsonObject responseMessages;
    protected JsonObject mainConfigs;
    protected JsonObject serviceConfigs;
    protected JWTAuth jwtAuthConfig;
    
    
    public LoginAuthorization(Vertx vertx){
        // init
        this.logger = LoggerFactory.getLogger(LoginAuthorization.class);
        this.parser = new ParserHelper();
        this.vertx = vertx;
        this.service = "jwt";
        this.serviceMessage = "authentication";
        this.settingResponse = new Response(this.vertx);
        this.jwtHelper = new JwtHelper(this.vertx);
        
        // Message & Config
        this.setConfigs();
        this.setMessages();
        
        // Jwt Config
        this.jwtAuthConfig = this.jwtHelper.getSettingJwtAuth();
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
        this.systemMessages = jMapData.get("messages.system").getJsonObject(this.serviceMessage);
        this.responseMessages = jMapData.get("messages.response").getJsonObject(this.serviceMessage);
    }
    
    public void handler(RoutingContext ctx){
        
        HttpServerResponse response = this.settingResponse.create(ctx);
        String authorization = ctx.request().headers().get(HttpHeaders.AUTHORIZATION);
        
        if (authorization != null) {
            String[] parts = authorization.split(" ");
            String token = parts[1];
        
            this.jwtAuthConfig.authenticate(new JsonObject().put("jwt", token), checked -> {
                if (checked.succeeded()) {
                    this.logger.info(this.systemMessages.getString("success"));
                    if(ctx.user() == null){
                        ctx.setUser(checked.result());
                    }
                    ctx.next();  
                }else{
                    String message = this.responseMessages.getString("failed");
                    this.logger.info(this.systemMessages.getString("fail")+" "+checked.cause().getMessage());
                    String messageResponse = Response.DataStructure(1,message);
                    response.setStatusCode(401);
                    response.end(messageResponse);
                }
            });
        }else{
            String message = this.responseMessages.getString("token-required");
            this.logger.info(this.systemMessages.getString("fail")+" "+message);
            String messageResponse = Response.DataStructure(1,message);
            response.setStatusCode(403);
            response.end(messageResponse);
        }
                    
    }
}
