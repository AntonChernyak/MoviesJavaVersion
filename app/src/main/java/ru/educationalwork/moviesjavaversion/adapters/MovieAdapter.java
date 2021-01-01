package ru.educationalwork.moviesjavaversion.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ru.educationalwork.moviesjavaversion.R;
import ru.educationalwork.moviesjavaversion.data.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private OnPosterClickLister onPosterClickLister;
    private OnReachEndListener onReachEndListener;

    public void setOnPosterClickLister(OnPosterClickLister onPosterClickLister) {
        this.onPosterClickLister = onPosterClickLister;
    }

    public void setOnReachEndListener(OnReachEndListener onReachEndListener) {
        this.onReachEndListener = onReachEndListener;
    }

    // Конструктор
    public List<Movie> getMovies() {
        return movies;
    }

    // Сеттер
    public void setMovies(List<Movie> movies) {
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

        // Пагинация
        if ((position == movies.size() - 4) && onReachEndListener != null) {
            onReachEndListener.onReachEnd();
        }

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
    public void addMovies(List<Movie> movies) {
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    /**
     * ViewHolder
     */
    class MovieViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageViewSmallPoster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSmallPoster = itemView.findViewById(R.id.imageViewSmallPoster);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPosterClickLister != null) {
                        onPosterClickLister.onPosterClick(getAdapterPosition());
                    }
                }
            });
        }

    }

    /**
     * Интерфейс для кликов на item
     */
    public interface OnPosterClickLister {
        void onPosterClick(int position);
    }

    /**
     * Интерфейс для пагинации
     */
    public interface OnReachEndListener {
        void onReachEnd();
    }

}
