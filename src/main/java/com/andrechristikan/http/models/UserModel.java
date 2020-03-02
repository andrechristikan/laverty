package com.andrechristikan.http.models;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Transaction;
import io.vertx.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class UserModel {

    // Customizable
    private final String [] columns = {"id","role_id","username","password","email","created_at","updated_at","last_login"};
    private final String tableName = "users";
    private final String service = "user";

    private final Logger logger;
    private Transaction trans;
    private SqlConnection conn;
    private JsonObject value = new JsonObject();
    private final Map<String, String> columnsName = new HashMap<>();
    private final Map<String, String> columnsType = new HashMap<>();

    private String selectQuery;
    private ArrayList <String> selectQueryArray = new ArrayList<String>();

    private JsonObject responseMessages;
    private final Vertx vertx;

    public UserModel(Vertx vertx, Transaction trans){
        this.logger = LoggerFactory.getLogger(UserModel.class);
        this.vertx = vertx;
        this.trans = trans;
        this.init();
    }

    public UserModel(Vertx vertx, SqlConnection conn){
        this.logger = LoggerFactory.getLogger(UserModel.class);
        this.vertx = vertx;
        this.conn = conn;
        this.init();
    }

    private void init(){
        this.setColumnsName();
        this.setMessages();
        this.setColumnsType();
    }

    // Customizable
    private void setColumnsName(){
        this.columnsName.put("id","id");
        this.columnsName.put("role_id","role_id");
        this.columnsName.put("username","username");
        this.columnsName.put("password","password");
        this.columnsName.put("email","email");
        this.columnsName.put("created_at","created_at");
        this.columnsName.put("updated_at","updated_at");
        this.columnsName.put("last_login","last_login");
    }

    // Customizable
    private void setColumnsType(){
        this.columnsType.put("id","uuid");
        this.columnsType.put("role_id","string");
        this.columnsType.put("username","string");
        this.columnsType.put("password","string");
        this.columnsType.put("email","string");
        this.columnsType.put("created_at","timestamptz");
        this.columnsType.put("updated_at","timestamptz");
        this.columnsType.put("last_login","timestamptz");
    }

    private void setColumnsArray(String[] columns){
        this.selectQueryArray.addAll(Arrays.asList(columns));
    }

    private void setColumnsArray(String columns){
        this.selectQueryArray.add(columns);
    }

    private void setMessages(){
        SharedData sharedData = this.vertx.sharedData();
        LocalMap<String, JsonObject> jMapData = sharedData.getLocalMap("vertx");
        this.responseMessages = jMapData.get("messages.response").getJsonObject("service").getJsonObject(this.service).getJsonObject("model");

    }

    public Future<Void> findOne(String id){

        if(this.selectQuery == null){
            this.selectQuery = this.select();
        }

        Promise<Void> promise = Promise.promise();
        UUID uId = UUID.fromString(id);
        Tuple args = Tuple.of(uId);
        StringBuilder query = new StringBuilder();
        query.append(this.selectQuery)
            .append(" FROM ")
            .append(this.tableName)
            .append(" WHERE ")
            .append(this.tableName)
            .append(".")
            .append("id")
            .append(" = ")
            .append("$1")
            .append(" LIMIT 1 ");

        this.logger.info("Query : "+query.toString());
        this.logger.info("Parameter : "+args.toString());

        this.conn.preparedQuery(query.toString(), args, fetch -> {
            if (fetch.succeeded()) {
                RowSet <Row> rs = fetch.result();

                if (rs.rowCount() == 0) {
                    String message = this.responseMessages.getJsonObject("find-one").getString("not-found");
                    promise.fail(message);
                } else {
                    Row row = rs.iterator().next();
                    JsonObject data = new JsonObject();

                    this.selectQueryArray.forEach( i -> {
                        data.put(this.columnsName.get(i),this.printResult(row, i));
                    });
                    this.value = data;

                    promise.complete();
                }
            }else {
                promise.fail(fetch.cause().getMessage());
            }
        });

        return promise.future();
    }

    private String select(){
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        for (int i = 0; i < (this.columns.length) ; i++) {
            query.append(this.tableName)
                .append(".")
                .append(this.columns[i]);
            if(i != (this.columns.length-1) )
                query.append(", ");

        }
        this.setColumnsArray(this.columns);
        return query.toString();
    }

    public UserModel select(String[] columns){
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        for (int i = 0; i < (columns.length) ; i++) {
            query.append(this.tableName);
            query.append(".");
            query.append(columns[i]);
            if(i != (columns.length-1) )
                query.append(", ");

        }
        this.setColumnsArray(columns);
        this.selectQuery = query.toString();
        return this;
    }

    public UserModel select(String column){

        StringBuilder query = new StringBuilder();

        if(this.selectQuery == null){
            query.append(" SELECT ")
                .append(this.tableName)
                .append(".")
                .append(column)
                .append(" ");
            this.selectQuery = query.toString();
        }else{
            query.append(", ")
                .append(this.tableName)
                .append(".")
                .append(column)
                .append(" ");
            this.selectQuery = this.selectQuery+query.toString();
        }

        this.setColumnsArray(column);
        return this;
    }

    public JsonObject toJson(){
        return this.value;
    }

    public String get(){
        return this.value.toString();
    }

    private String printResult(Row row, String i){
        String result;

        if(this.columnsType.get(i).equalsIgnoreCase("uuid")){
            result = row.getUUID(i) == null ? "" : row.getUUID(i).toString();
        }else if(this.columnsType.get(i).equalsIgnoreCase("timestamptz")){
            result = row.getOffsetDateTime(i) == null ? "" : row.getOffsetDateTime(i).toString();
        }else if(this.columnsType.get(i).equalsIgnoreCase("integer")){
            result = row.getInteger(i) == null ? "" : row.getInteger(i).toString();
        }else if(this.columnsType.get(i).equalsIgnoreCase("date")){
            result = row.getLocalDate(i) == null ? "" : row.getLocalDate(i).toString();
        }else if(this.columnsType.get(i).equalsIgnoreCase("timestamp")){
            result = row.getLocalDateTime(i) == null ? "" : row.getLocalDateTime(i).toString();
        }else if(this.columnsType.get(i).equalsIgnoreCase("datetime")){
            result = row.getValue(i) == null ? "" : row.getValue(i).toString();
        }else if(this.columnsType.get(i).equalsIgnoreCase("double")){
            result = row.getDouble(i) == null ? "" : row.getDouble(i).toString();
        }else if(this.columnsType.get(i).equalsIgnoreCase("float")){
            result = row.getFloat(i) == null ? "" : row.getFloat(i).toString();
        }else if(this.columnsType.get(i).equalsIgnoreCase("number")){
            result = row.getValue(i) == null ? "" : row.getValue(i).toString();
        }else if(this.columnsType.get(i).equalsIgnoreCase("boolean")){
            result = row.getBoolean(i) == null ? "" : row.getBoolean(i).toString();
        }else{
            result = row.getString(i) == null ? "" : row.getString(i);
        }

        return result;
    }

}
