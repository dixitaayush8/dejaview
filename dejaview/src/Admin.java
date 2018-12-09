import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.Scanner;

/*
 * Administrator directly extends the DejaView class allow for commonality 
 * This class holds all admin related methods
 */
public class Admin extends DejaView{

	public Admin(String userName, String password, String serverName, int portNumber, String dbName) {
		super(userName, password, serverName, portNumber, dbName);
		// TODO Auto-generated constructor stub
	}
	

	public void adminChoices() {
		System.out.println("");
		System.out.println("--------------- Admin Operations ---------------");
		System.out.println("1000. Archive Movies by Date");
		System.out.println("1001. See all users that have tickets");
		System.out.println("1002. Add a new movie to a theater");
		System.out.println("1003. Delete a movie");
		System.out.println("1004. See how many reviews each user made");
		System.out.println("1005. See all cinemas & their theaters' movie(s)/time(s)");
		System.out.println("1006. See all users and their min & max rating(s)");
		System.out.println("1007. See all tickets that have been sold per movie.");
		System.out.println("1008. Add a theater.");
		System.out.println("1009. Remove a theater.");
		System.out.println("1010. Update a movie attribute.");
		System.out.println("--------------- Admin Operations ---------------");
	}
	
	// ---------------------------------------------------------- ADMIN METHODS ---------------------------------------------------------- //


	public void archiveMoviesByDate(Scanner scan, Connection conn) throws SQLException {
		String query = "call archiveMoviesBefore(?)";
		PreparedStatement ps = conn.prepareStatement(query);
		System.out.println("Please enter the date [YYYY:MM:DD]: ");
		String date = scan.nextLine();
		ps.setString(1, date);
		ResultSet rs = ps.executeQuery();
		System.out.println("Success, here is a list of all of the movies archived before " + date + ":");
		String query_two = "SELECT * FROM ARCHIVED_MOVIES";
		Statement sTwo = conn.createStatement();
		ResultSet rsTwo = sTwo.executeQuery(query_two);
		while (rsTwo.next())
			System.out.println("\n" + "movieID: " + rsTwo.getInt("movieID") + "\n" + "movieTitle: "
					+ rsTwo.getString("movieTitle") + "\n" + "genre: " + rsTwo.getString("genre") + "\n" + "actors: "
					+ rsTwo.getString("actors") + "\n" + "director: " + rsTwo.getString("director") + "\n"
					+ "duration: " + rsTwo.getInt("duration") + "\n" + "rating: " + rsTwo.getDouble("rating") + "\n"
					+ "releaseYear: " + rsTwo.getInt("releaseYear") + "\n" + "updated on: "
					+ rsTwo.getDate("updated_on"));
	}

	public void adminSeeReviews(Scanner scan, Connection conn) throws SQLException {
		String query = "SELECT User.userID, User.firstName, User.lastName, User.email,\n"
				+ "count(Rating.ratingID) FROM User LEFT OUTER JOIN Rating USING\n" + "(userID) GROUP BY userID";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		System.out.println("Here are all of the users and how many reviews they have made\n");
		while (rs.next()) {
			System.out.println("userID: " + rs.getInt("userID") + "\nfirstName: " + rs.getString("firstName")
					+ "\nlastName: " + rs.getString("lastName") + "\nemail: " + rs.getString("email") + "\ncount: "
					+ rs.getInt("count(Rating.ratingID)") + "\n");
		}
	}

	// query #8
	public void adminSeeAllCinemas(Scanner scan, Connection conn) throws SQLException {
		String query = "SELECT Cinema.cinemaName, Theater.theaterID, Movie.movieTitle,\n"
				+ "Theater.startTime, Theater.endTime FROM Cinema LEFT OUTER JOIN\n"
				+ "Theater USING (cinemaID) LEFT OUTER JOIN Movie USING (movieID)";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		System.out.println(
				"Here are all of the cinemas and their theaters along with " + "movies being played at the theaters");
		while (rs.next()) {
			System.out.println("cinemaName: " + rs.getString("cinemaName") + "\ntheaterID: " + rs.getInt("theaterID")
					+ "\nmovieTitle: " + rs.getString("movieTitle") + "\nstartTime: " + rs.getString("startTime")
					+ "\nendTime: " + rs.getString("endTime") + "\n");
		}
	}

