/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.core;

import com.andrechristikan.helper.GeneralHelper;
import com.andrechristikan.helper.ParserHelper;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
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
import org.slf4j.LoggerFactory;

/**
 *
 * @author Syn-User
 */
public abstract class CoreModel {
    
    protected static Logger logger = LoggerFactory.getLogger(CoreModel.class);
    protected static ParserHelper parser = new ParserHelper();
    private final Transaction trans;
    protected static Vertx coreVertx;
    
    // mandatory field
    protected static String tableName = "TableName";
    protected static String primaryKeyName = "id";
    
    // for column
    private ArrayList<String> columns = new ArrayList<>();
    private final Map<String, String> columnsName = new HashMap<>();
    private final Map<String, String> columnsType = new HashMap<>();
    public Map<String, String> columnsValue = new HashMap<>();

    // For query
    private String selectQuery;
    private ArrayList <String> selectQueryArray = new ArrayList<>();
    
    private String whereQuery;
    private Tuple whereArgsQuery = Tuple.tuple();
    
    private String limitQuery;
    private String orderQuery;
    private String primaryKeyValue;
    
    private JsonObject jsonObjectValue = new JsonObject();
    private JsonArray jsonArrayValue = new JsonArray();
    
    // other
    private static JsonObject messages;
    private static JsonObject configs;
    private int index = 1;
    
    protected CoreModel(Vertx vertx, Transaction trans){
        this.trans = trans;
        coreVertx = vertx;

        this.setColumnsToMap();
        this.setColumnsNameToMap();
        this.setColumnsTypeToMap();

        messages = GeneralHelper.setMessages(vertx);
        configs = GeneralHelper.setConfigs(vertx);
    }
    
    
    /* 
        This is mandatory
        Set column from this function
    */ 
    protected ArrayList<String> setColumns(){
        return new ArrayList<>();
    }
    
    

