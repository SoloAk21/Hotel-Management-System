/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.hotelmanagement.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DBConnector {
        
    private final static DBConnector instance = new DBConnector(); 
    

    public static DBConnector getInstance() {
        return instance;
    }
    
    private Connection conn = null;


    public Connection getConnection() {
        return conn;
    }

    public Connection createConnection(String url, String user, String password, String database) throws SQLException{ 
        if (conn != null) conn.close();
        try {
            Properties connectionProps = new Properties();
            connectionProps.put("user", user);
            connectionProps.put("password", password); 
            connectionProps.put("database", database); 
            conn = DriverManager.getConnection(url + database, connectionProps);   
        } catch(SQLException ex) {
            return null;
        } 
        return conn;
    }
}
