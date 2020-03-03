/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Transaction;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.data.Numeric;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;

/**
 *
 * @author Syn-User
 */
public class Model {
    
    protected ArrayList<String> columns = new ArrayList<>();
    protected String tableName;
    protected String service;
    protected Vertx vertx;
    protected Logger logger;
    protected Transaction trans;
    protected Map<String, String> columnsName = new HashMap<>();
    protected Map<String, String> columnsType = new HashMap<>();
    public Map<String, String> columnsValue = new HashMap<>();

    private JsonObject responseMessages;
    private JsonObject value = new JsonObject();
    private JsonArray valueArray = new JsonArray();
    private String selectQuery;
    private String whereQuery;
    private Tuple whereArgsQuery = Tuple.tuple();
    private String limitQuery;
    private String orderQuery;
    private int index = 1;
    private ArrayList <String> selectQueryArray = new ArrayList<>();
    
    protected Model(Vertx vertx, Transaction trans){
        this.vertx = vertx;
        this.trans = trans;
        this.init();
    }
    
    private void init(){
        this.setColumnsName();
        this.setColumnsType();
        this.setColumns();
        this.setTableName();
        this.setService();
        this.setMessages();
    }

    /* 
        Customizable
        This count type must same with count of column
    */ 
    protected void setColumnsName(){
        this.columnsName.put("column1","columnName1");
        this.columnsName.put("column2","columnName2");
    }

    /* 
        This count type must same with count of column
        Support for Type
        - UUID
        - Timestamptz
        - Integer
        - Date
        - Datetime
        - Timestamp
        - Double
        - Float
        - Number
        - Boolean
    */ 
    protected void setColumnsType(){
        this.columnsType.put("column1","string");
        this.columnsType.put("column2","integer");
    }
    
    /* 
        Set column from this function
    */ 
    protected void setColumns(){
        this.columns.add("column1");
        this.columns.add("column2");
    }
    
    /* 
        Table name in database
    */ 
    protected void setTableName(){
        this.tableName = "tableName";
    }
    
    /* 
        Reference from response.json in resources/messages folder
    */ 
    protected void setService(){
        this.service = "service";
    }

    private void setColumnsArray(ArrayList<String> columns){
        this.selectQueryArray = columns;
    }

    private void setColumnArray(String columns){
        this.selectQueryArray.add(columns);
    }

    private void setMessages(){
        SharedData sharedData = this.vertx.sharedData();
        LocalMap<String, JsonObject> jMapData = sharedData.getLocalMap("vertx");
        this.responseMessages = jMapData.get("messages.response").getJsonObject("service").getJsonObject(this.service).getJsonObject("model");

    }
    
    public Future<Void> save(){
    
        Promise<Void> promise = Promise.promise();
        
        StringBuilder query = new StringBuilder();
        StringBuilder queryValue = new StringBuilder();
        Tuple args = Tuple.tuple();
        String id = UUID.randomUUID().toString();
        this.index = 1;
        
        if(this.columnsValue.get("id") == null || this.columnsValue.get("id").trim().equalsIgnoreCase("")){
            this.columnsValue.put("id", id);
        }
        
        query.append("INSERT INTO ")
            .append(this.tableName)
            .append(" ( ");
        
        for (int i = 0 ; i < this.columns.size() ; i++){
            if(this.columnsValue.get(this.columns.get(i)) != null){
                query.append(" ")
                    .append(this.columns.get(i));
                
                queryValue.append(" $")
                    .append(this.index++);
                
                if(i != (this.columnsValue.size()-1) ){
                    query.append(", ");
                    queryValue.append(", ");
                }
                
                args = this.addArgs(this.columns.get(i), this.columnsValue.get(this.columns.get(i)), args);
                
                query.append(" ");
                queryValue.append(" ");
            }
        }
            
        
        query.append(" ) VALUES ( ").append(queryValue.toString()).append(" ) ;");

        this.logger.info("Query : "+query.toString());
        this.logger.info("Parameter : "+args.toString());
        
        this.trans.preparedQuery(query.toString(), args, fetch -> {
            if (fetch.succeeded()) {
                this.findOne(id).setHandler(select -> {
                    if(select.succeeded()){
                        promise.complete();
                    }else{
                        promise.fail(select.cause().getMessage());
                    }
                });
            }else{
                promise.fail(fetch.cause().getMessage());
            }
        });
        
                
        
        
        
        return promise.future();
    }

