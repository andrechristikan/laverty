/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.services.implement;

import com.andrechristikan.helper.DatabaseHelper;
import com.andrechristikan.http.Response;
import com.andrechristikan.services.LoginService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.pgclient.PgPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Syn-User
 */
public class LoginServiceImplement implements LoginService {
    
    private final Logger logger;
    private final Vertx vertx;
    private final String service;
    private final DatabaseHelper databaseHelper;
    
    protected JsonObject systemMessages;
    protected JsonObject responseMessages;
    protected JsonObject mainConfigs;
    protected JsonObject serviceConfigs;
    
    protected PgPool poolConnection;

    public LoginServiceImplement(Vertx vertx) {
        this.logger = LoggerFactory.getLogger(LoginServiceImplement.class);
        this.vertx = vertx;
        this.service = "login";
        this.databaseHelper = new DatabaseHelper(vertx, this.service);
        
        // Message & Config
        this.setConfigs();
        this.setMessages();
        this.setDatabaseConnection();


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
        this.systemMessages = jMapData.get("messages.system").getJsonObject("service").getJsonObject(this.service).getJsonObject("implement");
        this.responseMessages = jMapData.get("messages.response").getJsonObject("service").getJsonObject(this.service).getJsonObject("implement");
    }
    
    private void setDatabaseConnection(){
        this.poolConnection = this.databaseHelper.createPool();
        this.logger.info(this.systemMessages.getString("databae-connection").replace("#serviceAddress", this.serviceConfigs.getString("address")));
    }

    @Override
    public void login(Handler<AsyncResult<String>> resultHandler){
        String response = Response.DataStructure(0, this.responseMessages.getString("success"));
        resultHandler.handle(Future.succeededFuture(response));
    }


}
