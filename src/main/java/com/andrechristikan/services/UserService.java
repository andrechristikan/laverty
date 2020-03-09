package com.andrechristikan.services;

import com.andrechristikan.services.implement.UserServiceImplement;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

@ProxyGen
public interface UserService {

    static UserService create(Vertx vertx) {
        return new UserServiceImplement(vertx);
    }

    static UserService createProxy(Vertx vertx, String address) {
        return new UserServiceVertxEBProxy(vertx, address);
    }

    void setDatabaseConnection();

    void get(String id, Handler<AsyncResult<JsonObject>> resultHandler);
    
    void list(Handler<AsyncResult<JsonArray>> resultHandler);

}
