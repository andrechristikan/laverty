/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.http.middleware;

import com.andrechristikan.core.CoreMiddleware;
import com.andrechristikan.helper.JwtHelper;
import com.andrechristikan.http.Response;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Syn-User
 */
public class AuthMiddleware extends CoreMiddleware{

    private final JwtHelper jwtHelper;
    
    public AuthMiddleware(Vertx vertx){
        super(vertx);
        logger = LoggerFactory.getLogger(AuthMiddleware.class);
        response = new Response(vertx);

        this.jwtHelper = new JwtHelper(vertx);

    }

    @Override
    public void handler(RoutingContext ctx){

        String authorization = JwtHelper.getTokenFromHeader(ctx);
        JWTAuth jwtAuthConfig = this.jwtHelper.getJwtAuth();
        response.create(ctx.response());

        if (authorization != null) {
            String[] parts = authorization.split(" ");
            String token = parts[1];

            jwtAuthConfig.authenticate(new JsonObject().put("jwt", token), checked -> {
                if (checked.succeeded()) {
                    logger.info(trans("system.middleware.auth.success"));
                    if(ctx.user() == null){
                        ctx.setUser(checked.result());
                    }
                    ctx.next();
                }else{
                    logger.info(trans("system.middleware.auth.failed")+" "+checked.cause().getMessage());
                    response.dataStructure("1", trans("response.middleware.auth.invalid-token"));
                    response.response(401);
                }
            });
        }else{
            logger.info(trans("system.middleware.auth.failed")+" "+trans("response.middleware.auth.token-required"));
            response.dataStructure("1", trans("response.middleware.auth.token-required"));
            response.response(403);
        }
                    
    }
}
