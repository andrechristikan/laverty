package com.andrechristikan.http.exception;

import com.andrechristikan.core.CoreException;
import com.andrechristikan.http.Response;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.LoggerFactory;

public class DefaultException extends CoreException implements ExceptionInterface{
    
    public DefaultException(Vertx vertx){
        super(vertx);
        logger = LoggerFactory.getLogger(DefaultException.class);
    }

    @Override
    public final void handler(RoutingContext ctx){
        logger.info(trans("system.exception.default.start"));
        
        response.create(ctx.response());
        response.dataStructure("1", trans("response.exception.default"));
        response.response(ctx.response().getStatusCode());

        logger.info(trans("system.exception.default.end"));

    }

}
