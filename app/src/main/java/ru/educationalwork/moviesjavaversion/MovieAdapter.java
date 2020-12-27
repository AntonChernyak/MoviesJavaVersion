package ru.educationalwork.moviesjavaversion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ru.educationalwork.moviesjavaversion.data.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private ArrayList<Movie> movies;

    // Конструктор
    public ArrayList<Movie> getMovies() {
        return movies;
    }

    // Сеттер
    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    // Геттер
    public MovieAdapter() {
        movies = new ArrayList<>();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        /* В этом списке только imageView. Сюда нужно сунуть постер. В JSON только часть пути к постреру (poster_path).
           В документации в Getting Started находим, что полный путь: https://image.tmdb.org/t/p/w500/poster_path,
           где до w500 --- базовый url, а сам w500 --- размер картинки

           Размеры картинки смотрим в пункте Configuration (https://developers.themoviedb.org/3/configuration/get-api-configuration)
           в "Try it out" после ввода ключа API в Variables. Отсюда интересует poster_sizes. Для малых изображений возьмем w185, для крупных w780.

           Всё это проделываем сразу при конвертации JSON в объект в классе JSONUtils.
        */

        Movie movie = movies.get(position);
        // Picasso
        Picasso.get()
                .load(movie.getPosterPath())
                .into(holder.imageViewSmallPoster);

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    // Метод для добавления новых фильмов при прокрутке списка
    public void addMovies(ArrayList<Movie> movies) {
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    /**
     * ViewHolder
     */
    static class MovieViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageViewSmallPoster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSmallPoster = itemView.findViewById(R.id.imageViewSmallPoster);
        }

    }
}