	// query #10
	public void adminSeeMinMaxRatings(Scanner scan, Connection conn) throws SQLException {
		String query = "SELECT User.userID, User.firstName, User.lastName, User.email,\n"
				+ "MIN(Rating.ratingNumber), MAX(Rating.ratingNumber),\n"
				+ "COUNT(Rating.ratingID) AS NumRatings FROM User, Rating WHERE\n"
				+ "User.userID=Rating.userID GROUP BY userID;";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		System.out.println("Here are all of the users and their minimum and maximum ratings.");

		while (rs.next()) {
			System.out.println("userID: " + rs.getInt("userID") + "\nfirstName: " + rs.getString("firstName")
					+ "\nlastName: " + rs.getString("lastName") + "\nemail: " + rs.getString("email") + "\nminRating: "
					+ rs.getInt("MIN(Rating.ratingNumber)") + "\nmaxRating: " + rs.getInt("MAX(Rating.ratingNumber)")
					+ "\ncount: " + rs.getInt("NumRatings") + "\n");
		}
	}

	// query #24
	public void adminSeeTicketsSold(Scanner scan, Connection conn) throws SQLException {
		String query = "SELECT COUNT(ticketID), Movie.movieTitle FROM Ticket, Movie WHERE\n"
				+ "Ticket.movieID=Movie.movieID GROUP BY Movie.movieID;";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		System.out.println("Here are the number of tickets that have been sold for each movie.");
		while (rs.next()) {
			System.out.println("Number of tickets: " + rs.getInt("COUNT(ticketID)") + "\nmovieTitle: "
					+ rs.getString("movieTitle") + "\n");
		}
	}

	// query #27
	public void adminAddTheater(Scanner scan, Connection conn) throws SQLException {
		String sInput;
		int iInput;
		String query = "INSERT INTO theater(theaterID, cinemaID, movieID, startTime,\n"
				+ "endTime, tickets) VALUES(?, ?, ?, ?, ?, ?)";
		PreparedStatement ps = conn.prepareStatement(query);
		System.out.print("Please enter the theater ID: ");
		iInput = scan.nextInt();
		scan.nextLine();
		ps.setInt(1, iInput);
		String query_one = "SELECT * FROM CINEMA";
		Statement s = conn.createStatement();
		ResultSet rsOne = s.executeQuery(query_one);
		while (rsOne.next())
			System.out.println("\n" + "Cinema ID: " + rsOne.getInt("cinemaID") + "\n" + "Cinema Name: "
					+ rsOne.getString("cinemaName"));
		System.out.print("Please enter the cinema ID: ");
		iInput = scan.nextInt();
		scan.nextLine();
		ps.setInt(2, iInput);
		String query_two = "SELECT * FROM MOVIE";
		Statement sTwo = conn.createStatement();
		ResultSet rsTwo = sTwo.executeQuery(query_two);
		while (rsTwo.next())
			System.out.println("\n" + "Movie ID: " + rsTwo.getInt("movieID") + "\n" + "Movie Name: "
					+ rsTwo.getString("movieTitle"));
		System.out.print("Please enter the movie ID: ");
		iInput = scan.nextInt();
		scan.nextLine();
		ps.setInt(3, iInput);
		System.out.println("Enter the start time[hh:mm:ss]: ");
		String st = scan.next();
		System.out.println("Enter the end time[hh:mm:ss]: ");
		String et = scan.next();
		Time start = Time.valueOf(st);
		Time end = Time.valueOf(et);
		ps.setTime(4, start);
		ps.setTime(5, end);
		System.out.println("Enter the number of tickets for this theater: ");
		iInput = scan.nextInt();
		scan.nextLine();
		ps.setInt(6, iInput);
		int result = ps.executeUpdate();
		if (result == 1) {
			System.out.println("Successfully added Movie to Theater");
			seeAllShowings(conn);
		} else {
			System.out.println("Unable to add movie to Theater");
		}

	}

