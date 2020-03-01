package com.andrechristikan.exception;

import com.andrechristikan.Response;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultException {

    private final Vertx vertx;
    private final Response response;
    private final Logger logger;
    private final String service;

    protected JsonObject systemMessages;

    public DefaultException(Vertx vertx){
        this.vertx = vertx;
        this.response = new Response(vertx);
        this.logger = LoggerFactory.getLogger(DefaultException.class);
        this.service = "exception";

        // Set Message
        this.setMessages();
    }

    private void setMessages(){
        SharedData sharedData = this.vertx.sharedData();
        LocalMap<String, JsonObject> jMapData = sharedData.getLocalMap("vertx");
        this.systemMessages = jMapData.get("messages.system").getJsonObject(this.service);
    }

    public final void Handler(RoutingContext ctx){
        this.logger.info(this.systemMessages.getJsonObject("default").getString("start"));

        HttpServerResponse response = this.response.create(ctx);
        String responseData = Response.DataStructure(1, ctx.failure().getMessage());

        this.logger.info(this.systemMessages.getJsonObject("default").getString("end"));

        response.setStatusCode(ctx.response().getStatusCode());
        response.end(responseData);

    }

}
