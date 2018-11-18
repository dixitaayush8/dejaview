package dejaview;

public class Movie {
	
	private String movieTitle;
	private String genre;
	private String actors;
	private String director;
	private int duration;
	private double rating;
	private int year;
	
	public Movie(String movieTitle, String genre, String actors, String director, int duration, double rating, int year) {
		this.movieTitle = movieTitle;
		this.genre = genre;
		this.actors = actors;
		this.director = director;
	}

}
