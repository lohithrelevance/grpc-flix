DROP TABLE IF EXISTS movie;
CREATE TABLE movie as SELECT * from CSVREAD('classpath:movie.csv')