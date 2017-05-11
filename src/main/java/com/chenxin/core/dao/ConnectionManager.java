package com.chenxin.core.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.chenxin.core.util.Config;

/**
 * DB Connection管理
 * @author j
 *
 */
public class ConnectionManager {
	
	private static Logger logger = Logger.getLogger(ConnectionManager.class);

	private static Connection connection;
	
	public static Connection getConnection(){
		//获取数据库连接
		try {
			if(connection==null||connection.isClosed()){
				connection = createConnection();
			}else{
				return connection;
			}
		} catch (SQLException e) {
			logger.error("SQLException", e);
		}
		return connection;
	}
	
	//加载驱动
	static{
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void close(){
		if(connection!=null){
			logger.info("关闭连接中！");
		}
		try {
			connection.close();
		} catch (SQLException e) {
			logger.error("SQLException", e);
		}
	}
	
	//获得数据库连接
	private static Connection createConnection() {
		String host = Config.dbHost;
		String user = Config.dbUsername;
		String password = Config.dbPassword;
		String dbName = Config.dbName;
		String url="jdbc:mysql://" + host + ":3306/" + dbName;
		connection = null;
		
		try {
			connection = DriverManager.getConnection(url,user,password);//建立mysql连接
			logger.debug("success!");
		} catch (SQLException e) {
			logger.error("SQLException",e);
		}
		
		return connection;
	}
	public static void main(String[] args) {
		getConnection();
		getConnection();
		close();
	}
}
