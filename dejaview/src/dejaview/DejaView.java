package dejaview;

import java.sql.*;
import java.sql.Date;
import java.util.*;

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
		st.setString(1, this.userEmail);
		st.setString(2, this.userPassword);
		ResultSet rs = st.executeQuery();
		rs.next();
		if (rs.getBoolean(1) == true) {
			this.isAdmin = true;
		} else {
			this.isAdmin = false;
		}
	}

	public boolean login(Scanner scan, Connection conn) throws SQLException {
		boolean success = false;
		System.out.print("Please enter your email: ");
		this.userEmail = scan.next();
		System.out.print("Please enter your password: ");
		this.userPassword = scan.next();

		String query = "SELECT * FROM USER where email=? and password=?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, this.userEmail);
		ps.setString(2, this.userPassword);
		ResultSet rs = ps.executeQuery();
		if (!rs.next()) {
			System.out.println("Login Failed! Please try again");
		} else {
			String rsEmail = rs.getString("email");
			String rsPass = rs.getString("password");
			if (this.userEmail.equals(rsEmail) && this.userPassword.equals(rsPass)) {
				System.out.print("\nWelcome " + rs.getString("firstName") + "!");
				isAdmin(conn);
				if (this.isAdmin == true) {
					System.out.println(" You have admin privileges");
				}
				this.userID = rs.getInt("userID");
			}

			success = true;

		}
		return success;
	}

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

	// USER CHOICES AND METHODS

	public void userchoices() {
		System.out.println("");
		System.out.println("--------------- User Operations ---------------");
		System.out.println("0. Exit program");
		System.out.println("1. Rate a movie");
		System.out.println("2. Search for movie by title");
		System.out.println("3. View your tickets");
		System.out.println("4: View number of movies by each director");
		System.out.println("5: Find above average (high rated) movies");
		System.out.println("6: View ticket for a movie by movie title");
		System.out.println("7: See all cinemas and their showings (if they have any)");
		System.out.println("8: See all cinemas and their showings for above average (high rated) movies");
		System.out.println("9: Filter movies based on two actors");
		System.out.println("10: See what other users have reviewed");
		System.out.println("11: See movies that have greater than a set number of reviews");
		System.out.println("13. Search movies by genre where ratings are higher than average ratings");
		System.out.println("16. Search movie by genre");
		System.out.println("17. Search movie by director");
		System.out.println("18. Search movie by rating number");
		System.out.println("19. Search movie by release year");
		System.out.println("20. Search movies playing in a cinema");
		System.out.println("21. See all movie reviews for a movie");
		System.out.println("22. Check start-time and end-time for a movie");
		System.out.println("23. Check start-time and end-time for ALL movies");
		System.out.println("24. Buy ticket");
		System.out.println("--------------- User Operations ---------------");
		System.out.println("");

		System.out.print("Select an operation: ");
	}

	///////////////////////////////
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

	public void findMoviesGreaterThanUserThreshold(Scanner scan, Connection conn) throws SQLException {
		String query = "SELECT t.count, Movie.movieID, movieTitle, genre, actors, director, duration, rating, releaseYear  from Movie,(select count(*) as count, movieID FROM Rating GROUP BY movieID HAVING count > ?) t WHERE Movie.movieID=t.movieID;";
		PreparedStatement ps = conn.prepareStatement(query);
		System.out.print(
				"Enter number of reviews threshold (e.g. if you put 0, all movies with the number of reviews greater than 0 will be displayed): ");
		String count = scan.nextLine();
		ps.setString(1, count);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			System.out.println("\n" + "Number of Reviews: " + rs.getString("count") + "\n" + "Movie ID: "
					+ rs.getInt("movieID") + "\n" + "Movie Title: " + rs.getString("movieTitle") + "\n" + "genre: "
					+ rs.getString(4) + "\n" + "actors: " + rs.getString(5) + "\n" + "director: " + rs.getString(6)
					+ "\n" + "duration: " + rs.getInt(7) + " minutes" + "\n" + "rating: " + rs.getDouble(8) + "/10"
					+ "\n" + "releaseYear: " + rs.getInt(9));
		}
	}

	public void seeCinemasWithAboveAverageMovies(Connection conn) throws SQLException {
		String query = "SELECT Cinema.cinemaName, Theater.theaterID, Movie.movieTitle, Movie.rating, Theater.startTime, Theater.endTime, Theater.tickets FROM Cinema LEFT OUTER JOIN Theater USING (cinemaID) LEFT OUTER JOIN Movie USING (movieID) WHERE Movie.rating > (select avg(rating) FROM Movie)";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		while (rs.next()) {
			System.out.println("\n" + "Cinema: " + rs.getString("cinemaName") + "\n" + "Theater ID: "
					+ rs.getInt("theaterID") + "\n" + "Movie Title: " + rs.getString("movieTitle") + "\n"
					+ "Movie Rating: " + rs.getInt("rating") + "\n" + "Start Time: " + rs.getTime("startTime") + "\n"
					+ "End Time: " + rs.getTime("endTime") + "\n" + "Tickets: " + rs.getInt("tickets"));
		}
	}

	public void filterMoviesBasedOnTwoActors(Scanner scan, Connection conn) throws SQLException {
		System.out.print("Name the first actor (e.g. Tom Hanks): ");
		String firstActor = scan.nextLine();
		System.out.print("Name the second actor (e.g. Liam Neeson): ");
		String secondActor = scan.nextLine();
		String firstActorWithPercentages = "%" + firstActor + "%";
		String secondActorWithPercentages = "%" + secondActor + "%";
		String query = "SELECT * FROM Movie WHERE actors LIKE " + "'" + firstActorWithPercentages + "'"
				+ " UNION SELECT * FROM Movie WHERE actors LIKE " + "'" + secondActorWithPercentages + "'";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		while (rs.next()) {
			System.out.println("\n" + "Movie ID: " + rs.getInt("movieID") + "\n" + "Movie Title: "
					+ rs.getString("movieTitle") + "\n" + "Genres: " + rs.getString("genre") + "\n" + "Actors: "
					+ rs.getString("actors") + "\n" + "Director: " + rs.getString("director") + "\n" + "Duration: "
					+ rs.getInt("duration") + "\n" + "Rating: " + rs.getDouble("rating") + "\n" + "Release Year: "
					+ rs.getInt("releaseYear"));
		}
	}

	public void seeOtherUsersReviews(Scanner scan, Connection conn) throws SQLException {
		String query = "SELECT User.email, User.firstName, User.lastName, Rating.ratingNumber, Movie.movieTitle FROM User LEFT OUTER JOIN Rating USING (userID) LEFT OUTER JOIN Movie USING (movieID) WHERE User.email=?";
		PreparedStatement ps = conn.prepareStatement(query);
		System.out.print("Please enter the user you want to see reviews of: ");
		String user = scan.nextLine();
		ps.setString(1, user);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			System.out.println("\n" + "Email: " + rs.getString("email") + "\n" + "First Name: "
					+ rs.getString("firstName") + "\n" + "Last Name: " + rs.getString("lastName") + "\n" + "Rating: "
					+ rs.getInt("ratingNumber") + "\n" + "Movie title: " + rs.getString("movieTitle"));
		}
	}

	public void findNumMoviesOfEachDirector(Connection conn) throws SQLException {
		String query = "SELECT count(*), director FROM Movie GROUP BY director";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		System.out.println("Here are the number of movies for each director:\n");
		System.out.println("Number of Movies\t\tDirector");
		while (rs.next()) {
			System.out.println(rs.getInt("count(*)") + "\t" + rs.getString("director"));
		}
		System.out.println("");
	}

	public void findHighRatedMovies(Connection conn) throws SQLException {
		String query = "SELECT * FROM Movie WHERE rating > (select avg(rating) FROM Movie)";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		while (rs.next()) {
			System.out.println("\n" + "MovieID: " + rs.getInt("movieID") + "\n" + "Movie Title: "
					+ rs.getString("movieTitle") + "\n" + "Genres: " + rs.getString("genre") + "\n" + "Actors: "
					+ rs.getString("actors") + "\n" + "Director: " + rs.getString("director") + "\n" + "Duration: "
					+ rs.getInt("duration") + "\n" + "Rating: " + rs.getDouble("rating") + "\n" + "Release Year: "
					+ rs.getInt("releaseYear") + "\n" + "Release Date: " + rs.getDate("updated_on"));
		}
	}

	public void viewTicketByMovieTitle(Scanner scan, Connection conn) throws SQLException {
		String query_one = "SELECT Movie.movieTitle FROM Movie, Ticket WHERE Movie.movieID = Ticket.movieID";
		Statement t = conn.createStatement();
		System.out.print("Enter movie title: ");
		String movieTitle = scan.nextLine();
		ResultSet rsOne = t.executeQuery(query_one);
		while (rsOne.next()) {
			if (movieTitle.equals(rsOne.getString("movieTitle"))) {
				String query = "SELECT User.userID, User.firstName, User.lastName, User.email, Movie.movieTitle, Movie.movieID, Ticket.ticketID, Theater.startTime,Theater.endTime, Cinema.cinemaName FROM TICKET INNER JOIN USER ON User.userID=ticket.userID INNER JOIN MOVIE ON Movie.movieID = ticket.movieID INNER JOIN CINEMA ON Cinema.cinemaID=ticket.cinemaID INNER JOIN THEATER ON Theater.theaterID=ticket.theaterID AND Theater.movieID=ticket.movieID AND User.email ="
						+ "'" + userEmail + "'" + " AND Movie.movieTitle =" + "'" + movieTitle + "'";
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(query);
				while (rs.next()) {
					System.out.println("\n" + "UserID: " + rs.getInt("userID") + "\n" + "First Name: "
							+ rs.getString("firstName") + "\n" + "Last Name: " + rs.getString("lastName") + "\n"
							+ "Email: " + rs.getString("email") + "\n" + "Movie Title: " + rs.getString("movieTitle")
							+ "\n" + "Movie ID: " + rs.getString("movieID") + "\n" + "Ticket ID: "
							+ rs.getInt("ticketID") + "\n" + "Start Time: " + rs.getTime("startTime") + "\n"
							+ "End Time: " + rs.getTime("endTime") + "\n" + "Cinema Name: "
							+ rs.getString("cinemaName"));
				}
				break;
			}
		}

	}

	public void buyTicket(Scanner scan, Connection conn) throws SQLException {
		System.out.println("All cinemas with movies: ");
		seeAllShowings(conn);
		System.out.print("Enter the cinema NAME you want to see the movie in: ");
		String cinemaName = scan.nextLine();
		String firstQuery = "SELECT Cinema.cinemaID FROM Cinema WHERE cinemaName=?";
		PreparedStatement st = conn.prepareStatement(firstQuery);
		st.setString(1, cinemaName);
		ResultSet rs = st.executeQuery();
		rs.next();
		int cinemaID = rs.getInt("cinemaID");
		System.out.print("Enter the movie title you want to see: ");
		String movieTitle = scan.nextLine();
		String secondQuery = "SELECT Movie.movieID FROM Movie WHERE movieTitle=?";
		PreparedStatement stOne = conn.prepareStatement(secondQuery);
		stOne.setString(1, movieTitle);
		ResultSet rsOne = stOne.executeQuery();
		rsOne.next();
		int movieID = rsOne.getInt("movieID");
		String thirdQuery = "SELECT theaterID FROM Theater WHERE cinemaID=? AND movieID=?";
		PreparedStatement stTwo = conn.prepareStatement(thirdQuery);
		stTwo.setInt(1, cinemaID);
		stTwo.setInt(2, movieID);
		ResultSet rsTwo = stTwo.executeQuery();
		rsTwo.next();
		int theaterID = rsTwo.getInt("theaterID");
		String fourthQuery = "INSERT INTO TICKET(userID,movieID,cinemaID,theaterID) VALUES (?,?,?,?)";
		PreparedStatement stThree = conn.prepareStatement(fourthQuery);
		stThree.setInt(1, userID);
		stThree.setInt(2, movieID);
		stThree.setInt(3, cinemaID);
		stThree.setInt(4, theaterID);
		stThree.executeUpdate();
		System.out.println("Ticket for " + movieTitle + " at " + cinemaName + " has been successfully added!");

	}

	////////////////////////////////

	public void viewUsersTickets(Scanner scan, Connection conn) throws SQLException {
		String query = "SELECT  User.userID, User.firstName, User.lastName, User.email, User.numTickets, Movie.movieTitle, Movie.movieID, Ticket.ticketID, Theater.startTime,Theater.endTime, Cinema.cinemaName FROM TICKET INNER JOIN USER ON User.userID=ticket.userID INNER JOIN MOVIE ON Movie.movieID = ticket.movieID INNER JOIN CINEMA ON Cinema.cinemaID=ticket.cinemaID INNER JOIN THEATER ON Theater.theaterID=ticket.theaterID AND Theater.movieID=ticket.movieID";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		System.out.println("All tickets held by users");
		while (rs.next()) {
			System.out.println("");
			System.out.println("UserID: " + rs.getInt(1));
			System.out.println("First Name: " + rs.getString(2));
			System.out.println("Last Name: " + rs.getString(3));
			System.out.println("Email: " + rs.getString(4));
			System.out.println("Number of Tickets: " + rs.getInt(5));
			System.out.println("Movie Title: " + rs.getString(6));
			System.out.println("MovieID: " + rs.getInt(7));
			System.out.println("TicketID: " + rs.getInt(8));
			System.out.println("Start Time: " + rs.getTime(9));
			System.out.println("End Time: " + rs.getTime(10));
			System.out.println("Cinema Name: " + rs.getString(11));
		}
	}

	public void viewTickets(Scanner scan, Connection conn) throws SQLException {
		String query = "SELECT  User.userID, User.firstName, User.lastName, User.email, Movie.movieTitle, Movie.movieID, Ticket.ticketID, Theater.startTime,Theater.endTime, Cinema.cinemaName FROM TICKET INNER JOIN USER ON User.userID=ticket.userID INNER JOIN MOVIE ON Movie.movieID = ticket.movieID INNER JOIN CINEMA ON Cinema.cinemaID=ticket.cinemaID INNER JOIN THEATER ON Theater.theaterID=ticket.theaterID AND Theater.movieID=ticket.movieID AND User.email=?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, this.userEmail);
		ResultSet rs = ps.executeQuery();
		System.out.println("");
		System.out.println("You ticket(s)");
		System.out.println(
				"userID\tfirstName\tlastName\temail\tmovieTitle\tmovieID\tticketID\tstartTime\tendTime\tcinemaName");
		int count = 0;
		while (rs.next()) {
			count++;
			System.out.println(rs.getInt(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t" + rs.getString(4)
					+ "\t" + rs.getString(5) + "\t" + rs.getInt(6) + "\t" + rs.getInt(7) + "\t" + rs.getTime(8) + "\t"
					+ rs.getTime(9) + "\t" + rs.getString(10) + "\t");
		}
		System.out.println("You have a total of " + count + " ticket(s)");
	}

	public void getMoviesByTitle(Scanner scan, Connection conn) throws SQLException {
		String query = "select * from movie where movieTitle=?";
		PreparedStatement ps = conn.prepareStatement(query);
		System.out.print("Please enter the title: ");
		String title = scan.nextLine();
		ps.setString(1, title);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			count++;
			System.out.println("\n" + "movieID: " + rs.getInt(1) + "\n" + "movieTitle: " + rs.getString(2) + "\n"
					+ "genre: " + rs.getString(3) + "\n" + "actors: " + rs.getString(4) + "\n" + "director: "
					+ rs.getString(5) + "\n" + "duration: " + rs.getInt(6) + " minutes" + "\n" + "rating: "
					+ rs.getDouble(7) + "/10" + "\n" + "releaseYear: " + rs.getInt(8));
		}
		if (count == 0) {
			System.out.println("Sorry that movie doesn't exist");
		}
	}

	public void getMoviesByGenre(Scanner scan, Connection conn) throws SQLException {
		String query = "select * from movie where genre like ? ORDER BY updated_on DESC";
		PreparedStatement ps = conn.prepareStatement(query);
		System.out.print("Please enter the genre you would like to search: ");
		String genre = scan.next();
		ps.setString(1, "%" + genre + "%");
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			count++;
			System.out.println("\n" + "movieID: " + rs.getInt(1) + "\n" + "movieTitle: " + rs.getString(2) + "\n"
					+ "genre: " + rs.getString(3) + "\n" + "actors: " + rs.getString(4) + "\n" + "director: "
					+ rs.getString(5) + "\n" + "duration: " + rs.getInt(6) + " minutes" + "\n" + "rating: "
					+ rs.getDouble(7) + "/10" + "\n" + "releaseYear: " + rs.getInt(8));
		}
		if (count == 0) {
			System.out.println("Sorry movies of that genre don't exist");
		}
	}

	public void getMoviesByDirector(Scanner scan, Connection conn) throws SQLException {
		String query = "SELECT * FROM Movie WHERE director = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		System.out.print("Please enter the directors name: ");
		String dName = scan.nextLine();
		ps.setString(1, dName);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			count++;
			System.out.println("\n" + "movieID: " + rs.getInt(1) + "\n" + "movieTitle: " + rs.getString(2) + "\n"
					+ "genre: " + rs.getString(3) + "\n" + "actors: " + rs.getString(4) + "\n" + "director: "
					+ rs.getString(5) + "\n" + "duration: " + rs.getInt(6) + " minutes" + "\n" + "rating: "
					+ rs.getDouble(7) + "/10" + "\n" + "releaseYear: " + rs.getInt(8));
		}
		if (count == 0) {
			System.out.println("Sorry movies by that director don't exist");
		}
	}

	public void getMoviesByRating(Scanner scan, Connection conn) throws SQLException {
		String query = "SELECT * FROM Movie WHERE Movie.rating = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		System.out.print("Please enter the rating to search by (1-10): ");
		int rating = scan.nextInt();
		ps.setInt(1, rating);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			count++;
			System.out.println("\n" + "movieID: " + rs.getInt(1) + "\n" + "movieTitle: " + rs.getString(2) + "\n"
					+ "genre: " + rs.getString(3) + "\n" + "actors: " + rs.getString(4) + "\n" + "director: "
					+ rs.getString(5) + "\n" + "duration: " + rs.getInt(6) + " minutes" + "\n" + "rating: "
					+ rs.getDouble(7) + "/10" + "\n" + "releaseYear: " + rs.getInt(8));
		}
		if (count == 0) {
			System.out.println("Sorry movies of that rating don't exist");
		}
	}

	public void getMoviesByReleaseYear(Scanner scan, Connection conn) throws SQLException {
		String query = "SELECT * FROM movie WHERE releaseYear = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		System.out.print("Please enter release year: ");
		int year = scan.nextInt();
		ps.setInt(1, year);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			count++;
			System.out.println("\n" + "movieID: " + rs.getInt(1) + "\n" + "movieTitle: " + rs.getString(2) + "\n"
					+ "genre: " + rs.getString(3) + "\n" + "actors: " + rs.getString(4) + "\n" + "director: "
					+ rs.getString(5) + "\n" + "duration: " + rs.getInt(6) + " minutes" + "\n" + "rating: "
					+ rs.getDouble(7) + "/10" + "\n" + "releaseYear: " + rs.getInt(8));
		}
		if (count == 0) {
			System.out.println("Sorry movies of that release year don't exist");
		}
	}

	public void getMoviesByCinemaName(Scanner scan, Connection conn) throws SQLException {
		String query = "Select Movie.movieTitle,Movie.movieTitle,Movie.genre,Movie.actors,Movie.director,Movie.duration,Movie.rating,Movie.releaseYear,Theater.cinemaID, Cinema.cinemaName FROM Movie,Theater,Cinema WHERE Movie.movieID = Theater.movieID and Theater.cinemaID = Cinema.cinemaID AND Cinema.cinemaName =?";
		PreparedStatement ps = conn.prepareStatement(query);
		System.out.print("Please enter cinema Name: ");
		String cinemaName = scan.nextLine();
		ps.setString(1, cinemaName);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			count++;
			System.out.println("\n" + "movieTitle: " + rs.getString(2) + "\n" + "genre: " + rs.getString(3) + "\n"
					+ "actors: " + rs.getString(4) + "\n" + "director: " + rs.getString(5) + "\n" + "duration: "
					+ rs.getInt(6) + " minutes" + "\n" + "rating: " + rs.getDouble(7) + "/10" + "\n" + "releaseYear: "
					+ rs.getInt(8));
		}
		if (count == 0) {
			System.out.println("Sorry there are no movies playing in that cinema");
		}
	}

	public void getReviewsOfMovieByUsers(Scanner scan, Connection conn) throws SQLException {
		String query = "SELECT Movie.movieTitle, Rating.ratingNumber, User.firstName, User.lastName FROM rating INNER JOIN user USING(userID) INNER JOIN movie USING (movieID) where movie.movieTitle = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		System.out.print("Please enter the movie title: ");
		String movie = scan.nextLine();
		ps.setString(1, movie);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			count++;
			System.out.println("\n" + "movieTitle: " + rs.getString(1) + "\n" + "Rating given: " + rs.getInt(2) + "\n"
					+ "First Name: " + rs.getString(3) + "\n" + "Last Name: " + rs.getString(4));
		}
		if (count == 0) {
			System.out.println("Empty");
		}

	}

	public void getStartEndTimeForMovie(Scanner scan, Connection conn) throws SQLException {
		String query = "SELECT Movie.movieTitle, Theater.theaterID, Theater.startTime, Theater.endTime FROM Movie INNER JOIN Theater USING (movieID) WHERE movieTitle = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		System.out.print("Please enter the movie title: ");
		String movieTitle = scan.nextLine();
		ps.setString(1, movieTitle);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			System.out.println("\n" + "movieTitle: " + rs.getString(1) + "\n" + "start-time: " + rs.getTime(2) + "\n"
					+ "end-time: " + rs.getTime(3));
		}
	}

	public void archiveMoviesByDate(Scanner scan, Connection conn) throws SQLException {
		String query = "call archiveMoviesBefore(?)";
		PreparedStatement ps = conn.prepareStatement(query);
		System.out.println("Please enter the date [YYYY:MM:DD]: ");
		String date = scan.nextLine();
		ps.setString(1, date);
		ResultSet rs = ps.executeQuery();
		System.out.println(rs.toString());
	}

	public void getStartEndTimeForAllMovies(Scanner scan, Connection conn) throws SQLException {
		String query = "SELECT Movie.movieTitle, Theater.theaterID, Theater.startTime, Theater.endTime FROM Movie LEFT OUTER JOIN Theater USING (movieID)";
		Statement ps = conn.createStatement();
		ResultSet rs = ps.executeQuery(query);
		String error = "No time avaialble";
		int count = 0;
		while (rs.next()) {
			count++;
			String theater = "";
			String start = "";
			String end = "";

			if (rs.getInt(2) == 0) {
				theater = "Not playing";
			} else {
				theater = Integer.toString(rs.getInt(2));
			}
			if (rs.getTime(3) != null) {
				start = rs.getTime(3).toString();
			} else {
				start = error;
			}
			if (rs.getTime(4) != null) {
				end = rs.getTime(4).toString();
			} else {
				end = error;
			}
			System.out.println("\n" + "movieTitle: " + rs.getString(1) + "\n" + "theater: " + theater + "\n"
					+ "start-time: " + start + "\n" + "end-time: " + end);
		}
	}

	public void getMoviesHigherThanAverageByGenre(Scanner scan, Connection conn) throws SQLException {
		String query = "SELECT * FROM Movie WHERE rating >= (select avg(rating) FROM Movie WHERE genre LIKE ?) and genre LIKE ?";
		PreparedStatement ps = conn.prepareStatement(query);
		System.out.print("Please enter the genre: ");
		String genre = scan.nextLine();
		ps.setString(1, "%" + genre + "%");
		ps.setString(2, "%" + genre + "%");
		ResultSet rs = ps.executeQuery();
		System.out.println("\n" + "Movies with higher than average ratings in genre: " + genre);
		while (rs.next()) {
			System.out.println("\n" + "movieID: " + rs.getInt(1) + "\n" + "movieTitle: " + rs.getString(2) + "\n"
					+ "genre: " + rs.getString(3) + "\n" + "actors: " + rs.getString(4) + "\n" + "director: "
					+ rs.getString(5) + "\n" + "duration: " + rs.getInt(6) + " minutes" + "\n" + "rating: "
					+ rs.getDouble(7) + "/10" + "\n" + "releaseYear: " + rs.getInt(8));
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

	public void rateMovie(Scanner scan, Connection conn) throws SQLException {
		String query = "INSERT INTO Rating (userID,movieID,ratingNumber) VALUES(?,?,?)";
		getMovies(conn);
		System.out.print("Please enter the movieID of the movie you would like to rate: ");
		int id = scan.nextInt();
		System.out.print("Please enter rating [1-10] you would like to give: ");
		int rating = scan.nextInt();
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setInt(1, this.userID);
		ps.setInt(2, id);
		ps.setInt(3, rating);
		ps.executeUpdate();
		System.out.println("Rating has been successfully added!");

	}

	// ADMIN CHOICES AND METHODS

	public void adminChoices() {
		System.out.println("");
		System.out.println("Admin Operations");
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
		System.out.println("Here are all of the tickets that have been sold for each movie.");
		while (rs.next()) {
			System.out.println(
					"ticketID: " + rs.getInt("ticketID") + "\nmovieTitle: " + rs.getString("movieTitle") + "\n");
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
		System.out.print("Please enter the cinema ID: ");
		iInput = scan.nextInt();
		scan.nextLine();
		ps.setInt(2, iInput);
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
		} else {
			System.out.println("Unable to add movie to Theater");
		}

	}

	// query #28
	public void adminRemoveTheater(Scanner scan, Connection conn) throws SQLException {
		String query = "DELETE FROM theater WHERE theaterID=? AND movieID=?";
		PreparedStatement ps = conn.prepareStatement(query);
		System.out.print("Enter the ID of the theater that you would like to delete: ");
		int theaterID = scan.nextInt();
		System.out.print("Enter the ID of the movie associated with the theater: ");
		int movieID = scan.nextInt();
		ps.setInt(1, theaterID);
		ps.setInt(2, movieID);
		int result = ps.executeUpdate();
		if (result == 1) {
			System.out.println("Successfully deleted " + theaterID);
		} else {
			System.out.println("Something went wrong");
		}
	}

	// query #29
	public void adminUpdateMovie(Scanner scan, Connection conn) throws SQLException {
		getMovies(conn);
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
		System.out.println("Enter the cinemaID: ");
		int cinemaID = scan.nextInt();
		ps.setInt(2, cinemaID);
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

	public static void mainMenuPrompt() {
		System.out.println("Main Menu");
		System.out.println("1. Login");
		System.out.println("2. Register");
		System.out.println("3. Quit");
		System.out.print("Enter choice: ");
	}

	public static void main(String[] args) throws SQLException {
		DejaView dj = new DejaView("user", "password", "localhost", 3306, "DEJAVIEW");
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
		mainMenuPrompt();
		while (scan.hasNextInt()) {
			int choice = scan.nextInt();
			scan.nextLine(); // consume \n
			if (choice == 1) {
				boolean login = false;
				while (login == false) {
					login = dj.login(scan, conn);
				}
				if (dj.isAdmin == true) {
					dj.adminChoices();
				}
				dj.userchoices();
				int optionChoice;
				while (scan.hasNextInt()) {
					optionChoice = scan.nextInt();
					scan.nextLine();
					if (optionChoice == 0) {
						System.out.println("Goodbye!");
						System.exit(0);
					} else if (optionChoice == 1)
						dj.rateMovie(scan, conn);
					else if (optionChoice == 2)
						dj.getMoviesByTitle(scan, conn);
					else if (optionChoice == 3)
						dj.viewTickets(scan, conn);
					else if (optionChoice == 4)
						dj.findNumMoviesOfEachDirector(conn);
					else if (optionChoice == 5)
						dj.findHighRatedMovies(conn);
					else if (optionChoice == 6)
						dj.viewTicketByMovieTitle(scan, conn);
					else if (optionChoice == 7)
						dj.seeAllShowings(conn);
					else if (optionChoice == 8)
						dj.seeCinemasWithAboveAverageMovies(conn);
					else if (optionChoice == 9)
						dj.filterMoviesBasedOnTwoActors(scan, conn);
					else if (optionChoice == 10)
						dj.seeOtherUsersReviews(scan, conn);
					else if (optionChoice == 11)
						dj.findMoviesGreaterThanUserThreshold(scan, conn);
					else if (optionChoice == 13)
						dj.getMoviesHigherThanAverageByGenre(scan, conn);
					else if (optionChoice == 16)
						dj.getMoviesByGenre(scan, conn);
					else if (optionChoice == 17)
						dj.getMoviesByDirector(scan, conn);
					else if (optionChoice == 18)
						dj.getMoviesByRating(scan, conn);
					else if (optionChoice == 19)
						dj.getMoviesByReleaseYear(scan, conn);
					else if (optionChoice == 20)
						dj.getMoviesByCinemaName(scan, conn);
					else if (optionChoice == 21)
						dj.getReviewsOfMovieByUsers(scan, conn);
					else if (optionChoice == 22)
						dj.getStartEndTimeForMovie(scan, conn);
					else if (optionChoice == 23)
						dj.getStartEndTimeForAllMovies(scan, conn);
					else if (optionChoice == 24)
						dj.buyTicket(scan, conn);
					else if (optionChoice == 1001)
						dj.viewUsersTickets(scan, conn);
					else if (optionChoice == 1002)
						dj.addNewMovieToTheater(scan, conn);
					else if (optionChoice == 1003)
						dj.deleteMovie(scan, conn);
					else if (optionChoice == 1004)
						dj.adminSeeReviews(scan, conn);
					else if (optionChoice == 1005)
						dj.seeAllShowings(conn);
					else if (optionChoice == 1006)
						dj.adminSeeMinMaxRatings(scan, conn);
					else if (optionChoice == 1007)
						dj.adminSeeTicketsSold(scan, conn);
					else if (optionChoice == 1008)
						dj.adminAddTheater(scan, conn);
					else if (optionChoice == 1009)
						dj.adminRemoveTheater(scan, conn);
					else if (optionChoice == 1010)
						dj.adminUpdateMovie(scan, conn);
					else if (optionChoice == 1000)
						dj.archiveMoviesByDate(scan, conn);
					else
						System.out.println("Please enter a valid choice");
					if (dj.isAdmin == true) {
						dj.adminChoices();
					}
					dj.userchoices();
				}
			} else if (choice == 2) { // register
				int register = dj.register(scan, conn);
				if (register == 1) {
					System.out.println("Successfully Registered! Please log in");
				} else {
					System.out.println("Could not register. Please log in");
				}
			} else if (choice == 3) {
				System.out.println("Goodbye!");
				System.exit(0);
			} else {
				System.out.println("Please enter an appropriate value");
			}
			mainMenuPrompt();
		}

	}

}