package util;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;



public class SQLUtil {
	
	private String driver = "com.mysql.jdbc.Driver";
//	private String driver = "acme.db.Driver";
	private String host;	
	private String database;
	private String user;
	private String password;
	private Connection cnn = null;
	
	/**
	 * Constructor
	 * @param db_property_path
	 */
	public SQLUtil(String db_property_path){
		
		Properties db_prop = new Properties();
		try {
			//Load DB properties from db.property
			db_prop.load(new FileInputStream(db_property_path));
			this.host = db_prop.getProperty("host");
			this.database = db_prop.getProperty("database");
			this.user = db_prop.getProperty("user");
			this.password = db_prop.getProperty("password");
			
			Class.forName(driver);
			
			this.cnn = getConnection();
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}			
	}
	
	
	
	/**
	 * Get connection to MySQL
	 * @return
	 */
	public Connection getConnection(){
		boolean flag = false;
		try {
	//		Connection conn = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + database,user,password);
			Connection conn = DriverManager.getConnection("jdbc:mysql://" + host + 
					"/" + database + "?useUnicode=true&characterEncoding=utf-8", user,
					password);
			if(!conn.isClosed()){
				return conn;
			}else{
				return null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Database access error occurs!");
			return null;
		}	
	}
	
	/**
	 * Close connection to the database
	 */
	public void close() {
		try {
			cnn.close();
		} catch (Exception e) {
			System.err.println("Error while closing the connection to database server: "
							+ e.getMessage());
		}
	}
	
	/**
	 * Get statement
	 * @return
	 */
	public Statement getStatement(){
		Statement stmt = null;
		try {
			if(this.cnn != null){
				stmt = this.cnn.createStatement();
				return stmt;
			}else{
				return null;
			}
			
		} catch (SQLException e) {
			System.err.println("create statement error! "+e);
			return null;
		}		
	}
	
	
	/**
	 * Create an SQL statement with parameters, which are updated later
	 * 
	 * @param sql
	 * @return
	 */
	public PreparedStatement createPreparedStatement(String sql) {
		try {
			return cnn.prepareStatement(sql);
		} catch (Exception e) {
			System.err.println("Error while creating a prepare statement: "
					+ e.getMessage());
		}
		return null;
	}


}
