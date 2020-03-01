/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan;

import com.andrechristikan.controller.LoginController;
import com.andrechristikan.exception.DefaultException;
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
    DefaultException defaultException;
    
    Route(Vertx vertx, Router router){
        this.router = router;
        this.vertx = vertx;

        // Init
        this.initController();
        this.initException();
    }

    // Router
    protected Router create(){

        this.router.get("/api/v1/login").handler(this.loginController::login).failureHandler(this.defaultException::Handler);
        
        return this.router;
    }
    
    // Init Controller
    private void initController(){
        this.loginController = new LoginController(this.vertx);

    }

    // Init Exception
    private void initException(){
        this.defaultException = new DefaultException(this.vertx);
    }
}
