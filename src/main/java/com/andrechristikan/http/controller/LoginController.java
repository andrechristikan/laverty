/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.http.controller;

import com.andrechristikan.core.CoreController;
import com.andrechristikan.http.Response;
import com.andrechristikan.helper.ParserHelper;
import com.andrechristikan.services.LoginService;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Syn-User
 */
public class LoginController extends CoreController implements ControllerInterface {

    protected static LoginService service;
    
    public LoginController(Vertx vertx){
        super(vertx);
        logger = LoggerFactory.getLogger(LoginController.class);
    }

    @Override
    public void setService(){
        logger.info(trans("system.service.login.controller.create").replace("#eventBusServiceName", conf("service.login.address")));
        service = LoginService.createProxy(coreVertx,conf("service.login.address"));
        logger.info(trans("system.service.login.controller.end").replace("#eventBusServiceName", conf("service.login.address")));
    }

    @Override
    public void login(RoutingContext ctx) {
        logger.info(trans("system.service.login.controller.login.start"));
        response.create(ctx.response());

        service.login(funct -> {
            if(funct.succeeded()){
                response.response(200, funct.result());
            }else{
                response.response(500, funct.cause().getMessage());
            }
        });

        logger.info(trans("system.service.login.controller.login.end"));

}
    
}
