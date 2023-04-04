INSERT INTO genres (name)
VALUES ('Комедия'),
	   ('Драма'),
	   ('Мультфильм'),
	   ('Триллер'),
	   ('Документальный'),
	   ('Боевик');

INSERT INTO mpa (name)
VALUES ('G'),
	   ('PG'),
	   ('PG-13'),
	   ('R'),
	   ('NC-17');

MERGE INTO event_types KEY (id) VALUES (1,'LIKE');
MERGE INTO event_types KEY (id) VALUES (2,'REVIEW');
MERGE INTO event_types KEY (id) VALUES (3,'FRIEND');

MERGE INTO operations KEY (id) VALUES (1,'REMOVE');
MERGE INTO operations KEY (id) VALUES (2,'ADD');
MERGE INTO operations KEY (id) VALUES (3,'UPDATE');
