package com.andrechristikan.core;

import com.andrechristikan.helper.DatabaseHelper;
import com.andrechristikan.helper.GeneralHelper;
import com.andrechristikan.helper.ParserHelper;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreImplement {

    protected static Logger logger = LoggerFactory.getLogger(CoreImplement.class);
    protected static ParserHelper parser = new ParserHelper();
    protected static Vertx coreVertx;

    private static JsonObject messages;
    private static JsonObject configs;

    protected static PgPool poolConnection;
    protected static DatabaseHelper databaseHelper;

    public CoreImplement(Vertx vertx){
        coreVertx = vertx;

        messages = GeneralHelper.setMessages(vertx);
        configs = GeneralHelper.setConfigs(vertx);

        setDatabaseConnection();
    }

    protected void setDatabaseConnection(){ }

    protected static String trans(String path){
        return GeneralHelper.trans(path, messages);
    }

    protected static String conf(String path){
        return GeneralHelper.conf(path, configs);
    }

    protected static JsonArray confAsJsonArray(String path){
        return GeneralHelper.confAsJsonArray(path, configs);
    }

    protected static JsonObject confAsJsonObject(String path){
        return GeneralHelper.confAsJsonObject(path, configs);
    }
}
