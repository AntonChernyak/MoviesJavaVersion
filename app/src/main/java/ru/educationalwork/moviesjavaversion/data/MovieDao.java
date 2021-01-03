package ru.educationalwork.moviesjavaversion.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MovieDao {
    // Для Movie
    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> getAllMovies();

    @Query("SELECT * FROM movies WHERE id == :movieId")
    Movie getMovieById(int movieId);

    @Query("DELETE FROM movies")
    void deleteAllMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie movie);

    // Для FavouriteMovie
    @Query("SELECT * FROM favourite_movies")
    LiveData<List<FavouriteMovie>> getAllFavouriteMovies();

    @Query("SELECT * FROM favourite_movies WHERE id == :movieId")
    FavouriteMovie getFavouriteMovieById(int movieId);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavouriteMovie(FavouriteMovie movie);

    @Delete
    void deleteFavouriteMovie(FavouriteMovie movie);

}