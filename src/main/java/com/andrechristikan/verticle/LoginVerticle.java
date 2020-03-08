/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.verticle;

import com.andrechristikan.core.CoreVerticle;
import com.andrechristikan.helper.GeneralHelper;
import com.andrechristikan.helper.ParserHelper;
import com.andrechristikan.services.LoginService;
import com.andrechristikan.services.implement.LoginServiceImplement;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.serviceproxy.ServiceBinder;
import org.slf4j.Logger;
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

        logger.info(trans("system.service.login.start").replace("#eventBusServiceName", conf("service.login.address")));
        ServiceBinder binder = new ServiceBinder(coreVertx);
        binder.setAddress(conf("service.login.address")).register(LoginService.class, new LoginServiceImplement(coreVertx));
        logger.info(trans("system.service.login.end").replace("#eventBusServiceName", conf("service.login.address")));

        promise.complete();
    }

    @Override
    public void end(){}

}
