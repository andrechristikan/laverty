package com.andrechristikan.http.controller;

import com.andrechristikan.core.CoreController;
import com.andrechristikan.services.LoginService;
import com.andrechristikan.services.UserService;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.LoggerFactory;

public class UserController extends CoreController implements ControllerInterface{

    protected static UserService service;

    public UserController(Vertx vertx){
        super(vertx);
        logger = LoggerFactory.getLogger(LoginController.class);
    }

    @Override
    public void setService(){
        logger.info(trans("system.service.user.controller.create").replace("#eventBusServiceName", conf("service.user.address")));
        service = UserService.createProxy(coreVertx,conf("service.user.address"));
        logger.info(trans("system.service.user.controller.end").replace("#eventBusServiceName", conf("service.user.address")));
    }

    public void get(RoutingContext ctx) {

        logger.info(trans("system.service.user.controller.get.start"));
        response.create(ctx.response());

        service.get(ctx.request().getParam("id"),funct -> {
            if(funct.succeeded()){
                response.dataStructure("0", trans("response.service.user.controller.success"), funct.result());
                response.response(200);
            }else{
                response.dataStructure("1", funct.cause().getMessage());
                response.response(500);
            }
        });

        logger.info(trans("system.service.user.controller.get.end"));
    }

}
