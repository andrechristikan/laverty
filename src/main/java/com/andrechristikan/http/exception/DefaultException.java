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
        response = new Response(vertx);
        service = "default";
        
    }

    @Override
    public final void handler(RoutingContext ctx){
        logger.info(systemMessage("start"));
        
        response.create(ctx.response());
        response.dataStructure(1, responseMessage(service));
        response.response(ctx.response().getStatusCode());

        logger.info(systemMessage("end"));

    }

}
