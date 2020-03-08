/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.http;

import com.andrechristikan.Runner;
import com.andrechristikan.Version;
import com.andrechristikan.helper.GeneralHelper;
import com.andrechristikan.verticle.LoginVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonArray;
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

    private final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    private JsonObject messages = new JsonObject();
    private JsonObject configs = new JsonObject();

    @Override
    public void start(Promise<Void> start) throws Exception {

        this.getConfig().setHandler(config -> {
            if(config.succeeded()){

                this.logger.info("Configs : "+ this.configs.toString());
                this.logger.info("Messages : "+ this.messages.toString());

                this.logger.info(trans("system.service.main.start"));

                // DEPLOY VERTICLE
                start.future().compose(st -> {

                    // -- Login
                    Promise <String> promise = Promise.promise();
                    this.vertx.deployVerticle(
                        new LoginVerticle(),
                        new DeploymentOptions().setConfig(
                            this.configs
                        ),
                        promise
                    );
                    return promise.future();

                }).compose(st -> {

                    // -- User
                    Promise <String> promise = Promise.promise();
                    promise.complete();
                    return promise.future();
                }).compose(st -> {

                    // -- SERVER
                    Promise <String> promise = Promise.promise();
                    this.vertx.deployVerticle(
                        new Server(),
                        new DeploymentOptions().setConfig(
                            this.configs
                        ),
                        promise
                    );
                    return promise.future();
                }).setHandler(prom -> {
                    if (prom.succeeded()) {
                        this.logger.info(trans("system.service.main.success"));
                    }else{
                        this.logger.error(trans("system.service.main.failed")+" "+ prom.cause().getMessage());
                        start.fail(prom.cause().getMessage());
                    }
                }).otherwise(otherWise -> {
                    this.logger.error(trans("system.service.main.otherwise")+" "+ otherWise.getCause().getMessage());
                    start.fail(otherWise.getCause().getMessage());
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
        LocalMap<String, JsonObject> jMapData = sharedData.getLocalMap("vertx");

        this.getMainConfig().setHandler(ar -> {
            if(ar.succeeded()){
                JsonObject config = ar.result();
                this.configs.put("main", config);
                jMapData.put("configs.main", config);

                this.getResponseMessage(config.getString("language")).compose( messages -> {
                    this.messages.put("response", messages);
                    jMapData.put("messages.response", messages);
                    return this.getSystemMessage(config.getString("language"));
                }).compose( messages -> {
                    this.messages.put("system", messages);
                    jMapData.put("messages.system", messages);
                    return this.getServiceConfig();
                }).setHandler( messages -> {
                    if(messages.succeeded()){
                        this.configs.put("service", messages.result());
                        jMapData.put("configs.service", messages.result());
                        promise.complete();
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
        vertxFileSystem.readFile("messages/"+language+"/response.json", readFile -> {
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
        vertxFileSystem.readFile("messages/"+language+"/system.json", readFile -> {
            if (readFile.succeeded()) {
                promise.complete(readFile.result().toJsonObject());
            }else{
                promise.fail(readFile.cause().getMessage());
            }
        });

       return promise.future();

    }

    private Future <JsonObject> getServiceConfig(){

        Promise <JsonObject> promise = Promise.promise();

        FileSystem vertxFileSystem = this.vertx.fileSystem();
        vertxFileSystem.readFile("configs/services.json", readFile -> {
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
                    new JsonObject().put("path", "configs/vertx.json")
            );
        ConfigStoreOptions sysPropsStore = new ConfigStoreOptions().setType("sys");

        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore)
                .addStore(sysPropsStore);

        ConfigRetriever retriever = ConfigRetriever.create(this.vertx, options);

        return ConfigRetriever.getConfigAsFuture(retriever);
    }

    protected String trans(String path){
        return GeneralHelper.trans(path, this.messages);
    }

    protected String conf(String path){
        return GeneralHelper.conf(path, this.configs);
    }

    protected JsonArray confAsJsonArray(String path){
        return GeneralHelper.confAsJsonArray(path, this.configs);
    }

    protected JsonObject confAsJsonObject(String path){
        return GeneralHelper.confAsJsonObject(path, this.configs);
    }

    public static void main(String[] args) throws Exception {
        
        final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
        Version ver = new Version();
        logger.info(ver.getDescription());
        logger.info(ver.getVersion());
        
        Runner.runExample(MainVerticle.class);

    }
    
    
}
