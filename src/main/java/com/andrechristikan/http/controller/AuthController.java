/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.http.controller;

import com.andrechristikan.core.CoreController;
import com.andrechristikan.services.AuthService;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Syn-User
 */
public class AuthController extends CoreController implements ControllerInterface {

    protected static AuthService service;
    
    public AuthController(Vertx vertx){
        super(vertx);
        logger = LoggerFactory.getLogger(AuthController.class);
    }

    @Override
    public void setService(){
        logger.info(trans("system.service.start-proxy")
                .replace("#className", AuthController.class.getName())
                .replace("#eventBusServiceName", conf("service.auth.address")));
        service = AuthService.createProxy(coreVertx,conf("service.auth.address"));
        logger.info(trans("system.service.end-proxy")
                .replace("#className", AuthController.class.getName())
                .replace("#eventBusServiceName", conf("service.auth.address")));
    }

    public void login(RoutingContext ctx) {

        logger.info(trans("system.service.auth.start-controller"));
        response.create(ctx.response());

        service.login(ctx.request().getFormAttribute("login"),ctx.request().getFormAttribute("password"),funct -> {
            if(funct.succeeded()){
                response.dataStructure("0", trans("response.service.auth.login.success"), funct.result());
                response.response(200);
            }else{
                response.dataStructure("1", funct.cause().getMessage());
                response.response(500);
            }
        });

        logger.info(trans("system.service.auth.end-controller"));

    }
    
}
