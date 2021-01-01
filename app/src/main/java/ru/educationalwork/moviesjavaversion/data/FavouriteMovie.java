package ru.educationalwork.moviesjavaversion.data;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "favourite_movies")
public class FavouriteMovie extends Movie {

    public FavouriteMovie(int uniqueId, int id, int voteCount, String title, String originalTitle, String overview, String posterPath, String bigPosterPath, String backdropPath, double voteAverage, String releaseDate) {
        super(uniqueId, id, voteCount, title, originalTitle, overview, posterPath, bigPosterPath, backdropPath, voteAverage, releaseDate);
    }

    /*
    Конструктор для преобразования Movie в FavoriteMovie. Используется при клике для добавления в избранное.
    Т.к. этот класс для БД, то должен быть только 1 конструктор, поэтому этос с @Ignore
     */
    @Ignore
    public FavouriteMovie(Movie movie) {
        super(
                movie.getId(),
                movie.getVoteCount(),
                movie.getTitle(),
                movie.getOriginalTitle(),
                movie.getOverview(),
                movie.getPosterPath(),
                movie.getBigPosterPath(),
                movie.getBackdropPath(),
                movie.getVoteAverage(),
                movie.getReleaseDate());
    }
}