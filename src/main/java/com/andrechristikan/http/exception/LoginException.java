/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.http.exception;

import com.andrechristikan.core.CoreException;
import com.andrechristikan.helper.RequestHelper;
import com.andrechristikan.http.Response;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Syn-User
 */
public class LoginException extends CoreException implements ExceptionInterface{
    
    public LoginException(Vertx vertx){
        super(vertx);
        
        // Set the main variable
        logger = LoggerFactory.getLogger(LoginException.class);
        response = new Response(vertx);
        service = "login";
    }
    
    @Override
    public final void handler(RoutingContext ctx){
        
        logger.info(systemMessage("start"));
    
        String authorization = RequestHelper.getAuthorization(ctx);
        
        if(authorization == null || authorization.trim().equals("")){
            response.create(ctx.response());
            response.dataStructure(1, responseMessage(service));
            response.response(ctx.response().getStatusCode());

            logger.info(systemMessage("fail") + " " +responseMessage(service));

        }else{
            ctx.next();
        }

        logger.info(systemMessage("end"));

    }
}