	// query #28
	public void adminRemoveTheater(Scanner scan, Connection conn) throws SQLException {
		String query = "DELETE FROM theater WHERE theaterID=? AND movieID=?";
		PreparedStatement ps = conn.prepareStatement(query);
		String query_one = "SELECT * FROM THEATER";
		Statement s = conn.createStatement();
		ResultSet rsOne = s.executeQuery(query_one);
		while (rsOne.next())
			System.out.println("\n" + "Theater ID: " + rsOne.getInt("theaterID") + "\n" + "Cinema ID: "
					+ rsOne.getInt("cinemaID") + "\n" + "Movie ID: " + rsOne.getInt("movieID") + "\n" + "Cinema ID: "
					+ rsOne.getInt("cinemaID") + "\n" + "Start Time: " + rsOne.getTime("startTime") + "\n"
					+ "End Time: " + rsOne.getTime("endTime") + "\n" + "Tickets: " + rsOne.getInt("tickets"));
		System.out.print("Enter the ID of the theater that you would like to delete: ");
		int theaterID = scan.nextInt();
		String query_two = "SELECT Movie.movieID, Movie.movieTitle FROM MOVIE, THEATER WHERE Movie.movieID=Theater.movieID AND Theater.theaterID= '"
				+ theaterID + "'";
		Statement sTwo = conn.createStatement();
		ResultSet rsTwo = sTwo.executeQuery(query_two);
		while (rsTwo.next())
			System.out.println("\n" + "Movie ID: " + rsTwo.getInt("movieID") + "\n" + "Movie Name: "
					+ rsTwo.getString("movieTitle"));
		System.out.print("Enter the ID of the movie associated with the theater: ");
		int movieID = scan.nextInt();
		ps.setInt(1, theaterID);
		ps.setInt(2, movieID);
		int result = ps.executeUpdate();
		if (result == 1) {
			System.out.println("Successfully deleted " + theaterID);
			System.out.println("Updated Theater table: ");
			Statement sThree = conn.createStatement();
			ResultSet rsThree = sThree.executeQuery(query_one);
			while (rsThree.next())
				System.out.println("\n" + "Theater ID: " + rsThree.getInt("theaterID") + "\n" + "Cinema ID: "
						+ rsThree.getInt("cinemaID") + "\n" + "Movie ID: " + rsThree.getInt("movieID") + "\n"
						+ "Cinema ID: " + rsThree.getInt("cinemaID") + "\n" + "Start Time: "
						+ rsThree.getTime("startTime") + "\n" + "End Time: " + rsThree.getTime("endTime") + "\n"
						+ "Tickets: " + rsThree.getInt("tickets"));
		} else {
			System.out.println("Something went wrong");
		}
	}

	// query #29
	public void adminUpdateMovie(Scanner scan, Connection conn) throws SQLException {
		String query_two = "SELECT * FROM MOVIE";
		Statement sTwo = conn.createStatement();
		ResultSet rsTwo = sTwo.executeQuery(query_two);
		while (rsTwo.next())
			System.out.println("\n" + "movieID: " + rsTwo.getInt("movieID") + "\n" + "movieTitle: "
					+ rsTwo.getString("movieTitle") + "\n" + "genre: " + rsTwo.getString("genre") + "\n" + "actors: "
					+ rsTwo.getString("actors") + "\n" + "director: " + rsTwo.getString("director") + "\n"
					+ "duration: " + rsTwo.getInt("duration") + "\n" + "rating: " + rsTwo.getDouble("rating") + "\n"
					+ "releaseYear: " + rsTwo.getInt("releaseYear"));
		System.out.println("Enter the movie ID for the movie to update: ");
		int ID = scan.nextInt();
		scan.nextLine();
		System.out.println("Enter the movie attribute that you would like to modify: ");
		String attr = scan.nextLine();
		String query = "UPDATE movie SET " + attr + " = ? where movieID = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setInt(2, ID);
		System.out.println("Enter the new value : ");
		if (attr.equals("duration") || attr.equals("releaseYear")) {
			int iInput = scan.nextInt();
			scan.nextLine();
			ps.setInt(1, iInput);
		} else if (attr.equals("movieTitle") || attr.equals("genre") || attr.equals("actors")
				|| attr.equals("director")) {
			attr = scan.nextLine();
			ps.setString(1, attr);
		} else if (attr.equals("rating")) {
			double dInput = scan.nextDouble();
			scan.nextLine();
			ps.setDouble(1, dInput);
		} else if (attr.equals("updated_on")) {
			String str = scan.next();
			Date nDate = Date.valueOf(str);
			ps.setDate(1, nDate);
		}
		System.out.println(ps.toString());
		int result = ps.executeUpdate();
		if (result == 1) {
			System.out.println("Successfully updated " + ID);
			String query_three = "SELECT * FROM MOVIE";
			Statement sThree = conn.createStatement();
			ResultSet rsThree = sThree.executeQuery(query_three);
			while (rsThree.next())
				System.out.println("\n" + "movieID: " + rsThree.getInt("movieID") + "\n" + "movieTitle: "
						+ rsThree.getString("movieTitle") + "\n" + "genre: " + rsThree.getString("genre") + "\n"
						+ "actors: " + rsThree.getString("actors") + "\n" + "director: " + rsThree.getString("director")
						+ "\n" + "duration: " + rsThree.getInt("duration") + "\n" + "rating: "
						+ rsThree.getDouble("rating") + "\n" + "releaseYear: " + rsThree.getInt("releaseYear"));

		} else {
			System.out.println("Something went wrong");
		}
	}

