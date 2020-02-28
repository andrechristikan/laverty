/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.services.impl;

import com.andrechristikan.services.LoginService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Syn-User
 */
public class LoginServiceImpl implements LoginService {
    
    private final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);
    
    public LoginServiceImpl() {
        this.logger.info("masuk login");
    }
}
