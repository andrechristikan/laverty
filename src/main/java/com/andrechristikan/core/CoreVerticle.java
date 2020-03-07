package com.andrechristikan.core;

import com.andrechristikan.helper.GeneralHelper;
import com.andrechristikan.helper.ParserHelper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CoreVerticle extends AbstractVerticle {

    protected static Logger logger = LoggerFactory.getLogger(CoreVerticle.class);
    protected static ParserHelper parser = new ParserHelper();

    protected static JsonObject messages;
    protected static JsonObject configs;

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
