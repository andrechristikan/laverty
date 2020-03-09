/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.http.models;

import com.andrechristikan.core.CoreModel;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Transaction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Syn-UserModel
 */
public class UserModel extends CoreModel implements ModelInterface{
    
    public UserModel(Vertx vertx, Transaction trans){
        super(vertx, trans);
        logger = LoggerFactory.getLogger(UserModel.class);

        tableName = "users";
        primaryKeyName = "id";
    }
    

    /*
        This is mandatory
        Set column from this function
    */
    @Override
    public ArrayList<String> setColumns(){

        ArrayList<String> columns = new ArrayList<>();
        columns.add("id");
        columns.add("role_id");
        columns.add("username");
        columns.add("password_hash");
        columns.add("salt");
        columns.add("email");
        columns.add("created_at");
        columns.add("updated_at");
        columns.add("last_login");

        return columns;
    }

    /*
        This is will be mandatory if you add some column
        Type of raw column must setted
        This count type must same with count of column
    */

    @Override
    public Map<String, String> setColumnsName(){

        Map<String, String> columnsName = new HashMap<>();
        columnsName.put("id","id");
        columnsName.put("role_id","role_id");
        columnsName.put("username","user_name");
        columnsName.put("password_hash","password_hash");
        columnsName.put("salt","salt");
        columnsName.put("email","email");
        columnsName.put("created_at","created_at");
        columnsName.put("updated_at","updated_at");
        columnsName.put("last_login","last_login");

        return columnsName;
    }


    /*
        This is mandatory
        Type of raw column must setted, or they will be string for default
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
    public Map<String, String> setColumnsType(){

        Map<String, String> columnsType = new HashMap<>();
        columnsType.put("id","uuid");
        columnsType.put("role_id","string");
        columnsType.put("username","string");
        columnsType.put("password_hash","string");
        columnsType.put("salt","string");
        columnsType.put("email","string");
        columnsType.put("created_at","timestamptz");
        columnsType.put("updated_at","timestamptz");
        columnsType.put("last_login","timestamptz");

        return columnsType;
    }
    

}
