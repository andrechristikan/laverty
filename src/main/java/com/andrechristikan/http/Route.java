/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.http;

import com.andrechristikan.http.middleware.AuthMiddleware;
import com.andrechristikan.http.controller.LoginController;
import com.andrechristikan.http.exception.LoginException;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 *
 * @author Syn-User
 */
public class Route {
 
    private final Router router;
    private final Vertx vertx;

    // Controller Init
    LoginController loginController;
    
    // Exception Init
    LoginException loginException;
    
    // Authorization Init
    AuthMiddleware loginAuthorization;
    
    protected Route(Vertx vertx, Router router){
        this.router = router;
        this.vertx = vertx;

        // Init
        this.initController();
        this.initException();
        this.initAuthorization();
    }

    // Router
    protected Router create(){
        
        // Before Auth
        this.router.post("/api/v1/login").handler(this.loginController::login);
        
        // Auth
        this.router.route("/api/v1/*").handler(this.loginAuthorization::handler).failureHandler(this.loginException::handler);
//        this.router.get("/api/v1/user/:id").handler(this.loginController::login);
        
        // After Auth
        
        return this.router;
    }
    
    // Init Controller
    private void initController(){
        this.loginController = new LoginController(this.vertx);

    }

    // Init Exception
    private void initException(){
        this.loginException = new LoginException(this.vertx);
    }
    
    // Init Authorization
    private void initAuthorization(){
        this.loginAuthorization = new AuthMiddleware(this.vertx);
    }
}
