package dejaview;

import java.sql.*;
import java.util.*;

public class DejaView {
	
//	
//	/** The name of the MySQL account to use (or empty for anonymous) */
//	private final String userName = "root";
//
//	/** The password for the MySQL account (or empty for anonymous) */
//	private final String password = "password";
//
//	/** The name of the computer running MySQL */
//	private final String serverName = "localhost";
//
//	/** The port of the MySQL server (default is 3306) */
//	private final int portNumber = 3306;
//
//	/** The name of the database we are testing with (this default is installed with MySQL) */
//	private final String dbName = "DEJAVIEW";
//	
//	/** The name of the table we are testing with */
//	private final String tableName = "MOVIE";
	
	
	private String userName;
	private String password;
	private String serverName;
	private int portNumber;
	private String dbName;
	
	public DejaView(String userName, String password, String serverName, int portNumber, String dbName) {
		this.userName = userName;
		this.password = password;
		this.serverName = serverName;
		this.portNumber = portNumber;
		this.dbName = dbName;
	}
	/**
	 * Get a new database connection
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", this.userName);
		connectionProps.put("password", this.password);
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://"
					+ this.serverName + ":" + this.portNumber + "/" + this.dbName,
					connectionProps);
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
		return conn;
	}
	
	public ResultSet login(String email, String password, Connection conn) throws SQLException {
		String query = "SELECT email, password FROM USER where email=? and password=?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, email);
		ps.setString(2, password);
		ResultSet rs = ps.executeQuery();
		
		return rs;
	}
	
	public static void main(String[] args) {
		DejaView dj = new DejaView("root", "password", "localhost", 3306, "DEJAVIEW");
		Connection conn = null;
		try {
			conn = dj.getConnection();
			System.out.println("Connected to database");
		} catch (SQLException e) {
			System.out.println("ERROR: Could not connect to the database");
			e.printStackTrace();
			return;
		}
		System.out.println("Welcome to DejaView: A Movie Ticket Reservation System!");

		
		Scanner scan = new Scanner(System.in);
		System.out.println("Do you have an account? [y/n]");
		boolean success = false;
		while(!scan.hasNext("[yn]")) {
			System.out.println("Please enter [y] for yes or [n] for no");
			scan.next();
		}
		String choice = scan.next();
		if(choice.equals("y")) {
			System.out.print("Please enter your email: ");
			String email = scan.next();
			System.out.print("Please enter your password: ");
			String password = scan.next();
			try {
				ResultSet rs = dj.login(email, password, conn);
				if(!rs.next()) {
					System.out.println("User does not exist");
				}
				else {
					System.out.println("email: " + rs.getString("email"));
					System.out.println("password: " + rs.getString("password"));
				}
			} catch (SQLException e) {
				System.out.println("Something went wrong!");
				e.printStackTrace();
			}
		} else {
			System.out.println("Registration still in progress..");
		}
		
	}

}