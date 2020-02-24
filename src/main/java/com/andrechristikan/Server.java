/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan;

import com.andrechristikan.helper.ParserHelper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.Router;
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

        Router router = Router.router(this.vertx);
        router.get("/").handler(hndlr -> {
            HttpServerResponse response = hndlr.response();
            response.setStatusCode(200);
            response.end(this.mainConfigs.toString());
        });
        
        // No SSL requested, start a non-SSL HTTP server.
        this.server = this.vertx.createHttpServer(
                new HttpServerOptions()
                        .setPort(this.parser.parseInt(this.mainConfigs.getJsonObject("http-server").getString("port"), 8181))
                        .setHost(this.parser.parseString(this.mainConfigs.getJsonObject("http-server").getString("address"),"127.0.0.1")));
        this.server.requestHandler(router)
            .listen(
                ar -> {
                    if(ar.succeeded()){
                        this.logger.info(
                            systemMessage
                                .getString("ongoing")
                                .replace("#IP_ADDRESS", this.parser.parseString(this.mainConfigs.getJsonObject("http-server").getString("address"),"127.0.0.1"))
                                .replace("#PORT", this.parser.parseString(this.mainConfigs.getJsonObject("http-server").getString("port"),"8181"))
                        );
                        promise.complete();
                    }else{
                        this.logger.error(ar.cause().getMessage());
                        promise.fail(ar.cause().getMessage());
                    }
            });
        
    }
    
    @Override
    public void stop(){
        this.server.close();
        this.vertx.close();
    }
}
