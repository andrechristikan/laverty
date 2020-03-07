/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.http.exception;

import io.vertx.ext.web.RoutingContext;

/**
 *
 * @author Syn-User
 */
public interface ExceptionInterface {
    
    void handler(RoutingContext ctx);
    
}
