package com.andrechristikan.verticle;

import com.andrechristikan.core.CoreVerticle;
import com.andrechristikan.services.LoginService;
import com.andrechristikan.services.UserService;
import com.andrechristikan.services.implement.LoginServiceImplement;
import com.andrechristikan.services.implement.UserServiceImplement;
import io.vertx.core.Promise;
import io.vertx.serviceproxy.ServiceBinder;
import org.slf4j.LoggerFactory;

public class UserVerticle extends CoreVerticle {


    public UserVerticle(){
        logger = LoggerFactory.getLogger(UserVerticle.class);
    }

    @Override
    public void run(Promise<Void> promise) {

        logger.info(trans("system.service.user.start").replace("#eventBusServiceName", conf("service.user.address")));
        ServiceBinder binder = new ServiceBinder(coreVertx);
        binder.setAddress(conf("service.user.address")).register(UserService.class, new UserServiceImplement(coreVertx));
        logger.info(trans("system.service.user.end").replace("#eventBusServiceName", conf("service.user.address")));

        promise.complete();
    }

    @Override
    public void end(){}

}
