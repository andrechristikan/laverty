/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan;

import com.andrechristikan.helper.ParserHelper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Syn-User
 */
public class Server extends AbstractVerticle {
    
    final Logger logger;
    HttpServer server;
    JsonObject systemMessages;
    JsonObject responseMessages;
    JsonObject mainConfigs;
    JsonObject routerConfigs;
    
    ParserHelper parser;
    
    Server(){
        this.logger = LoggerFactory.getLogger(Server.class);
        this.parser = new ParserHelper();
    }
    
    @Override
    public void start(final Promise<Void> promise) throws Exception {
        
        // Config
        this.mainConfigs = config().getJsonObject("main");
        this.routerConfigs = config().getJsonObject("router");
        
        // Message
        SharedData sharedData = this.vertx.sharedData();
        LocalMap<String, JsonObject> mapData = sharedData.getLocalMap("vertx");
        this.systemMessages = mapData.get("messages.system");
        this.responseMessages = mapData.get("messages.response");
        JsonObject systemMessage = this.systemMessages.getJsonObject("server");

        
        
        
        // -- START
        this.logger.info(this.systemMessages.getJsonObject("server").getString("start"));
        
        // Create Security
        Router router = Router.router(this.vertx);
        JsonArray requestConfigHeader = this.mainConfigs.getJsonObject("cors").getJsonArray("header");
        JsonArray requestConfigMethod = this.mainConfigs.getJsonObject("cors").getJsonArray("method");
        CorsHandler cors = CorsHandler.create(this.mainConfigs.getJsonObject("cors").getString("allow-origin"));
        
        // Set cors
        if(!requestConfigHeader.isEmpty()){
            requestConfigHeader.forEach(action -> {
                cors.allowedHeader(action.toString());
            });
        }
        
        if(!requestConfigMethod.isEmpty()){
            requestConfigMethod.forEach(action -> {
                if(action.toString().toUpperCase().equals("GET")){
                    cors.allowedMethod(HttpMethod.GET);
                }
                
                if(action.toString().toUpperCase().equals("POST")){
                    cors.allowedMethod(HttpMethod.POST);
                }
                
                if(action.toString().toUpperCase().equals("PUT")){
                    cors.allowedMethod(HttpMethod.PUT);
                }
                
                if(action.toString().toUpperCase().equals("DELETE")){
                    cors.allowedMethod(HttpMethod.DELETE);
                }
                
                if(action.toString().toUpperCase().equals("OPTIONS")){
                    cors.allowedMethod(HttpMethod.OPTIONS);
                }
                
                if(action.toString().toUpperCase().equals("HEAD")){
                    cors.allowedMethod(HttpMethod.HEAD);
                }
            });
        }
        
        // Put setting into router
        router.route().handler(cors);
        
        // Upload Setting
        router.route().handler(
            BodyHandler.create()
                .setUploadsDirectory(this.mainConfigs.getJsonObject(this.mainConfigs.getString("environment")).getJsonObject("upload-files").getString("files-uploaded"))
                .setDeleteUploadedFilesOnEnd(this.parser.parseBoolean(this.mainConfigs.getJsonObject(this.mainConfigs.getString("environment")).getJsonObject("upload-files").getString("delete-on-end"), Boolean.TRUE))
        );
        router.route().handler(StaticHandler.create());
        
        
        
        // Router
        
        
        
        
        
        
        
        
        // No SSL requested, start a non-SSL HTTP server.
        this.server = this.vertx.createHttpServer(
                new HttpServerOptions()
                        .setPort(this.parser.parseInt(this.mainConfigs.getJsonObject(this.mainConfigs.getString("environment")).getJsonObject("http-server").getString("port"),8181))
                        .setHost(this.parser.parseString(this.mainConfigs.getJsonObject(this.mainConfigs.getString("environment")).getJsonObject("http-server").getString("address"),"127.0.0.1")));
        this.server.requestHandler(router)
            .listen(
                ar -> {
                    if(ar.succeeded()){
                        this.logger.info(
                            systemMessage
                                .getString("ongoing")
                                .replace("#IP_ADDRESS", this.parser.parseString(this.mainConfigs.getJsonObject(this.mainConfigs.getString("environment")).getJsonObject("http-server").getString("address"),"127.0.0.1"))
                                .replace("#PORT", this.parser.parseString(this.mainConfigs.getJsonObject(this.mainConfigs.getString("environment")).getJsonObject("http-server").getString("port"),"8181"))
                        );
                        promise.complete();
                    }else{
                        this.logger.error(ar.cause().getMessage());
                        promise.fail(ar.cause().getMessage());
                    }
            });
        
        
        // -- END
        
    }
    
    @Override
    public void stop(){
        this.server.close();
        this.vertx.close();
    }
}