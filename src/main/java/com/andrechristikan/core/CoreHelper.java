package com.andrechristikan.core;

import com.andrechristikan.helper.GeneralHelper;
import com.andrechristikan.helper.ParserHelper;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CoreHelper {

    protected static Logger logger = LoggerFactory.getLogger(CoreHelper.class);
    protected static ParserHelper parser = new ParserHelper();
    protected static Vertx coreVertx;

    private static JsonObject messages;
    private static JsonObject configs;

    public CoreHelper(Vertx vertx){
        coreVertx = vertx;
        messages = GeneralHelper.setMessages(vertx);
        configs = GeneralHelper.setConfigs(vertx);

    }

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
