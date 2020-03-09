package com.andrechristikan.services.implement;

import com.andrechristikan.core.CoreImplement;
import com.andrechristikan.helper.DatabaseHelper;
import com.andrechristikan.http.models.UserModel;
import com.andrechristikan.services.UserService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Transaction;
import java.util.ArrayList;
import org.slf4j.LoggerFactory;

public class UserServiceImplement extends CoreImplement implements UserService {

    public UserServiceImplement(Vertx vertx) {
        super(vertx);
        logger = LoggerFactory.getLogger(UserServiceImplement.class);
    }

    @Override
    public void setDatabaseConnection(){
        databaseHelper = new DatabaseHelper(coreVertx, "user");
        poolConnection = databaseHelper.createPool();
        logger.info(trans("system.service.start-pool-database")
                .replace("#className", AuthServiceImplement.class.getName())
                .replace("#serviceAddress", conf("service.user.address")));
    }

    @Override
    public void get(String id, Handler<AsyncResult<JsonObject>> resultHandler){
        databaseHelper.openConnection(poolConnection).setHandler(open -> {
            if(open.succeeded()){

                SqlConnection conn = open.result();
                Transaction trans = conn.begin();
                UserModel user = new UserModel(coreVertx, trans);
                
                ArrayList<String> columns = new ArrayList<>();
                columns.add("password_hash");
                columns.add("users.salt");
                user
                    .select("id")
                    .select("roles.id")
                    .selectRaw("(users.created_at)")
                    .selectRaw("roles.name as role_name")
                    .selectRaw("(roles.name) as role_name2")
                    .select("username")
                    .select(columns)
                    .selectRaw("email as user_email")
                    .selectRaw("users.last_login")
                    .join("roles","users.role_id","=","roles.id")
                .findOne(id).setHandler(select -> {
                    if(select.succeeded()) {
                        logger.info(trans("system.service.user.success-service")+user.firstAsString());
                        trans.commit();
                        resultHandler.handle(Future.succeededFuture(user.first()));
                    }else{
                        logger.error(trans("system.service.user.failed-service") +" "+select.cause().getMessage());
                        trans.rollback();
                        resultHandler.handle(Future.failedFuture(select.cause().getMessage()));
                    }
                });

            }else{
                logger.error(trans("system.service.user.failed-service") +" "+open.cause().getMessage());
                resultHandler.handle(Future.failedFuture(open.cause().getMessage()));
            }
        });
    }
    
    
    @Override
    public void list(Handler<AsyncResult<JsonArray>> resultHandler){
        databaseHelper.openConnection(poolConnection).setHandler(open -> {
            if(open.succeeded()){

                SqlConnection conn = open.result();
                Transaction trans = conn.begin();
                UserModel user = new UserModel(coreVertx, trans);
                
                ArrayList<String> columns = new ArrayList<>();
                columns.add("password_hash");
                columns.add("users.salt");
                user.select("id")
                    .select("users.role_id")
                    .selectRaw("(select name from roles where roles.id = users.role_id limit 1) AS role_name")
                    .select("username")
                    .select(columns)
                    .selectRaw("users.email AS email")
                    .select()
                    .where("username", "=", "user")
                .findAll().setHandler(select -> {
                    if(select.succeeded()) {
                        logger.info(trans("system.service.user.success-service")+user.getAsString());
                        trans.commit();
                        resultHandler.handle(Future.succeededFuture(user.get()));
                    }else{
                        logger.error(trans("system.service.user.failed-service") +" "+select.cause().getMessage());
                        trans.rollback();
                        resultHandler.handle(Future.failedFuture(select.cause().getMessage()));
                    }
                });

            }else{
                logger.error(trans("system.service.user.failed-service") +" "+open.cause().getMessage());
                resultHandler.handle(Future.failedFuture(open.cause().getMessage()));
            }
        });
    }


}
