package com.magicutils.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class DatabaseAccess {

	private interface ConstructorFunciton{
		<T> T construct(ResultSet rs);
	}
	
	public static boolean executeQuerry(DatabaseInfo di, String querry) throws SQLException {
		Connection conn = di.getConnection();
		PreparedStatement statement = conn.prepareStatement(querry);
		return statement.execute();
	}
	
	public static ResultSet getData(DatabaseInfo di, String querry) throws SQLException {
		Connection conn = di.getConnection();
		Statement stmt = conn.createStatement();
		return stmt.executeQuery(querry);
	}
	
	public static <T> Set<T> getDataAsSet(DatabaseInfo di, String querry, ConstructorFunciton f) throws SQLException {
		ResultSet rs = getData(di, querry);
		Set<T> out = new HashSet<T>();
		while(rs.next()) {
			T t = f.construct(rs);
			out.add(t);
		}
		return out;
	}
	
	public static boolean testConnection(DatabaseInfo di) {
	    try {
	      if (di.getConnection() != null) {
	        return true;
	      }

	    } catch (SQLException ex) {
	    	return false;
	    }
	    return false;
	}
	
	public static DatabaseInfo getDatabaseInfo(String url, String name, String pass) {
		return new DatabaseInfo(url, name, pass);
	}
	
}
