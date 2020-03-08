package com.andrechristikan.helper;

import com.andrechristikan.core.CoreHelper;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnection;
import org.slf4j.LoggerFactory;

public class DatabaseHelper extends CoreHelper {

    private JsonObject dbConfig;

    public DatabaseHelper(Vertx vertx, String database){
        super(vertx);
        logger = LoggerFactory.getLogger(DatabaseHelper.class);

        this.dbConfig = confAsJsonObject("main."+conf("main.environment")+".database."+conf("service."+database+".database-usage"));
    }

    public PgPool createPool(){
        PgConnectOptions dbOptions = new PgConnectOptions()
                .setHost(this.dbConfig.getString("host"))
                .setPort(Integer.parseInt(this.dbConfig.getString("port")))
                .setDatabase(this.dbConfig.getString("name"))
                .setUser(this.dbConfig.getString("user"))
                .setPassword(this.dbConfig.getString("password"));

        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(this.dbConfig.getInteger("poolMaxSize", 10));
        PgPool dbClient = PgPool.pool(coreVertx, dbOptions, poolOptions);

        logger.info(trans("system.database.create-pool.success"));

        return dbClient;

    }

    public void destroyPool(PgPool pool){
        pool.close();
        logger.info(trans("system.database.destroy-pool.success"));
    }

    public Future<SqlConnection> openConnection(PgPool pool){

        Promise <SqlConnection> promise = Promise.promise();
        pool.getConnection(ar -> {
            if (ar.succeeded()){
                logger.info(trans("system.database.open-connection.success"));
                promise.complete(ar.result());
            }else{
                logger.info(ar.cause().getMessage());
                promise.fail(ar.cause().getMessage());
            }
        });

        return promise.future();
    };

    public void closeConnection(SqlConnection conn){
        conn.close();
        logger.info(trans("system.database.close-connection.success"));
    };
}
