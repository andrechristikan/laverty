package com.andrechristikan.http.exception;

import com.andrechristikan.core.CoreException;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.LoggerFactory;

public class NotFoundException extends CoreException implements ExceptionInterface{

    public NotFoundException(Vertx vertx){
        super(vertx);
        logger = LoggerFactory.getLogger(LoginException.class);
    }

    @Override
    public final void handler(RoutingContext ctx){
        logger.info(trans("system.exception.not-found.start"));

        response.create(ctx.response());
        response.dataStructure("1", trans("response.exception.not-found"));
        response.response(ctx.response().getStatusCode());

        logger.info(trans("system.exception.not-found.start"));

    }

}
