-- Users
INSERT INTO Users (name, email, password, role) VALUES ('Admin', 'admin@test.com', '1234', 'ADMIN');

-- Movies
INSERT INTO Movies (title, genre, duration_minutes, rating, release_date)
VALUES ('Inception','Sci-Fi',148,'PG-13','2010-07-16');

-- Theaters
INSERT INTO Theaters (name, location, total_screens) VALUES ('City Cinema','Downtown',5);

-- Showtimes (movie_id=1, theater_id=1)
INSERT INTO Showtimes (movie_id, theater_id, screen_number, show_date, show_time)
VALUES (1, 1, 1, '2025-10-15', '18:30:00');
