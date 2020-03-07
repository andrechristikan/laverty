package com.andrechristikan.core;

import com.andrechristikan.helper.GeneralHelper;
import com.andrechristikan.helper.ParserHelper;
import com.andrechristikan.http.Response;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreController {

    protected static Logger logger = LoggerFactory.getLogger(CoreController.class);
    protected static ParserHelper parser = new ParserHelper();

    protected static Response response;
    protected static Vertx coreVertx;

    private static JsonObject messages;
    private static JsonObject configs;

    public CoreController(Vertx vertx){
        coreVertx = vertx;
        messages = GeneralHelper.setMessages(vertx);
        configs = GeneralHelper.setConfigs(vertx);
        response = new Response(vertx);

        this.setService();
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

    protected void setService(){}

    protected void login(RoutingContext ctx) {}

}
