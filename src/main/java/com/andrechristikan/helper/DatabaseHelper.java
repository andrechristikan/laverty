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
    private final String eventBusService;

    protected JsonObject systemMessages;
    protected JsonObject responseMessages;
    protected JsonObject mainConfigs;
    protected JsonObject eventBusServiceConfigs;
    protected JsonObject dbConfig;

    public DatabaseHelper(Vertx vertx, String eventBusServiceName){
        this.logger = LoggerFactory.getLogger(DatabaseHelper.class);
        this.vertx = vertx;
        this.eventBusService = "database";

        // Message
        this.setConfigs();
        this.setMessages();

        // Db Config
        this.dbConfig = this.mainConfigs.getJsonObject(this.eventBusServiceConfigs.getJsonObject(eventBusServiceName).getString("database-usage"));
    }

    private void setConfigs(){
        SharedData sharedData = this.vertx.sharedData();
        LocalMap<String, JsonObject> jMapData = sharedData.getLocalMap("vertx");
        this.mainConfigs = jMapData.get("configs.main");
        this.eventBusServiceConfigs = jMapData.get("configs.service").getJsonObject(this.eventBusService);
    }

    private void setMessages(){
        SharedData sharedData = this.vertx.sharedData();
        LocalMap<String, JsonObject> jMapData = sharedData.getLocalMap("vertx");
        this.systemMessages = jMapData.get("messages.system").getJsonObject(this.eventBusService);
        this.responseMessages = jMapData.get("messages.response").getJsonObject(this.eventBusService);
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

        this.logger.info(this.systemMessages.getJsonObject("service").getJsonObject("create-pool").getString("success"));

        return dbClient;

    }

    public void destroyPool(PgPool pool){
        pool.close();
        this.logger.info(this.systemMessages.getJsonObject("service").getJsonObject("destroy-pool").getString("success"));
    }

    public Future<SqlConnection> openConnection(PgPool pool){

        Promise <SqlConnection> promise = Promise.promise();
        pool.getConnection(ar -> {
            if (ar.succeeded()){
                this.logger.info(this.systemMessages.getJsonObject("service").getJsonObject("open-connection").getString("success"));
                promise.complete(ar.result());
            }else{
                String message = this.systemMessages.getJsonObject("service").getJsonObject("open-connection").getString("fail");
                this.logger.info(message);
                promise.fail(message);
            }
        });

        return promise.future();
    };

    public void closeConnection(SqlConnection conn){
        conn.close();
        this.logger.info(this.systemMessages.getJsonObject("service").getJsonObject("close-connection").getString("success"));
    };
}