	public void deleteMovie(Scanner scan, Connection conn) throws SQLException {
		String query = "Delete from Movie where movieTitle=?";
		PreparedStatement ps = conn.prepareStatement(query);
		getMovies(conn);
		System.out.print("Enter the title of the movie you would like to delete: ");
		String title = scan.nextLine();
		ps.setString(1, title);
		int result = ps.executeUpdate();
		if (result == 1) {
			System.out.println("Successfully deleted " + title);
		} else {
			System.out.println("Something went wrong");
		}

	}

	public void addNewMovieToTheater(Scanner scan, Connection conn) throws SQLException {
		String query = "INSERT INTO theater VALUES(?, ?, ?, ?, ?, ?)";
		PreparedStatement ps = conn.prepareStatement(query);
		System.out.println("Enter the theaterID: ");
		int theaterID = scan.nextInt();
		ps.setInt(1, theaterID);
		String query_one = "SELECT * FROM CINEMA";
		Statement s = conn.createStatement();
		ResultSet rsOne = s.executeQuery(query_one);
		while (rsOne.next())
			System.out.println("\n" + "Cinema ID: " + rsOne.getInt("cinemaID") + "\n" + "Cinema Name: "
					+ rsOne.getString("cinemaName"));
		System.out.println("Enter the cinemaID: ");
		int cinemaID = scan.nextInt();
		ps.setInt(2, cinemaID);
		String query_two = "SELECT Movie.movieID, Movie.movieTitle FROM MOVIE, THEATER WHERE Movie.movieID=Theater.movieID AND Theater.theaterID= '"
				+ theaterID + "'";
		Statement sTwo = conn.createStatement();
		ResultSet rsTwo = sTwo.executeQuery(query_two);
		while (rsTwo.next())
			System.out.println("\n" + "Movie ID: " + rsTwo.getInt("movieID") + "\n" + "Movie Name: "
					+ rsTwo.getString("movieTitle"));
		System.out.print("Enter the ID of the movie associated with the theater (CANNOT be any Movie ID from above): ");
		System.out.println("Enter the movieID: ");
		int movieID = scan.nextInt();
		ps.setInt(3, movieID);
		System.out.println("Enter the start time[hh:mm:ss]: ");
		String st = scan.next();
		System.out.println("Enter the end time[hh:mm:ss]: ");
		String et = scan.next();
		Time start = Time.valueOf(st);
		Time end = Time.valueOf(et);
		System.out.println("Enter the number of tickets: ");
		int tickets = scan.nextInt();
		ps.setTime(4, start);
		ps.setTime(5, end);
		ps.setInt(6, tickets);
		int result = ps.executeUpdate();
		if (result == 1) {
			System.out.println("Successfully added Movie to Theater");
		} else {
			System.out.println("Unable to add movie to Theater");
		}
	}

}
