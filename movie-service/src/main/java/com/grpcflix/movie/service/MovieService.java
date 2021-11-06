package com.grpcflix.movie.service;


import com.dailyjava.grpcflix.movie.MovieDto;
import com.dailyjava.grpcflix.movie.MovieSearchRequest;
import com.dailyjava.grpcflix.movie.MovieSearchResponse;
import com.dailyjava.grpcflix.movie.MovieServiceGrpc;
import com.grpcflix.movie.repository.MovieRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class MovieService extends MovieServiceGrpc.MovieServiceImplBase {

    @Autowired
    private MovieRepository repository;

    @Override
    public void getMovies(MovieSearchRequest request, StreamObserver<MovieSearchResponse> responseObserver) {

        List<MovieDto> movieDtos = repository.getMovieByGenreOrderByYearDesc(request.getGenre().toString())
                .stream().map(movie -> MovieDto.newBuilder()
                        .setTitle(movie.getTitle())
                        .setYear(movie.getYear())
                        .setRating(movie.getRating())
                        .build())
                .collect(Collectors.toList());

        MovieSearchResponse response = MovieSearchResponse.newBuilder().addAllMovie(movieDtos).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
