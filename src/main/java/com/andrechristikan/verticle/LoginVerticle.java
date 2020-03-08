/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.verticle;

import com.andrechristikan.core.CoreVerticle;
import com.andrechristikan.services.AuthService;
import com.andrechristikan.services.implement.AuthServiceImplement;
import io.vertx.core.Promise;
import io.vertx.serviceproxy.ServiceBinder;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Syn-User
 */
public class LoginVerticle extends CoreVerticle {

    public LoginVerticle(){
        logger = LoggerFactory.getLogger(LoginVerticle.class);
    }

    @Override
    public void run(Promise<Void> promise) {

        logger.info(trans("system.service.start-verticle").replace("#className",LoginVerticle.class.getName()).replace("#eventBusServiceName", conf("service.auth.address")));
        ServiceBinder binder = new ServiceBinder(coreVertx);
        binder.setAddress(conf("service.auth.address")).register(AuthService.class, new AuthServiceImplement(coreVertx));
        logger.info(trans("system.service.end-verticle").replace("#className",LoginVerticle.class.getName()).replace("#eventBusServiceName", conf("service.auth.address")));

        promise.complete();
    }

    @Override
    public void end(){}

}
