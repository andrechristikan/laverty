/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.http;

import com.andrechristikan.http.controller.UserController;
import com.andrechristikan.http.middleware.AuthMiddleware;
import com.andrechristikan.http.controller.LoginController;
import com.andrechristikan.http.exception.AuthException;
import com.andrechristikan.http.middleware.RoleMiddleware;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 *
 * @author Syn-User
 */
public class Route {

    // Controller Init
    private static LoginController loginController;
    private static UserController userController;
    
    // Exception Init
    private static AuthException authException;
    
    // Authorization Init
    private static AuthMiddleware authMiddleware;
    private static RoleMiddleware roleMiddleware;
    
    protected Route(Vertx vertx){
        this.initController(vertx);
        this.initException(vertx);
        this.initAuthorization(vertx);
    }

    // Router
    protected void create(Router router){
        
        // Before Auth
        router.post("/api/v1/login").handler(loginController::login);
        
        // After Auth
        router.route("/api/v1/*").handler(authMiddleware::handler).failureHandler(authException::handler);

        router.get("/api/v1/test/:id").handler(userController::get);

        // If Auth Is Admin
        roleMiddleware.setRole("admin");
        router.route("/api/v1/user/*").handler(roleMiddleware::handler);

        router.get("/api/v1/user/:id").handler(userController::get);

    }

    private void initController(Vertx vertx){
        loginController = new LoginController(vertx);
        userController = new UserController(vertx);

    }

    private void initException(Vertx vertx){
        authException = new AuthException(vertx);
    }

    private void initAuthorization(Vertx vertx){
        authMiddleware = new AuthMiddleware(vertx);
        roleMiddleware = new RoleMiddleware(vertx);
    }
}
