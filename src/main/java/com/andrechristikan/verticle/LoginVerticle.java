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
public class LoginVerticle extends CoreVerticle implements  VerticleInterface{

    public LoginVerticle(){
        logger = LoggerFactory.getLogger(LoginVerticle.class);
    }

    @Override
    public void start(Promise<Void> promise) throws Exception {

        messages = GeneralHelper.setMessages(vertx);
        configs = GeneralHelper.setConfigs(vertx);

        logger.info(trans("system.service.login.start").replace("#eventBusServiceName", conf("service.login.address")));
        ServiceBinder binder = new ServiceBinder(this.vertx);
        binder.setAddress(conf("service.login.address")).register(LoginService.class, new LoginServiceImplement(this.vertx));
        logger.info(trans("system.service.login.end").replace("#eventBusServiceName", conf("service.login.address")));

        promise.complete();
    }

}
