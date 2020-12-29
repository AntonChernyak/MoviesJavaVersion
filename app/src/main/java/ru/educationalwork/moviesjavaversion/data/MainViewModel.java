package ru.educationalwork.moviesjavaversion.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class MainViewModel extends AndroidViewModel {

    private static MovieDatabase database;
    private final LiveData<List<Movie>> movies;

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(getApplication());
        movies = database.movieDao().getAllMovies();
    }

    // Методы для доступа к данным
    public Movie getMovieById(int id) {
        try {
            return new GetMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class GetMovieTask extends AsyncTask<Integer, Void, Movie> {
        @Override
        protected Movie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.movieDao().getMovieById(integers[0]);
            }
            return null;
        }
    }

    // Удаление элементов
    public void deleteAllMovies() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                database.movieDao().deleteAllMovies();
            }
        });
    }

    // Вставка элементов
    public void insertMovie(final Movie insMovie) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                database.movieDao().insertMovie(insMovie);
            }
        });
    }

    // Удаление одного элемента из БД
    public void deleteMovie(final Movie delMovie) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                database.movieDao().deleteMovie(delMovie);
            }
        });
    }

}