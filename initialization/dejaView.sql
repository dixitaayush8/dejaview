set global local_infile = 1;
DROP DATABASE IF EXISTS DEJAVIEW;
CREATE DATABASE DEJAVIEW;
USE DEJAVIEW; 

DROP TABLE IF EXISTS MOVIE;
CREATE TABLE MOVIE
(
 movieID INT AUTO_INCREMENT,
 movieTitle VARCHAR(255),
 genre VARCHAR(255),
 actors VARCHAR(800),
 director VARCHAR(50),
 duration INT,
 rating DOUBLE(4,1), 
 releaseYear INT,
 updated_on DATE,
 CHECK (rating <= 10 and rating > 0),
 PRIMARY KEY (movieID)
);
ALTER table MOVIE AUTO_INCREMENT = 1;

DROP TABLE IF EXISTS USER;
CREATE TABLE USER
(
 userID INT AUTO_INCREMENT,
 firstName VARCHAR(50),
 lastName VARCHAR(50),
 email VARCHAR(255) UNIQUE,
 password VARCHAR(200),
 isAdmin BOOLEAN DEFAULT FALSE,
 numTickets INT DEFAULT 0,
 PRIMARY KEY (userID)
);
ALTER table USER AUTO_INCREMENT = 1;

DROP TABLE IF EXISTS RATING;
CREATE TABLE RATING
(
 ratingID INT AUTO_INCREMENT,
 userID INT,
 movieID INT,
 ratingNumber INT,
 PRIMARY KEY(ratingID),
 FOREIGN KEY(userID) REFERENCES User(userID) on delete cascade,
 FOREIGN KEY(movieID) REFERENCES Movie(movieID) on delete cascade  
); 
ALTER table RATING AUTO_INCREMENT = 1;

DROP TABLE IF EXISTS CINEMA;
CREATE TABLE CINEMA
(
 cinemaID INT AUTO_INCREMENT,
 cinemaName VARCHAR(50),
 PRIMARY KEY (cinemaID)
);
ALTER table CINEMA AUTO_INCREMENT = 1;

DROP TABLE IF EXISTS THEATER;
CREATE TABLE THEATER
(
 theaterID INT,
 cinemaID INT,
 movieID INT,
 startTime TIME,
 endTime TIME,
 tickets INT,
 CHECK (endTime > startTime),
 PRIMARY KEY (theaterID, movieID),
 FOREIGN KEY (cinemaID) REFERENCES Cinema(cinemaID) on delete cascade
);

DROP TABLE IF EXISTS TICKET;
CREATE TABLE TICKET
(
 ticketID INT AUTO_INCREMENT,
 userID INT,
 movieID INT,
 cinemaID INT,
 theaterID INT,
 PRIMARY KEY (ticketID),
 FOREIGN KEY (userID) REFERENCES User(userID) on delete cascade,
 FOREIGN KEY (movieID) REFERENCES Movie(movieID) on delete cascade,
 FOREIGN KEY (theaterID, movieID) REFERENCES Theater(theaterID, movieID) on delete cascade,
 FOREIGN KEY (cinemaID) REFERENCES Cinema(cinemaID) on delete cascade
);
ALTER table TICKET AUTO_INCREMENT = 1;

DROP TABLE IF EXISTS ARCHIVED_MOVIES;
CREATE TABLE ARCHIVED_MOVIES
(
 movieID INT AUTO_INCREMENT,
 movieTitle VARCHAR(255),
 genre VARCHAR(255),
 actors VARCHAR(800),
 director VARCHAR(50),
 duration INT,
 rating DOUBLE(4,1), 
 releaseYear INT,
 updated_on DATE,
 CHECK (rating <= 10 and rating > 0),
 PRIMARY KEY (movieID)
);
ALTER table ARCHIVED_MOVIES AUTO_INCREMENT = 1;

DROP PROCEDURE IF EXISTS archiveMoviesBefore;
DELIMITER //
CREATE PROCEDURE archiveMoviesBefore(IN thisDate date) 
BEGIN
INSERT INTO ARCHIVED_MOVIES 
(SELECT *
FROM MOVIE
WHERE thisDate > updated_on);
DELETE FROM MOVIE WHERE thisDate > updated_on;
END //
DELIMITER ;


DROP TRIGGER IF EXISTS updateTickets;
DELIMITER //
CREATE TRIGGER updateTickets
AFTER INSERT ON TICKET
FOR EACH ROW
BEGIN
	UPDATE USER SET numTickets = numTickets + 1 WHERE  userID = NEW.userID;
	UPDATE THEATER SET tickets = tickets - 1 WHERE theaterID = NEW.theaterID AND movieID = NEW.movieID;
END //
DELIMITER ;


DROP TRIGGER IF EXISTS updateRatings;
DELIMITER //
CREATE TRIGGER updateRatings
AFTER INSERT ON RATING
FOR EACH ROW
BEGIN
	UPDATE MOVIE SET rating = 
		(select avg(ratingNumber) averageRating
		 from Rating
		 WHERE movieID = NEW.movieID) where movieID = NEW.movieID;
END //
DELIMITER ;

LOAD DATA LOCAL INFILE 'movies.txt' INTO TABLE MOVIE;
LOAD DATA LOCAL INFILE 'users.txt' INTO TABLE USER;
LOAD DATA LOCAL INFILE 'ratings.txt' INTO TABLE RATING;
LOAD DATA LOCAL INFILE 'cinemas.txt' INTO TABLE CINEMA;
LOAD DATA LOCAL INFILE 'theaters.txt' INTO TABLE THEATER;
LOAD DATA LOCAL INFILE 'tickets.txt' INTO TABLE TICKET;


