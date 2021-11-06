package com.grpcflix.aggregator.service;

import com.dailyjava.grpcflix.common.Genre;
import com.dailyjava.grpcflix.movie.MovieSearchRequest;
import com.dailyjava.grpcflix.movie.MovieSearchResponse;
import com.dailyjava.grpcflix.movie.MovieServiceGrpc;
import com.dailyjava.grpcflix.user.UserGenreUpdateRequest;
import com.dailyjava.grpcflix.user.UserResponse;
import com.dailyjava.grpcflix.user.UserSearchRequest;
import com.dailyjava.grpcflix.user.UserServiceGrpc;
import com.grpcflix.aggregator.dto.RecommendedMovie;
import com.grpcflix.aggregator.dto.UserGenre;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMovieService {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userStub;

    @GrpcClient("movie-service")
    private MovieServiceGrpc.MovieServiceBlockingStub movieStub;

    public List<RecommendedMovie> getUserMovieSuggestions(String loginId){
        UserSearchRequest userSearchRequest = UserSearchRequest.newBuilder().setLoginId(loginId).build();
        UserResponse userGenre = userStub.getUserGenre(userSearchRequest);

        MovieSearchRequest searchRequest = MovieSearchRequest.newBuilder().setGenre(userGenre.getGenre()).build();
        MovieSearchResponse movies = movieStub.getMovies(searchRequest);

        List<RecommendedMovie> recommendedMovies = movies.getMovieList().stream()
                .map(movieDto -> new RecommendedMovie(movieDto.getTitle(), movieDto.getYear(), movieDto.getRating()))
                .collect(Collectors.toList());

        return recommendedMovies;
    }

    public void setUserGenre(UserGenre userGenre){

        UserGenreUpdateRequest userGenreUpdateRequest = UserGenreUpdateRequest.newBuilder()
                .setLoginId(userGenre.getLoginId())
                .setGenre(Genre.valueOf(userGenre.getGenre().toUpperCase()))
                .build();

        UserResponse userResponse = userStub.updateUserGenre(userGenreUpdateRequest);

        System.out.println(userResponse);

    }
}
