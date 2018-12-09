
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

/*
 * Class to run the DejaView Database
 */

public class DejaViewConsole {
	
	
	public static void mainMenuPrompt() {
		System.out.println("Hello! If you are running this for the first time, please register or use the credientials found in user.txt file\n**Note** During registration you may pass the keyword 'admin' when the system prompts for optional keyword\nEntering admin here will give you admin or administrator access and allow you to execute more advanced features\nThank you\nCoded with love by Team Trifecta\n ------------ Begin ------------");
		System.out.println("Main Menu");
		System.out.println("1. Login");
		System.out.println("2. Register");
		System.out.println("3. Quit");
		System.out.print("Enter choice: ");
	}

	public static void main(String[] args) throws SQLException {
		User dj = new User("root", "password", "localhost", 3306, "DEJAVIEW");

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
				if (dj.getIsAdmin() == true) {
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
					else if (optionChoice == 12)
						dj.getMoviesHigherThanAverageByGenre(scan, conn);
					else if (optionChoice == 13)
						dj.getMoviesByGenre(scan, conn);
					else if (optionChoice == 14)
						dj.getMoviesByDirector(scan, conn);
					else if (optionChoice == 15)
						dj.getMoviesByRating(scan, conn);
					else if (optionChoice == 16)
						dj.getMoviesByReleaseYear(scan, conn);
					else if (optionChoice == 17)
						dj.getMoviesByCinemaName(scan, conn);
					else if (optionChoice == 18)
						dj.getReviewsOfMovieByUsers(scan, conn);
					else if (optionChoice == 19)
						dj.getStartEndTimeForMovie(scan, conn);
					else if (optionChoice == 20)
						dj.getStartEndTimeForAllMovies(scan, conn);
					else if (optionChoice == 21)
						dj.buyTicket(scan, conn);
					else if (optionChoice == 1000)
						dj.archiveMoviesByDate(scan, conn);
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
					else
						System.out.println("Please enter a valid choice");
					if (dj.getIsAdmin() == true) {
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
	
	
	/////////////////
	
//	public static void mainMenuPrompt() {
//		System.out.println("Main Menu");
//		System.out.println("1. Login");
//		System.out.println("2. Register");
//		System.out.println("3. Quit");
//		System.out.print("Enter choice: ");
//	}
//	
//	
//	public static void main(String[] args) throws SQLException {
//		DejaView dj = new DejaView("root", "password", "localhost", 3306, "DEJAVIEW");
//		Connection conn = null;
//		try {
//			conn = dj.getConnection();
//			System.out.println("Connected to database");
//		} catch (SQLException e) {
//			System.out.println("ERROR: Could not connect to the database");
//			e.printStackTrace();
//			return;
//		}
//		
//		System.out.println("Welcome to DejaView: A Movie Ticket Reservation System!");
//		Scanner scan = new Scanner(System.in);
//		mainMenuPrompt();
//		while(scan.hasNextInt()) {
//			int choice = scan.nextInt();
//			scan.nextLine(); //consume \n
//			if(choice == 1) {
//				boolean login = true;
//				while(login == false) {
//					login = dj.login(scan, conn);
//				}
////				if(dj.getIsAdmin() == true) {
////					dj.adminChoices();
////				}
//				dj.adminChoices();
//				dj.userchoices();
//				int optionChoice;
//				while(scan.hasNextInt()) {
//					optionChoice = scan.nextInt();
//					scan.nextLine();
//					if(optionChoice == 0) {
//						System.out.println("Goodbye!");
//						System.exit(0);
//					}
//					// ADD THE NEW METHOD IN HERE
//					else if(optionChoice == 1)
//						dj.rateMovie(scan, conn);
//					else if(optionChoice == 2)
//						dj.getMoviesByTitle(scan, conn);
//					else if(optionChoice == 3)
//						dj.viewTickets(scan, conn);
//					else if(optionChoice == 4)
//                        dj.findNumMoviesOfEachDirector(conn);
//                    else if(optionChoice == 5)
//                        dj.findHighRatedMovies(conn);
//                    else if(optionChoice == 6)
//                        dj.viewTicketByMovieTitle(scan, conn);
//                    else if(optionChoice == 7)
//                        dj.seeAllShowings(conn);
//                    else if(optionChoice == 8)
//                        dj.seeCinemasWithAboveAverageMovies(conn);
//                    else if(optionChoice == 9)
//                        dj.filterMoviesBasedOnTwoActors(scan, conn);
//                    else if(optionChoice == 10)
//                        dj.seeOtherUsersReviews(scan, conn);
//                    else if(optionChoice == 11)
//                        dj.findMoviesGreaterThanUserThreshold(scan, conn);
//					else if(optionChoice == 13)
//						dj.getMoviesHigherThanAverageByGenre(scan, conn);
//					else if(optionChoice == 16)
//						dj.getMoviesByGenre(scan, conn);
//					else if(optionChoice == 17)
//						dj.getMoviesByDirector(scan, conn);
//					else if(optionChoice == 18)
//						dj.getMoviesByRating(scan, conn);
//					else if(optionChoice == 19)
//						dj.getMoviesByReleaseYear(scan, conn);
//					else if(optionChoice == 20)
//						dj.getMoviesByCinemaName(scan, conn);
//					else if(optionChoice == 21)
//						dj.getReviewsOfMovieByUsers(scan, conn);
//					else if(optionChoice == 22)
//						dj.getStartEndTimeForMovie(scan, conn);
//					else if(optionChoice == 23)
//						dj.getStartEndTimeForAllMovies(scan, conn);
//					else if(optionChoice == 199)
//						dj.archiveMoviesByDate(scan, conn);
//					else if (optionChoice == 1000)
//						dj.archiveMoviesByDate(scan, conn);					
//					else if(optionChoice == 1001)
//						dj.viewUsersTickets(scan, conn);
//					else if(optionChoice == 1002)
//						dj.addNewMovieToTheater(scan, conn);
//					else if(optionChoice == 1003)
//						dj.deleteMovie(scan, conn);
//					else if(optionChoice == 1004)
//                        dj.adminSeeReviews(scan, conn);
//                    else if(optionChoice == 1005)
//                        dj.adminSeeAllCinemas(scan, conn);
//                    else if(optionChoice == 1006)
//                        dj.adminSeeMinMaxRatings(scan, conn);
//                    else if(optionChoice == 1007)
//                        dj.adminSeeTicketsSold(scan, conn);
//                    else if(optionChoice == 1008)
//                        dj.adminAddTheater(scan, conn);
//                    else if(optionChoice == 1009)
//                        dj.adminRemoveTheater(scan, conn);
//                    else if(optionChoice == 1010)
//                        dj.adminUpdateMovie(scan, conn);
//					else
//						System.out.println("Please enter a valid choice");
//					if(dj.getIsAdmin() == true) {
//						dj.adminChoices();
//					}
//					dj.userchoices();
//				}
//			}
//			else if(choice == 2) { // register
//				int register = dj.register(scan, conn);
//				if(register == 1) {
//					System.out.println("Successfully Registered! Please log in");
//				}
//				else {
//					System.out.println("Could not register. Please log in");
//				}
//			}
//			else if(choice == 3){
//				System.out.println("Goodbye!");
//				System.exit(0);
//			}
//			else {
//				System.out.println("Please enter an appropriate value");
//			}
//			mainMenuPrompt();
//		}
//		
//	}
}
