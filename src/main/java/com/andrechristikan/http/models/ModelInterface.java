/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.http.models;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Syn-User
 */
public interface ModelInterface {
    
    ArrayList<String> setColumns();
    
    Map<String, String> setColumnsName();
    
    Map<String, String> setColumnsType();
    
}
