package com.magicutils.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseInfo{
	
	private String url;
	private String user;
	private String pass;
			
	public DatabaseInfo(String url, String name, String pass){
		this.url = url;
		this.user = name;
		this.pass = pass;
	}
	
	public Connection getConnection() throws SQLException {
		return (this.user != null && pass != null ? DriverManager.getConnection(url, user, pass) : DriverManager.getConnection(url));
	}
}