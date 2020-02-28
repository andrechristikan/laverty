/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan;

import com.andrechristikan.controller.LoginController;
import com.andrechristikan.services.LoginService;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

/**
 *
 * @author Syn-User
 */
public class Route {
 
    final Router router;
    final Vertx vertx;
    
    // Controller Init
    LoginController loginController;
    
    Route(Vertx vertx, Router router){
        this.router = router;
        this.vertx = vertx;
        
        this.createController();
    }
    
    protected Router create(){
        
        this.router.get("/api/v1/login").handler(this.loginController::login);
        
        return this.router;
    }
    
    // Create proxy
    private void createController(){
        
        this.loginController = new LoginController(this.vertx);
        
    }
    
}
