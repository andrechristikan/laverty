/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Syn-User
 */
public class MainVerticle extends AbstractVerticle{
    
    final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    
    JsonObject systemMessages;
    JsonObject responseMessages;
    JsonObject routerConfigs;
    JsonObject mainConfigs;

    @Override
    public void start(Promise<Void> start) throws Exception {
        
        this.getConfig().setHandler(config -> {
            if(config.succeeded()){
                
                this.logger.info("System Messages : "+ this.systemMessages.toString());
                this.logger.info("Response Messages : "+ this.responseMessages.toString());
                this.logger.info("Reouter Configs : "+ this.routerConfigs.toString());
                this.logger.info("Main Configs : "+ this.mainConfigs.toString());
                
                JsonObject systemMessage = this.systemMessages.getJsonObject("main-verticle");
                this.logger.info(systemMessage.getString("start"));
                
                start.future().compose(st -> {
                     
                    //------------- Server
                    Promise <String> promise = Promise.promise();
                    this.vertx.deployVerticle(
                        new Server(), 
                        new DeploymentOptions().setConfig(
                            new JsonObject()
                                .put("main",this.mainConfigs)
                                .put("router",this.routerConfigs)
                        ),
                        promise
                    );
                    return promise.future();
                 }).setHandler(prom -> {
                    if (prom.succeeded()) {
                        this.logger.info(systemMessage.getString("success"));
                    }else{
                        this.logger.error(systemMessage.getString("fail")+" "+ prom.cause().toString());
                        start.fail(prom.cause());
                    }
                }).otherwise(otherWise -> {
                    this.logger.error(systemMessage.getString("otherwise")+" "+ otherWise.getCause().toString());
                    start.fail(otherWise);
                    this.vertx.close();
                    return otherWise.getMessage();
                });
                
                start.complete();
        
            }else{
                start.fail(config.cause().getMessage());
            }
        });
        
    }
    
    @Override
    public void stop(){
        this.vertx.close();
        
    }
    
    private Future <Void> getConfig(){
        
        Promise <Void> promise = Promise.promise();
        SharedData sharedData = this.vertx.sharedData();
        LocalMap<String, JsonObject> mapData = sharedData.getLocalMap("vertx");


        this.getMainConfig().setHandler(ar -> {
            if(ar.succeeded()){
                JsonObject config = ar.result();
                this.mainConfigs = config;
                mapData.put("configs.main", config);
                
                this.getResponseMessage(config.getString("language")).compose( messages -> {
                    this.responseMessages = messages;
                    mapData.put("messages.response", messages);
                    return this.getSystemMessage(config.getString("language"));
                }).compose( messages -> {
                    this.systemMessages = messages;
                    mapData.put("messages.system", messages);
                    return this.getRouterConfig();
                }).setHandler(messages -> {
                    if(messages.succeeded()){
                        this.routerConfigs = messages.result();
                        mapData.put("configs.router", messages.result());
                        promise.complete(null);
                    }else{
                        promise.fail(messages.cause().getMessage());
                    }
                });
            }else{
                promise.fail(ar.cause().getMessage());
            }
        });

        return promise.future() ;
    }
    
    private Future <JsonObject> getResponseMessage(String language){
        
        Promise <JsonObject> promise = Promise.promise();
        
        FileSystem vertxFileSystem = this.vertx.fileSystem();
        vertxFileSystem.readFile("resources/messages/"+language+"/response.json", readFile -> {
            if (readFile.succeeded()) {
                promise.complete(readFile.result().toJsonObject());
            }else{
                promise.fail(readFile.cause().getMessage());
            }
        });
            
       return promise.future();

    }
    
    private Future <JsonObject> getSystemMessage(String language){
        
        Promise <JsonObject> promise = Promise.promise();
        
        FileSystem vertxFileSystem = this.vertx.fileSystem();
        vertxFileSystem.readFile("resources/messages/"+language+"/system.json", readFile -> {
            if (readFile.succeeded()) {
                promise.complete(readFile.result().toJsonObject());
            }else{
                promise.fail(readFile.cause().getMessage());
            }
        });
    
       return promise.future();

    }
    
    private Future <JsonObject> getRouterConfig(){
        
        Promise <JsonObject> promise = Promise.promise();
        
        FileSystem vertxFileSystem = this.vertx.fileSystem();
        vertxFileSystem.readFile("resources/configs/router.json", readFile -> {
            if (readFile.succeeded()) {
                promise.complete(readFile.result().toJsonObject());
            }else{
                promise.fail(readFile.cause().getMessage());
            }
        });
    
       return promise.future();

    }
    
    private Future <JsonObject> getMainConfig() {
       
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
            .setType("file")
            .setConfig(
                    new JsonObject().put("path", "./configs/vertx.json")
            );
        ConfigStoreOptions sysPropsStore = new ConfigStoreOptions().setType("sys");

        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore)
                .addStore(sysPropsStore);

        ConfigRetriever retriever = ConfigRetriever.create(this.vertx, options);

        Future<JsonObject> future = ConfigRetriever.getConfigAsFuture(retriever);
        
        return future;
    }
  
    public static void main(String[] args) throws Exception {
        
        final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
        Version ver = new Version();
        logger.info(ver.getDescription());
        logger.info(ver.getVersion());
        
        Runner.runExample(MainVerticle.class);
    }
    
    
}
