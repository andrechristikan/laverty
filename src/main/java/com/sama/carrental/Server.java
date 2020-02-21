/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sama.carrental;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Syn-User
 */
public class Server extends AbstractVerticle {
    
    Logger logger = LoggerFactory.getLogger(Server.class);
    
    @Override
    public void start(final Promise<Void> promise) throws Exception {
        
        this.logger.info("aaa");
                
        Router router = Router.router(this.vertx);
        router.get("/").handler(hndlr -> {
            
            HttpServerResponse response = hndlr.response();
            response.setStatusCode(200);
            response.end("success");
        });
        this.logger.info("bbbb");
        
        // No SSL requested, start a non-SSL HTTP server.
        vertx.createHttpServer()    
            .requestHandler(router)
            .listen(8082, "127.0.0.1", ar -> {
                this.logger.info("successs");
            });
        
    }
}
