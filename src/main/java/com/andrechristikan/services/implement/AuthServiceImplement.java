/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.services.implement;

import com.andrechristikan.core.CoreImplement;
import com.andrechristikan.helper.DatabaseHelper;
import com.andrechristikan.helper.JwtHelper;
import com.andrechristikan.helper.PasswordHelper;
import com.andrechristikan.http.models.UserModel;
import com.andrechristikan.services.AuthService;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Transaction;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 *
 * @author Syn-User
 */
public class AuthServiceImplement extends CoreImplement implements AuthService {

    private final JwtHelper jwtHelper;

    public AuthServiceImplement(Vertx vertx) {
        super(vertx);
        logger = LoggerFactory.getLogger(AuthServiceImplement.class);

        this.jwtHelper = new JwtHelper(vertx);
    }

    @Override
    public void setDatabaseConnection(){
        databaseHelper = new DatabaseHelper(coreVertx, "auth");
        poolConnection = databaseHelper.createPool();
        logger.info(trans("system.service.start-pool-database")
                .replace("#className", AuthServiceImplement.class.getName())
                .replace("#serviceAddress", conf("service.auth.address")));
    }

    @Override
    public void login(String loginString, String passwordString, Handler<AsyncResult<JsonObject>> resultHandler){

        databaseHelper.openConnection(poolConnection).setHandler(open -> {
            if(open.succeeded()){

                SqlConnection conn = open.result();
                Transaction trans = conn.begin();
                UserModel user = new UserModel(coreVertx, trans);

                user.whereRaw("lower(username)","like","%"+loginString.toLowerCase()+"%")
                    .orWhereRaw("lower(email)","like","%"+loginString.toLowerCase()+"%")
                    .findOne()
                .setHandler(select_user -> {
                    if(select_user.succeeded()){
                        JsonObject data = user.first();
                        PasswordHelper ph = new PasswordHelper();

                        try {
                            // Check password
                            if (!ph.validatePassword(passwordString, data.getString("password_hash"), data.getString("salt"))) {
                                trans.rollback();
                                resultHandler.handle(Future.failedFuture(trans("response.service.auth.login.credential-not-match")));
                            }else{
                                OffsetDateTime lastLogin = OffsetDateTime.now(ZoneOffset.UTC);
                                user.columnsValue.replace("last_login",lastLogin.toString());
                                user.update().setHandler(update_user -> {
                                    if(update_user.succeeded()){
                                        data.remove("password_hash");
                                        data.remove("salt");

                                        JsonObject tokenJwt = this.jwtHelper.getTokenJwt(data.getString("role_id"), data);
                                        logger.info(trans("system.service.auth.success-service")+tokenJwt);

                                        trans.commit();
                                        resultHandler.handle(Future.succeededFuture(tokenJwt));

                                    }else{
                                        trans.rollback();
                                        logger.error(trans("system.service.auth.failed-service") +" "+update_user.cause().getMessage());
                                        resultHandler.handle(Future.failedFuture(update_user.cause().getMessage()));
                                    }

                                    trans.close();
                                    conn.close();
                                });
                            }
                        }catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
                            logger.error(trans("system.service.auth.failed-service") +" "+ex.getMessage());
                            trans.rollback();
                            resultHandler.handle(Future.failedFuture(ex.getMessage()));
                        }
                    }else{
                        trans.rollback();
                        logger.error(trans("system.service.auth.failed-service") +" "+select_user.cause().getMessage());
                        resultHandler.handle(Future.failedFuture(select_user.cause().getMessage()));
                    }
                });
            }else{
                logger.error(trans("system.service.auth.failed-service") +" "+open.cause().getMessage());
                resultHandler.handle(Future.failedFuture(open.cause().getMessage()));
            }
        });
    }

    @Override
    public void logout(Handler<AsyncResult<String>> resultHandler){

    }

}
