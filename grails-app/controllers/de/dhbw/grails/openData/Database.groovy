package de.dhbw.grails.openData

import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.nio.file.WatchService
import java.sql.DriverManager
import java.sql.SQLException

import com.mysql.jdbc.Connection

/**
 * @author Raffaela F., Benny R.
 */
// made this one a singleton
@Singleton
class Database {
	private Connection con
	final String driverClass = "com.mysql.jdbc.Driver"

	private static String database_schema = ""
	private static String database_path = ""
	private static String database_username = ""
	private static String database_password = ""

	public static void setDatabaseSchema(String database_schema) {
		Database.database_schema = database_schema
		println "schema: " + database_schema
	}

	public static void setDatabasePath(String database_path) {
		Database.database_path = database_path
		println "path: " + database_path
	}

	public static void setDatabaseUsername(String database_username) {
		Database.database_username = database_username
		println "username: " + database_username
	}

	public static void setDatabasePassword(String database_password) {
		Database.database_password = database_password
	}

	public Connection getConnection() {
		if(con==null) {
			buildConnection()
		}
		if(con==null) {
			log.error "getConnection() Connection still null"
			throw new RuntimeException("No sql connection")
		}
		return con
	}

	public void rebuildConnection() {
		this.closeConnection()
		this.buildConnection()
	}

	private void buildConnection() {
		con = null

		try {
			Class.forName(driverClass)
		} catch (ClassNotFoundException e) {
			log.error "Driver not found ", e
			return
		}

		try{
			String connector = "jdbc:mysql://" + database_path + "_" + database_schema
			log.info "DB Trying to connect to: "+ connector
			con = DriverManager.getConnection(connector , database_username, database_password)
		} catch (SQLException e) {
			log.error "SQL Exception trying to get Connection ", e
		}
	}

	private void closeConnection() {
		if (con != null) {
			try {
				con.close()
			} catch (SQLException e) {
				log.error "Error trying to close connection", e
			}
		}
	}
}