    public Future<Void> findOne(String id){

        StringBuilder addWhere = new StringBuilder();
        Promise<Void> promise = Promise.promise();
        StringBuilder query = new StringBuilder();
        this.index = 1;
        
        if(this.selectQuery == null){
            this.selectQuery = this.select();
        }
        
        addWhere.append(this.whereQuery != null ? " AND " : " WHERE ")
            .append(this.tableName)
            .append(".")
            .append("id")
            .append(" = $")
            .append(this.index++);
        this.whereQuery = this.whereQuery != null ? this.whereQuery +addWhere.toString() : addWhere.toString();
        
        this.whereArgsQuery = this.addArgs("id", id, this.whereArgsQuery);
        query.append(this.selectQuery)
            .append(" FROM ")
            .append(this.tableName)
            .append(" ")
            .append(this.whereQuery == null ? "" : this.whereQuery)
            .append(" ")
            .append(" LIMIT 1 ");

        this.logger.info("Query : "+query.toString());
        this.logger.info("Parameter : "+this.whereArgsQuery.toString());

        this.trans.preparedQuery(query.toString(), this.whereArgsQuery, fetch -> {
            if (fetch.succeeded()) {
                RowSet <Row> rs = fetch.result();

                if (rs.rowCount() == 0) {
                    String message = this.responseMessages.getJsonObject("find-one").getString("not-found");
                    promise.fail(message);
                } else {
                    Row row = rs.iterator().next();
                    JsonObject data = new JsonObject();

                    this.selectQueryArray.forEach( column -> {
                        data.put(this.columnsName.get(column),this.result(row, column));
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
    
    public Future<Void> findOne(){

        Promise<Void> promise = Promise.promise();
        StringBuilder query = new StringBuilder();
        this.index = 1;
        
        if(this.selectQuery == null){
            this.selectQuery = this.select();
        }
        
        query.append(this.selectQuery)
            .append(" FROM ")
            .append(this.tableName)
            .append(" ")
            .append(this.whereQuery == null ? "" : this.whereQuery)
            .append(" ")
            .append(" LIMIT 1 ");

        this.logger.info("Query : "+query.toString());
        this.logger.info("Parameter : "+this.whereArgsQuery.toString());

        this.trans.preparedQuery(query.toString(), this.whereArgsQuery, fetch -> {
            if (fetch.succeeded()) {
                RowSet <Row> rs = fetch.result();

                if (rs.rowCount() == 0) {
                    String message = this.responseMessages.getJsonObject("find-one").getString("not-found");
                    promise.fail(message);
                } else {
                    Row row = rs.iterator().next();
                    JsonObject data = new JsonObject();

                    this.selectQueryArray.forEach( column -> {
                        data.put(this.columnsName.get(column),this.result(row, column));
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
    
    public Future<Void> find(){

        if(this.selectQuery == null){
            this.selectQuery = this.select();
        }
        
        Promise<Void> promise = Promise.promise();
        StringBuilder query = new StringBuilder();
        this.index = 1;
        query.append(this.selectQuery)
            .append(" FROM ")
            .append(this.tableName)
            .append(" ")
            .append(this.whereQuery == null ? "" : this.whereQuery)
            .append(" ")
            .append(this.limitQuery == null ? "" : this.limitQuery)
            .append(" ")
            .append(this.orderQuery == null ? "" : this.orderQuery);

        this.logger.info("Query : "+query.toString());
        this.logger.info("Parameter : "+this.whereArgsQuery.toString());

        this.trans.preparedQuery(query.toString(), this.whereArgsQuery, fetch -> {
            if (fetch.succeeded()) {
                for (Row row : fetch.result()) {
                    JsonObject data = new JsonObject();
                    this.selectQueryArray.forEach( i -> {
                        data.put(this.columnsName.get(i),this.result(row, i));
                    });
                   this.valueArray.add(data);
                }
                
                promise.complete();
            }else {
                promise.fail(fetch.cause().getMessage());
            }
        });

        return promise.future();
    }
    
    public Future<String> count(){
        
        Promise<String> promise = Promise.promise();
        StringBuilder query = new StringBuilder();
        this.index = 1;
        query.append("SELECT count(")
            .append(this.tableName)
            .append(".id)")
            .append(" FROM ")
            .append(this.tableName)
            .append(" ")
            .append(this.whereQuery == null ? "" : this.whereQuery);

        this.logger.info("Query : "+query.toString());
        this.logger.info("Parameter : "+this.whereArgsQuery.toString());

        this.trans.preparedQuery(query.toString(), this.whereArgsQuery, fetch -> {
            if (fetch.succeeded()) {
                RowSet <Row> rs = fetch.result();
                Row row = rs.iterator().next();
                promise.complete(row.getInteger(0).toString());
            }else {
                promise.fail(fetch.cause().getMessage());
            }
        });

        return promise.future();
    }
    
    public Model limit(String limit){
        this.limitQuery = String.format(" LIMIT %s ",limit);
        return this;
    }
    
    public Model orderBy(String column, String orderType){
        this.orderQuery = String.format(" ORDER BY %s $s ",column,orderType);
        return this;
    }

    public Model where(String column, String operator, String value){
        
        StringBuilder where = new StringBuilder();
        
        if(this.whereQuery == null){
            where.append(" WHERE ")
                .append(this.tableName)
                .append(".")
                .append(column)
                .append(" ")
                .append(operator)
                .append(" $")
                .append(this.index++)
                .append(" ");
        }else{
            where
                .append(" AND ")
                .append(this.tableName)
                .append(".")
                .append(column)
                .append(" ")
                .append(operator)
                .append(" $")
                .append(this.index++)
                .append(" ");
        }
        
        this.whereArgsQuery = this.addArgs(column, value, this.whereArgsQuery);
        this.whereQuery = this.whereQuery == null ? where.toString() : this.whereQuery + where.toString();
        return this;
    }
    
    public Model orWhere(String column, String operator, String value){
        
        StringBuilder where = new StringBuilder();
        
        if(this.whereQuery == null){
            where.append(" WHERE ")
                .append(this.tableName)
                .append(".")
                .append(column)
                .append(" ")
                .append(operator)
                .append(" $")
                .append(this.index++)
                .append(" ");
        }else{
            where.append(" OR ")
                .append(this.tableName)
                .append(".")
                .append(column)
                .append(" ")
                .append(operator)
                .append(" $")
                .append(this.index++)
                .append(" ");
        }
        
        this.whereArgsQuery = this.addArgs(column, value, this.whereArgsQuery);
        this.whereQuery = this.whereQuery == null ? where.toString() : this.whereQuery + where.toString();
        return this;
    }
    
    private String select(){
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        for (int i = 0; i < this.columns.size() ; i++) {
            query.append(this.tableName)
                .append(".")
                .append(this.columns.get(i));
            if(i != (this.columns.size()-1) )
                query.append(", ");

        }
        this.setColumnsArray(this.columns);
        return query.toString();
    }

    public Model select(ArrayList<String> columns){
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        for (int i = 0; i < columns.size() ; i++) {
            query.append(this.tableName);
            query.append(".");
            query.append(columns.get(i));
            if(i != (columns.size()-1) )
                query.append(", ");

        }
        this.setColumnsArray(columns);
        this.selectQuery = query.toString();
        return this;
    }

    public Model select(String column){

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

        this.setColumnArray(column);
        return this;
    }

    public JsonObject getJson(){
        return this.value;
    }
    
    public JsonArray getJsonArray(){
        return this.valueArray;
    }

    public String get(){
        return this.value.toString();
    }
    
    public String getArray(){
        return this.valueArray.toString();
    }

    private String result(Row row, String column){
        String result;

        if(this.columnsType.get(column).equalsIgnoreCase("uuid")){
            result = row.getUUID(column) == null ? "" : row.getUUID(column).toString();
        }else if(this.columnsType.get(column).equalsIgnoreCase("timestamptz")){
            result = row.getOffsetDateTime(column) == null ? "" : row.getOffsetDateTime(column).toString();
        }else if(this.columnsType.get(column).equalsIgnoreCase("integer")){
            result = row.getInteger(column) == null ? "" : row.getInteger(column).toString();
        }else if(this.columnsType.get(column).equalsIgnoreCase("date")){
            result = row.getLocalDate(column) == null ? "" : row.getLocalDate(column).toString();
        }else if(this.columnsType.get(column).equalsIgnoreCase("timestamp")){
            result = row.getLocalDateTime(column) == null ? "" : row.getLocalDateTime(column).toString();
        }else if(this.columnsType.get(column).equalsIgnoreCase("datetime")){
            result = row.getLocalDateTime(column) == null ? "" : row.getLocalDateTime(column).toString();
        }else if(this.columnsType.get(column).equalsIgnoreCase("double")){
            result = row.getDouble(column) == null ? "" : row.getDouble(column).toString();
        }else if(this.columnsType.get(column).equalsIgnoreCase("float")){
            result = row.getFloat(column) == null ? "" : row.getFloat(column).toString();
        }else if(this.columnsType.get(column).equalsIgnoreCase("number")){
            result = row.getValue(column) == null ? "" : row.getValue(column).toString();
        }else if(this.columnsType.get(column).equalsIgnoreCase("boolean")){
            result = row.getBoolean(column) == null ? "" : row.getBoolean(column).toString();
        }else{
            result = row.getString(column) == null ? "" : row.getString(column);
        }

        return result;
    }
    
    private Tuple addArgs(String column, String value, Tuple args){

        if(this.columnsType.get(column).equalsIgnoreCase("uuid")){
            args.addUUID(UUID.fromString(value));
        }else if(this.columnsType.get(column).equalsIgnoreCase("timestamptz")){
            args.addOffsetDateTime(OffsetDateTime.parse(value));
        }else if(this.columnsType.get(column).equalsIgnoreCase("integer")){
            args.addInteger(Integer.parseInt(value));
        }else if(this.columnsType.get(column).equalsIgnoreCase("date")){
            args.addLocalDate(LocalDate.parse(value));
        }else if(this.columnsType.get(column).equalsIgnoreCase("timestamp")){
            args.addLocalDateTime(LocalDateTime.parse(value));
        }else if(this.columnsType.get(column).equalsIgnoreCase("datetime")){
            args.addLocalDateTime(LocalDateTime.parse(value));
        }else if(this.columnsType.get(column).equalsIgnoreCase("double")){
            args.addDouble(Double.parseDouble(value));
        }else if(this.columnsType.get(column).equalsIgnoreCase("float")){
            args.addFloat(Float.parseFloat(value));
        }else if(this.columnsType.get(column).equalsIgnoreCase("number")){
            args.addValue(Numeric.parse(value));
        }else if(this.columnsType.get(column).equalsIgnoreCase("boolean")){
            args.addBoolean(Boolean.parseBoolean(value));
        }else{
            args.addString(String.valueOf(value));
        }
        
        return args;
    }

    
    
}
