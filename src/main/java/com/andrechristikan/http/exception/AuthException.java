/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.http.exception;

import com.andrechristikan.core.CoreException;
import com.andrechristikan.helper.JwtHelper;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Syn-User
 */
public class AuthException extends CoreException implements ExceptionInterface{
    
    public AuthException(Vertx vertx){
        super(vertx);
        logger = LoggerFactory.getLogger(AuthException.class);
    }
    
    @Override
    public final void handler(RoutingContext ctx){

        logger.info(trans("system.exception.login.start"));
    
        String authorization = JwtHelper.getTokenFromHeader(ctx);
        
        if(authorization == null || authorization.trim().equals("")){
            response.create(ctx.response());
            response.dataStructure("1", trans("response.exception.login"));
            response.response(ctx.response().getStatusCode());

            logger.info(trans("system.exception.login.fail") + " " +trans("response.exception.login"));

        }else{
            ctx.next();
        }

        logger.info(trans("system.exception.login.end"));

    }
}
