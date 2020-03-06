package com.andrechristikan.http.exception;

import com.andrechristikan.core.CoreException;
import com.andrechristikan.http.Response;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.LoggerFactory;

public class NotFoundException extends CoreException implements ExceptionInterface{

    public NotFoundException(Vertx vertx){
        super(vertx);
        
        // Set the main variable
        logger = LoggerFactory.getLogger(LoginException.class);
        response = new Response(vertx);
        service = "not-found";
    }

    @Override
    public final void handler(RoutingContext ctx){
        logger.info(systemMessage("start"));

        response.create(ctx.response());
        response.dataStructure(1, responseMessage("not-found"));
        response.response(ctx.response().getStatusCode());
        
        logger.info(systemMessage("end"));

    }

}
