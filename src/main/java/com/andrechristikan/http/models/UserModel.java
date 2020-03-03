/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.http.models;

import com.andrechristikan.Model;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Transaction;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Syn-UserModel
 */
public class UserModel extends Model {
    
    public UserModel(Vertx vertx, Transaction trans){
        super(vertx, trans);
        this.logger = LoggerFactory.getLogger(UserModel.class);
    }
    
    /* 
        Set column from this function
    */ 
    @Override
    public void setColumns(){
        this.columns.add("id");
        this.columns.add("role_id");
        this.columns.add("username");
        this.columns.add("password");
        this.columns.add("email");
        this.columns.add("created_at");
        this.columns.add("updated_at");
        this.columns.add("last_login");
    }
    
    /* 
        Customizable
        This count type must same with count of column
    */ 
    @Override
    public void setColumnsName(){
        this.columnsName.put("id","user_id");
        this.columnsName.put("role_id","role_id");
        this.columnsName.put("username","user_name");
        this.columnsName.put("password","password");
        this.columnsName.put("email","email_user");
        this.columnsName.put("created_at","created_at");
        this.columnsName.put("updated_at","updated_at");
        this.columnsName.put("last_login","last_login");
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
    @Override
    public void setColumnsType(){
        this.columnsType.put("id","uuid");
        this.columnsType.put("role_id","string");
        this.columnsType.put("username","string");
        this.columnsType.put("password","string");
        this.columnsType.put("email","string");
        this.columnsType.put("created_at","timestamptz");
        this.columnsType.put("updated_at","timestamptz");
        this.columnsType.put("last_login","timestamptz");
    }
    
    /* 
        Table name in database
    */ 
    @Override
    public void setTableName(){
        this.tableName = "users";
    }
    
    /* 
        Reference from response.json in resources/messages folder
    */ 
    @Override
    public void setService(){
        this.service = "user";
    }

}
