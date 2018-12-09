
import java.sql.*;
import java.sql.Date;
import java.util.*;

/* 
 * DejaView Class that hold common methods and encapsulates the Database connection
 */

public class DejaView {

	private String userName;
	private String password;
	private String serverName;
	private int portNumber;
	private String dbName;
	private boolean isAdmin;
	private String userEmail;
	private String userPassword;
	private int userID;

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
			conn = DriverManager.getConnection(
					"jdbc:mysql://" + this.serverName + ":" + this.portNumber + "/" + this.dbName, connectionProps);
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
		return conn;
	}

	public boolean getIsAdmin() {
		return this.isAdmin;
	}

	public void isAdmin(Connection conn) throws SQLException {
		String query = "SELECT isAdmin FROM USER WHERE email=? and password=?";
		PreparedStatement st = conn.prepareStatement(query);
		st.setString(1, this.getUserEmail());
		st.setString(2, this.userPassword);
		ResultSet rs = st.executeQuery();
		rs.next();
		if (rs.getBoolean(1) == true) {
			this.isAdmin = true;
		} else {
			this.isAdmin = false;
		}
	}

	/*
	 * Login
	 */
	public boolean login(Scanner scan, Connection conn) throws SQLException {
		boolean success = false;
		System.out.print("Please enter your email: ");
		this.setUserEmail(scan.next());
		System.out.print("Please enter your password: ");
		this.userPassword = scan.next();

		String query = "SELECT * FROM USER where email=? and password=?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, this.getUserEmail());
		ps.setString(2, this.userPassword);
		ResultSet rs = ps.executeQuery();
		if (!rs.next()) {
			System.out.println("Login Failed! Please try again");
		} else {
			String rsEmail = rs.getString("email");
			String rsPass = rs.getString("password");
			if (this.getUserEmail().equals(rsEmail) && this.userPassword.equals(rsPass)) {
				System.out.print("\nWelcome " + rs.getString("firstName") + "!");
				isAdmin(conn);
				if (this.isAdmin == true) {
					System.out.println(" You have admin privileges");
				}
				this.setUserID(rs.getInt("userID"));
			}

			success = true;

		}
		return success;
	}

	/*
	 * Register method
	 */
	public int register(Scanner scan, Connection conn) throws SQLException {
		String query = "INSERT INTO USER VALUES(?,?,?,?,?,?,?)";
		String tableCount = "SELECT COUNT(*) FROM USER";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(tableCount);
		int count = 0;
		while (rs.next()) {
			count = rs.getInt(1) + 1;
		}
		System.out.print("Please enter your First Name: ");
		String firstName = scan.next();
		System.out.print("Please enter your Last Name: ");
		String lastName = scan.next();
		System.out.print("Please enter your email address: ");
		String email = scan.next();
		System.out.print("Please enter your password: ");
		String password = scan.next();
		System.out.print("Please enter optional keyword: ");
		String admin = scan.next();

		PreparedStatement ps = conn.prepareStatement(query);
		ps.setInt(1, count);
		ps.setString(2, firstName);
		ps.setString(3, lastName);
		ps.setString(4, email);
		ps.setString(5, password);
		if (admin.equals("admin")) {
			ps.setBoolean(6, true);
		} else {
			ps.setBoolean(6, false);
		}
		ps.setInt(7, 0);

		return ps.executeUpdate();
	}

	public void seeAllShowings(Connection conn) throws SQLException {
		String query = "SELECT Cinema.cinemaName, Theater.theaterID, Movie.movieTitle, Theater.startTime, Theater.endTime, Theater.tickets FROM Cinema LEFT OUTER JOIN Theater USING (cinemaID) LEFT OUTER JOIN Movie USING (movieID)";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		String theater = "";
		String k = "";
		String l = "";
		String m = "";
		while (rs.next()) {
			if (rs.getInt("theaterID") != 0) {
				theater = Integer.toString(rs.getInt("theaterID"));
			} else {
				theater = "No theaters entered";
			}
			if (rs.getString("movieTitle") != null && !rs.getString("movieTitle").equals("")) {
				k = rs.getString("movieTitle");

			} else {
				k = "No movies entered";
			}
			if (rs.getTime("startTime") != null) {
				l = rs.getTime("startTime").toString();
			} else {
				l = "No start times entered";
			}
			if (rs.getTime("endTime") != null) {
				m = rs.getTime("endTime").toString();
			} else {
				m = "No end times entered";
			}
			System.out.println("\n" + "Cinema: " + rs.getString("cinemaName") + "\n" + "Theater: " + theater + "\n"
					+ "Movie: " + k + "\n" + "Start Time: " + l + "\n" + "End Time: " + m + "\n" + "Tickets: "
					+ rs.getInt("tickets"));
		}
	}
	public void getMovies(Connection conn) throws SQLException {
		String query = "select movieID, movieTitle from Movie";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		System.out.println("Here are the available movies\n");
		System.out.println("MovieID\t\tMovie Title");
		while (rs.next()) {
			System.out.println(rs.getInt("movieID") + "\t" + rs.getString("movieTitle"));
		}
		System.out.println("");
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}
}