    /* 
        This is optional
        This count type must same with count of column
    */ 
    protected Map<String, String> setColumnsName(){
        return new HashMap<>();
    }

    
    /* 
        This is mandatory
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
    protected Map<String, String> setColumnsType(){
        return new HashMap<>();
    }

    protected static String trans(String path){
        return GeneralHelper.trans(path, messages);
    }

    protected static String conf(String path){
        return GeneralHelper.conf(path, configs);
    }

    protected static JsonArray confAsJsonArray(String path){
        return GeneralHelper.confAsJsonArray(path, configs);
    }

    protected static JsonObject confAsJsonObject(String path){
        return GeneralHelper.confAsJsonObject(path, configs);
    }

    private void setColumnsToMap(){
        this.columns = setColumns();
    }
    
    private void setColumnsTypeToMap(){
        Map<String, String> columnsTypeFromSetter = setColumnsType();
        this.columns.forEach(column -> {
            this.columnsType.put(column, columnsTypeFromSetter.get(column));
        });
    }
    
    private void setColumnsNameToMap(){
        Map<String, String> columnsNameFromSetter = setColumnsName();
        this.columns.forEach(column -> {
            this.columnsName.put(column, columnsNameFromSetter.get(column));
        });
    }
    
    private void setColumnsArray(ArrayList<String> columns){
        this.selectQueryArray.addAll(columns);
    }

    private void setColumnArray(String columns){
        this.selectQueryArray.add(columns);
    }
    
    public Future<Void> update(){
    
        Promise<Void> promise = Promise.promise();
        StringBuilder query = new StringBuilder();
        Tuple args = this.whereArgsQuery;

        if(this.jsonObjectValue == null || this.jsonObjectValue.size() == 0 ){
            promise.fail(trans("response.service.user.model.update.need-select-before-update"));
        }
        
        query.append("UPDATE ")
            .append(tableName)
            .append(" SET ");
        
        for (int i = 0 ; i < this.columns.size() ; i++){
            if( !this.columns.get(i).equalsIgnoreCase(primaryKeyName) && this.columnsValue.get(this.columns.get(i)) != null){
                query.append(" ")
                    .append(this.columns.get(i))
                    .append(" = $")
                    .append(this.index++)
                    .append(" ");
                
                if(i != (this.columnsValue.size()-1) ){
                    query.append(", ");
                }
                
                this.addArgs(this.columns.get(i), this.columnsValue.get(this.columns.get(i)), args);
                
                query.append(" ");
            }
        }
        
        query.append(" ")
            .append(this.whereQuery == null ? String.format("WHERE %s = $%d ", primaryKeyName, this.index) : this.whereQuery)
            .append(";");
        
        if(this.whereQuery == null){
            this.addArgs(primaryKeyName, this.primaryKeyValue, args);
        }
        
        logger.info("Query : "+query.toString());
        logger.info("Parameter : "+args.toString());
        
        this.trans.preparedQuery(query.toString(), args, fetch -> {
            if (fetch.succeeded()) {
                this.stop();
                this.findOne(this.primaryKeyValue).setHandler(select -> {
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

    public Future<Void> update(Map<String, String> localColumnsValue, String id){

        Promise<Void> promise = Promise.promise();
        StringBuilder query = new StringBuilder();
        Tuple args = this.whereArgsQuery;

        query.append("UPDATE ")
                .append(tableName)
                .append(" SET ");

        for (int i = 0 ; i < this.columns.size() ; i++){
            if(! this.columns.get(i).equalsIgnoreCase(primaryKeyName) && localColumnsValue.get(this.columns.get(i)) != null){
                query.append(" ")
                        .append(this.columns.get(i))
                        .append(" = $")
                        .append(this.index++)
                        .append(" ");

                if(i != (localColumnsValue.size()-1) ){
                    query.append(", ");
                }

                this.addArgs(this.columns.get(i), localColumnsValue.get(this.columns.get(i)), args);

                query.append(" ");
            }
        }

        query.append(" ")
                .append(this.whereQuery == null ? "" : this.whereQuery)
                .append(" ")
                .append(this.whereQuery == null ? String.format(" WHERE %s = $%d ", primaryKeyName, this.index++) : String.format(" AND %s = $%d ", primaryKeyName, this.index++))
                .append(";");

        this.addArgs(primaryKeyName, id, args);

        logger.info("Query : "+query.toString());
        logger.info("Parameter : "+args.toString());

        this.trans.preparedQuery(query.toString(), args, fetch -> {
            if (fetch.succeeded()) {
                this.stop();
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
    
    public Future<Void> save(){
    
        Promise<Void> promise = Promise.promise();
        
        StringBuilder query = new StringBuilder();
        StringBuilder queryValue = new StringBuilder();
        Tuple args = Tuple.tuple();
        String id = UUID.randomUUID().toString();
        
        if(!this.columnsValue.containsKey(primaryKeyName) || this.columnsValue.get(primaryKeyName) == null || this.columnsValue.get(primaryKeyName).trim().equalsIgnoreCase("")){
            this.columnsValue.put(primaryKeyName, id);
        }
        
        query.append("INSERT INTO ")
            .append(tableName)
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
                
                this.addArgs(this.columns.get(i), this.columnsValue.get(this.columns.get(i)), args);
                
                query.append(" ");
                queryValue.append(" ");
            }
        }
        
        query.append(" ) VALUES ( ").append(queryValue.toString()).append(" ) ;");

        logger.info("Query : "+query.toString());
        logger.info("Parameter : "+args.toString());
        
        this.trans.preparedQuery(query.toString(), args, fetch -> {
            if (fetch.succeeded()) {
                this.stop();
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
    
    public Future<Void> insert(Map<String, String> localColumnsValue){

        Promise<Void> promise = Promise.promise();

        StringBuilder query = new StringBuilder();
        StringBuilder queryValue = new StringBuilder();
        Tuple args = Tuple.tuple();
        String id = UUID.randomUUID().toString();

        if(!localColumnsValue.containsKey(primaryKeyName) || localColumnsValue.get(primaryKeyName) == null || localColumnsValue.get(primaryKeyName).trim().equalsIgnoreCase("")){
            localColumnsValue.put(primaryKeyName, id);
        }

        query.append("INSERT INTO ")
                .append(tableName)
                .append(" ( ");

        for (int i = 0 ; i < this.columns.size() ; i++){
            if(localColumnsValue.get(this.columns.get(i)) != null){
                query.append(" ")
                        .append(this.columns.get(i));

                queryValue.append(" $")
                        .append(this.index++);

                if(i != (localColumnsValue.size()-1) ){
                    query.append(", ");
                    queryValue.append(", ");
                }

                this.addArgs(this.columns.get(i), localColumnsValue.get(this.columns.get(i)), args);

                query.append(" ");
                queryValue.append(" ");
            }
        }

        query.append(" ) VALUES ( ").append(queryValue.toString()).append(" ) ;");

        logger.info("Query : "+query.toString());
        logger.info("Parameter : "+args.toString());

        this.trans.preparedQuery(query.toString(), args, fetch -> {
            if (fetch.succeeded()) {
                this.stop();
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
    
    public Future<Void> delete(){

        Promise <Void> promise = Promise.promise();
        StringBuilder query = new StringBuilder();
        Tuple args = this.whereArgsQuery;
        
        if(this.jsonObjectValue == null || this.jsonObjectValue.size() == 0 ){
            promise.fail(trans("response.service.user.model.delete.need-select-before-delete"));
        }

        query.append("DELETE FROM ")
            .append(tableName)
            .append(" ")
            .append(this.whereQuery == null ? String.format("WHERE %s = $%d ", primaryKeyName, this.index++) : this.whereQuery);

        if(this.whereQuery == null){
            this.addArgs(primaryKeyName, this.primaryKeyValue, args);
        }
        
        logger.info("Query : "+query.toString());
        logger.info("Parameter : "+args.toString());

        this.trans.preparedQuery(query.toString(), args, fetch -> {
            if (fetch.succeeded()) {
                this.stop();
                this.setNullModelValues();
                promise.complete();
            }else{
                promise.fail(fetch.cause().getMessage());
            }
        });

        return promise.future();
    }
    
    public Future<Void> delete(String id){

        Promise <Void> promise = Promise.promise();
        StringBuilder query = new StringBuilder();
        Tuple args = this.whereArgsQuery;
        
        query.append("DELETE FROM ")
                .append(tableName)
                .append(" ");
        
        query.append(" ")
                .append(this.whereQuery == null ? "" : this.whereQuery)
                .append(" ")
                .append(this.whereQuery == null ? String.format(" WHERE %s.%s = $%d ", tableName, primaryKeyName, this.index++) : String.format(" AND %s.%s = $%d ", tableName, primaryKeyName, this.index++))
                .append(";");

        this.addArgs(primaryKeyName, id, args);
        
        logger.info("Query : "+query.toString());
        logger.info("Parameter : "+args.toString());

        this.trans.preparedQuery(query.toString(), args, fetch -> {
            if (fetch.succeeded()) {
                this.stop();
                this.setNullModelValues();
                promise.complete();
            }else{
                promise.fail(fetch.cause().getMessage());
            }
        });

        return promise.future();
    }
    
    public Future<Void> findOne(String id){

        Promise<Void> promise = Promise.promise();
        StringBuilder query = new StringBuilder();
        Tuple args = this.whereArgsQuery;
        
        if(this.selectQuery == null){
            this.selectQuery = this.select();
        }
        
        query.append(this.selectQuery)
            .append(" ")
            .append(" FROM ")
            .append(tableName)
            .append(" ")
            .append(this.whereQuery == null ? "" : this.whereQuery)
            .append(" ")
            .append(this.whereQuery == null ? String.format(" WHERE %s.%s = $%d ", tableName, primaryKeyName, this.index++) : String.format(" AND %s.%s = $%d ", tableName, primaryKeyName, this.index++))
            .append(" ")
            .append(" LIMIT 1 ;");

        this.addArgs(primaryKeyName, id, args);
        
        logger.info("Query : "+query.toString());
        logger.info("Parameter : "+args.toString());

        this.trans.preparedQuery(query.toString(), args, fetch -> {
            if (fetch.succeeded()) {
                RowSet <Row> rs = fetch.result();

                if (rs.rowCount() == 0) {
                    String message = trans("response.service.user.model.find-one.not-found");
                    promise.fail(message);
                } else {
                    Row row = rs.iterator().next();
                    JsonObject data = new JsonObject();

                    this.selectQueryArray.forEach( column -> {

                        if(column.equalsIgnoreCase(primaryKeyName)){
                            this.primaryKeyValue = this.result(row, column);
                        }

                        data.put(this.columnsName.get(column),this.result(row, column));
                        this.columnsValue.put(column, this.result(row, column));
                    });
                    this.jsonObjectValue = data;

                    this.stop();
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
        
        if(this.whereQuery == null || this.whereQuery.trim().equalsIgnoreCase("")){
            promise.fail(trans("response.service.user.model.find-one.need-where-statement"));
        }

        if(this.selectQuery == null){
            this.selectQuery = this.select();
        }
        
        query.append(this.selectQuery)
            .append(" FROM ")
            .append(tableName)
            .append(" ")
            .append(this.whereQuery == null ? "" : this.whereQuery)
            .append(" ")
            .append(" LIMIT 1 ;");

        logger.info("Query : "+query.toString());
        logger.info("Parameter : "+this.whereArgsQuery.toString());

        this.trans.preparedQuery(query.toString(), this.whereArgsQuery, fetch -> {
            if (fetch.succeeded()) {
                RowSet <Row> rs = fetch.result();

                if (rs.rowCount() == 0) {
                    String message = trans("response.service.user.model.find-one.not-found");
                    promise.fail(message);
                } else {
                    Row row = rs.iterator().next();
                    JsonObject data = new JsonObject();

                    this.selectQueryArray.forEach( column -> {

                        if(column.equalsIgnoreCase(primaryKeyName)){
                            this.primaryKeyValue = this.result(row, column);
                        }

                        data.put(this.columnsName.get(column),this.result(row, column));
                        this.columnsValue.put(column, this.result(row, column));
                    });
                    this.jsonObjectValue = data;

                    this.stop();
                    promise.complete();
                }
            }else {
                promise.fail(fetch.cause().getMessage());
            }
        });

        return promise.future();
    }
    
    public Future<Void> findAll(){

        Promise<Void> promise = Promise.promise();
        StringBuilder query = new StringBuilder();
        
        if(this.selectQuery == null){
            this.selectQuery = this.select();
        }
        
        query.append(this.selectQuery)
            .append(" FROM ")
            .append(tableName)
            .append(" ")
            .append(this.whereQuery == null ? "" : this.whereQuery)
            .append(" ")
            .append(this.limitQuery == null ? "" : this.limitQuery)
            .append(" ")
            .append(this.orderQuery == null ? "" : this.orderQuery)
            .append(";");

        logger.info("Query : "+query.toString());
        logger.info("Parameter : "+this.whereArgsQuery.toString());

        this.trans.preparedQuery(query.toString(), this.whereArgsQuery, fetch -> {
            if (fetch.succeeded()) {

                for (Row row : fetch.result()) {
                    JsonObject data = new JsonObject();
                    
                    this.selectQueryArray.forEach( i -> {
                        data.put(this.columnsName.get(i),this.result(row, i));
                    });
                    this.jsonArrayValue.add(data);
                }
                
                this.stop();
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

        if(this.jsonObjectValue != null && this.jsonObjectValue.size() > 0 ){
            this.stop();
            promise.complete("1");
        }else if(this.jsonArrayValue != null && this.jsonArrayValue.size() > 0){
            this.stop();
            promise.complete(String.valueOf(this.jsonArrayValue.size()));
        }else{
            
            query.append("SELECT count(")
                    .append(tableName)
                    .append(".")
                    .append(primaryKeyName)
                    .append(") FROM ")
                    .append(tableName)
                    .append(" ")
                    .append(this.whereQuery == null ? "" : this.whereQuery)
                    .append(";");

            logger.info("Query : "+query.toString());
            logger.info("Parameter : "+this.whereArgsQuery.toString());

            this.trans.preparedQuery(query.toString(), this.whereArgsQuery, fetch -> {
                if (fetch.succeeded()) {
                    RowSet <Row> rs = fetch.result();
                    Row row = rs.iterator().next();

                    this.stop();
                    promise.complete(row.getInteger(0).toString());
                }else {
                    promise.fail(fetch.cause().getMessage());
                }
            });
        }

        return promise.future();
    }
    
    public CoreModel limit(String limit){
        this.limitQuery = String.format(" LIMIT %s ",limit);
        return this;
    }
    
    public CoreModel orderBy(String column, String orderType){
        this.orderQuery = String.format(" ORDER BY %s $s ",column,orderType);
        return this;
    }

    public CoreModel where(String column, String operator, String value){
        
        StringBuilder where = new StringBuilder();
        
        if(this.whereQuery == null){
            where.append(" WHERE ")
                .append(tableName)
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
                .append(tableName)
                .append(".")
                .append(column)
                .append(" ")
                .append(operator)
                .append(" $")
                .append(this.index++)
                .append(" ");
        }
        
        this.addArgs(column, value, this.whereArgsQuery);
        this.whereQuery = this.whereQuery == null ? where.toString() : this.whereQuery + where.toString();
        return this;
    }

    public CoreModel whereRaw(String column, String operator, String value){

        StringBuilder where = new StringBuilder();

        if(this.whereQuery == null){
            where.append(" WHERE ")
                    .append(column)
                    .append(" ")
                    .append(operator)
                    .append(" $")
                    .append(this.index++)
                    .append(" ");
        }else{
            where
                    .append(" AND ")
                    .append(column)
                    .append(" ")
                    .append(operator)
                    .append(" $")
                    .append(this.index++)
                    .append(" ");
        }

        this.addArgsRaw(column, value, this.whereArgsQuery);
        this.whereQuery = this.whereQuery == null ? where.toString() : this.whereQuery + where.toString();
        return this;
    }
    
    public CoreModel orWhere(String column, String operator, String value){
        
        StringBuilder where = new StringBuilder();
        
        if(this.whereQuery == null){
            where.append(" WHERE ")
                .append(tableName)
                .append(".")
                .append(column)
                .append(" ")
                .append(operator)
                .append(" $")
                .append(this.index++)
                .append(" ");
        }else{
            where.append(" OR ")
                .append(tableName)
                .append(".")
                .append(column)
                .append(" ")
                .append(operator)
                .append(" $")
                .append(this.index++)
                .append(" ");
        }
        
        this.addArgs(column, value, this.whereArgsQuery);
        this.whereQuery = this.whereQuery == null ? where.toString() : this.whereQuery + where.toString();
        return this;
    }

    public CoreModel orWhereRaw(String column, String operator, String value){

        StringBuilder where = new StringBuilder();

        if(this.whereQuery == null){
            where.append(" WHERE ")
                    .append(column)
                    .append(" ")
                    .append(operator)
                    .append(" $")
                    .append(this.index++)
                    .append(" ");
        }else{
            where.append(" OR ")
                    .append(column)
                    .append(" ")
                    .append(operator)
                    .append(" $")
                    .append(this.index++)
                    .append(" ");
        }

        this.addArgsRaw(column, value, this.whereArgsQuery);
        this.whereQuery = this.whereQuery == null ? where.toString() : this.whereQuery + where.toString();
        return this;
    }
    
    private String select(){
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        for (int i = 0; i < this.columns.size() ; i++) {
            query.append(tableName)
                .append(".")
                .append(this.columns.get(i));
            if(i != (this.columns.size()-1) )
                query.append(", ");

        }
        this.setColumnsArray(this.columns);
        return query.toString();
    }

    public CoreModel select(ArrayList<String> localColumns){
        StringBuilder query = new StringBuilder();
        query.append(" SELECT ");
        for (int i = 0; i < localColumns.size() ; i++) {
            query.append(tableName);
            query.append(".");
            query.append(localColumns.get(i));
            if(i != (localColumns.size()-1) )
                query.append(", ");

        }
        this.setColumnsArray(localColumns);
        this.selectQuery = query.toString();
        return this;
    }

    public CoreModel select(String localColumn){

        StringBuilder query = new StringBuilder();

        if(this.selectQuery == null){
            query.append(" SELECT ")
                .append(tableName)
                .append(".")
                .append(localColumn)
                .append(" ");
            this.selectQuery = query.toString();
        }else{
            query.append(", ")
                .append(tableName)
                .append(".")
                .append(localColumn)
                .append(" ");
            this.selectQuery = this.selectQuery+query.toString();
        }

        this.setColumnArray(localColumn);
        return this;
    }

    public String asString(){
        
        if(this.jsonArrayValue != null && this.jsonArrayValue.size() > 0){
            return this.jsonArrayValue.toString();
        }
        
        return this.jsonObjectValue.toString();
    }

    public JsonObject first(){
        return this.jsonObjectValue;
    }
    
    public JsonArray get(){
        return this.jsonArrayValue;
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
    
    private void addArgs(String column, String value, Tuple args){

        if(this.columnsType.get(column).equalsIgnoreCase("uuid")){
            args.addUUID(value == null || value.trim().equals("") ? null : UUID.fromString(value));
        }else if(this.columnsType.get(column).equalsIgnoreCase("timestamptz")){
            args.addOffsetDateTime(value == null || value.trim().equals("") ? null : OffsetDateTime.parse(value));
        }else if(this.columnsType.get(column).equalsIgnoreCase("integer")){
            args.addInteger(value == null || value.trim().equals("") ? null : Integer.parseInt(value));
        }else if(this.columnsType.get(column).equalsIgnoreCase("date")){
            args.addLocalDate(value == null || value.trim().equals("") ? null : LocalDate.parse(value));
        }else if(this.columnsType.get(column).equalsIgnoreCase("timestamp")){
            args.addLocalDateTime(value == null || value.trim().equals("") ? null : LocalDateTime.parse(value));
        }else if(this.columnsType.get(column).equalsIgnoreCase("datetime")){
            args.addLocalDateTime(value == null || value.trim().equals("") ? null : LocalDateTime.parse(value));
        }else if(this.columnsType.get(column).equalsIgnoreCase("double")){
            args.addDouble(value == null || value.trim().equals("") ? null : Double.parseDouble(value));
        }else if(this.columnsType.get(column).equalsIgnoreCase("float")){
            args.addFloat(value == null || value.trim().equals("") ? null : Float.parseFloat(value));
        }else if(this.columnsType.get(column).equalsIgnoreCase("number")){
            args.addValue(value == null || value.trim().equals("") ? null : Numeric.parse(value));
        }else if(this.columnsType.get(column).equalsIgnoreCase("boolean")){
            args.addBoolean(value == null || value.trim().equals("") ? null : Boolean.parseBoolean(value));
        }else{
            args.addString(value == null || value.trim().equals("") ? null : String.valueOf(value));
        }
    }

    private void addArgsRaw(String columnRaw, String value, Tuple args){

        String localColumnArray[] = columnRaw.split("\\(");
        String localColumn = localColumnArray[localColumnArray.length-1];
        localColumn = localColumn.replace(")","");

        if(this.columnsType.get(localColumn).equalsIgnoreCase("uuid")){
            args.addUUID(value == null || value.trim().equals("") ? null : UUID.fromString(value));
        }else if(this.columnsType.get(localColumn).equalsIgnoreCase("timestamptz")){
            args.addOffsetDateTime(value == null || value.trim().equals("") ? null : OffsetDateTime.parse(value));
        }else if(this.columnsType.get(localColumn).equalsIgnoreCase("integer")){
            args.addInteger(value == null || value.trim().equals("") ? null : Integer.parseInt(value));
        }else if(this.columnsType.get(localColumn).equalsIgnoreCase("date")){
            args.addLocalDate(value == null || value.trim().equals("") ? null : LocalDate.parse(value));
        }else if(this.columnsType.get(localColumn).equalsIgnoreCase("timestamp")){
            args.addLocalDateTime(value == null || value.trim().equals("") ? null : LocalDateTime.parse(value));
        }else if(this.columnsType.get(localColumn).equalsIgnoreCase("datetime")){
            args.addLocalDateTime(value == null || value.trim().equals("") ? null : LocalDateTime.parse(value));
        }else if(this.columnsType.get(localColumn).equalsIgnoreCase("double")){
            args.addDouble(value == null || value.trim().equals("") ? null : Double.parseDouble(value));
        }else if(this.columnsType.get(localColumn).equalsIgnoreCase("float")){
            args.addFloat(value == null || value.trim().equals("") ? null : Float.parseFloat(value));
        }else if(this.columnsType.get(localColumn).equalsIgnoreCase("number")){
            args.addValue(value == null || value.trim().equals("") ? null : Numeric.parse(value));
        }else if(this.columnsType.get(localColumn).equalsIgnoreCase("boolean")){
            args.addBoolean(value == null || value.trim().equals("") ? null : Boolean.parseBoolean(value));
        }else{
            args.addString(value == null || value.trim().equals("") ? null : String.valueOf(value));
        }
    }

    private void stop(){
        this.whereQuery = null;
        this.whereArgsQuery = Tuple.tuple();
        
        this.limitQuery = null;
        this.orderQuery = null;
        
        this.selectQuery = null;
        this.selectQueryArray = new ArrayList<>();
        
        this.index = 1;
    
    }

    private void setNullModelValues(){
        this.primaryKeyValue = null;
        this.columnsValue = new HashMap<>();;
        
        this.jsonObjectValue = new JsonObject();
        this.jsonArrayValue = new JsonArray();
    }
    
}
