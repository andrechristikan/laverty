/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.services.implement;

import com.andrechristikan.helper.DatabaseHelper;
import com.andrechristikan.http.Response;
import com.andrechristikan.http.models.UserModel;
import com.andrechristikan.services.LoginService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Transaction;
import java.util.ArrayList;
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
        this.logger.info(this.systemMessages.getString("database-connection").replace("#serviceAddress", this.serviceConfigs.getString("address")));
    }

    @Override
    public void login(Handler<AsyncResult<String>> resultHandler){

        this.databaseHelper.openConnection(this.poolConnection).setHandler(open -> {
            if(open.succeeded()){
                SqlConnection conn = open.result();
                Transaction trans = conn.begin();
                UserModel user = new UserModel(this.vertx, trans);
                
//                user.columnsValue.put("role_id", "user");
//                user.columnsValue.put("username", "andre");
//                user.columnsValue.put("password", "12345");
//                user.columnsValue.put("email", "andre@gmail.com");
//
//                user.save().setHandler(select -> {
//                    if(select.succeeded()){
//                        String message = this.responseMessages.getString("success");
//
//                        trans.commit();
//                        this.logger.info(this.systemMessages.getString("success"));
//                        this.databaseHelper.closeConnection(conn);
//                        String response = Response.DataStructure(0, message, user.toJson());
//                        resultHandler.handle(Future.succeededFuture(response));
//                    }else{
//                        trans.rollback();
//                        String response = Response.DataStructure(1, select.cause().getMessage());
//                        this.logger.error(this.systemMessages.getString("fail") +" "+select.cause().getMessage());
//                        resultHandler.handle(Future.failedFuture(response));
//                    }
//                    trans.close();
//                    conn.close();
//                });

//                ArrayList <String> columns = new ArrayList<>();
//                columns.add("role_id");
//                columns.add("username");
//                columns.add("email");
//                user.select("id").select(columns).select("password").findOne("9a057751-3624-4216-a2ce-66b8fb64b2e6").setHandler(select -> {
//                    if(select.succeeded()){
//                        String message = this.responseMessages.getString("success");
//
//                        trans.commit();
//                        this.logger.info(this.systemMessages.getString("success"));
//                        this.databaseHelper.closeConnection(conn);
//                        String response = Response.DataStructure(0, message, user.toJson());
//                        resultHandler.handle(Future.succeededFuture(response));
//                    }else{
//                        trans.rollback();
//                        String response = Response.DataStructure(1, select.cause().getMessage());
//                        this.logger.error(this.systemMessages.getString("fail") +" "+select.cause().getMessage());
//                        resultHandler.handle(Future.failedFuture(response));
//                    }
//                    trans.close();
//                    conn.close();
//                });


                user.delete("9bb5e295-1627-4fad-b4b8-46755c50c2aa").setHandler(select -> {
                    if(select.succeeded()){
                        String message = this.responseMessages.getString("success");

                        trans.commit();
                        this.logger.info(this.systemMessages.getString("success"));
                        this.databaseHelper.closeConnection(conn);
                        String response = Response.DataStructure(0, message);
                        resultHandler.handle(Future.succeededFuture(response));
                    }else{
                        trans.rollback();
                        String response = Response.DataStructure(1, select.cause().getMessage());
                        this.logger.error(this.systemMessages.getString("fail") +" "+select.cause().getMessage());
                        resultHandler.handle(Future.failedFuture(response));
                    }
                    trans.close();
                    conn.close();
                });
            }else{
                String response = Response.DataStructure(1, open.cause().getMessage());
                this.logger.error(this.systemMessages.getString("fail") +" "+open.cause().getMessage());
                resultHandler.handle(Future.failedFuture(response));
            }
        });
    }


}
