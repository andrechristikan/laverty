/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.services;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import com.andrechristikan.services.implement.LoginServiceImplement;
import io.vertx.core.json.JsonObject;

/**
 *
 * @author Syn-User
 */
@ProxyGen
public interface LoginService {
    
    static LoginService create(Vertx vertx) {
        return new LoginServiceImplement(vertx);
    }

    static LoginService createProxy(Vertx vertx, String address) {
        return new LoginServiceVertxEBProxy(vertx, address);
    }

    void setDatabaseConnection();

    void login(String loginString, String passwordString, Handler<AsyncResult<JsonObject>> resultHandler);

    void logout(Handler<AsyncResult<String>> resultHandler);

}
