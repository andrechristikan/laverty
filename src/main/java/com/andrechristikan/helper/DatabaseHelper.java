package com.andrechristikan.helper;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseHelper {

    private final Logger logger;
    private final Vertx vertx;
    private final String service;

    protected JsonObject systemMessages;
    protected JsonObject mainConfigs;
    protected JsonObject serviceConfigs;
    protected JsonObject dbConfig;

    public DatabaseHelper(Vertx vertx, String serviceName){
        this.logger = LoggerFactory.getLogger(DatabaseHelper.class);
        this.vertx = vertx;
        this.service = "database";

        // Message
        this.setConfigs(serviceName);
        this.setMessages();
  
        // Db Config
        this.dbConfig = this.mainConfigs.getJsonObject(
            this.mainConfigs.getString("environment")
        ).getJsonObject(this.service).getJsonObject(
            this.serviceConfigs.getString("database-usage")
        );
    }

    private void setConfigs(String serviceName){
        SharedData sharedData = this.vertx.sharedData();
        LocalMap<String, JsonObject> jMapData = sharedData.getLocalMap("vertx");
        this.mainConfigs = jMapData.get("configs.main");
        this.serviceConfigs = jMapData.get("configs.service").getJsonObject(serviceName);
    }

    private void setMessages(){
        SharedData sharedData = this.vertx.sharedData();
        LocalMap<String, JsonObject> jMapData = sharedData.getLocalMap("vertx");
        this.systemMessages = jMapData.get("messages.system").getJsonObject(this.service);
    }

    public PgPool createPool(){
        PgConnectOptions dbOptions = new PgConnectOptions()
                .setHost(this.dbConfig.getString("host"))
                .setPort(Integer.parseInt(this.dbConfig.getString("port")))
                .setDatabase(this.dbConfig.getString("name"))
                .setUser(this.dbConfig.getString("user"))
                .setPassword(this.dbConfig.getString("password"));

        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(this.dbConfig.getInteger("poolMaxSize", 10));
        PgPool dbClient = PgPool.pool(this.vertx, dbOptions, poolOptions);

        this.logger.info(this.systemMessages.getJsonObject("create-pool").getString("success"));

        return dbClient;

    }

    public void destroyPool(PgPool pool){
        pool.close();
        this.logger.info(this.systemMessages.getJsonObject("destroy-pool").getString("success"));
    }

    public Future<SqlConnection> openConnection(PgPool pool){

        Promise <SqlConnection> promise = Promise.promise();
        pool.getConnection(ar -> {
            if (ar.succeeded()){
                this.logger.info(this.systemMessages.getJsonObject("open-connection").getString("success"));
                promise.complete(ar.result());
            }else{
                this.logger.info(ar.cause().getMessage());
                promise.fail(ar.cause().getMessage());
            }
        });

        return promise.future();
    };

    public void closeConnection(SqlConnection conn){
        conn.close();
        this.logger.info(this.systemMessages.getJsonObject("close-connection").getString("success"));
    };
}
