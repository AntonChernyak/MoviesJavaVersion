package ru.educationalwork.moviesjavaversion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import ru.educationalwork.moviesjavaversion.adapters.MovieAdapter;
import ru.educationalwork.moviesjavaversion.data.FavouriteMovie;
import ru.educationalwork.moviesjavaversion.data.MainViewModel;
import ru.educationalwork.moviesjavaversion.data.Movie;

import static ru.educationalwork.moviesjavaversion.DetailActivity.INTENT_KEY_ID;

public class FavoriteActivity extends AppCompatActivity {

    private MovieAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        RecyclerView recyclerViewFavouriteMovies = findViewById(R.id.recyclerViewFavouriteMovies);
        recyclerViewFavouriteMovies.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new MovieAdapter();
        recyclerViewFavouriteMovies.setAdapter(adapter);

        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        LiveData<List<FavouriteMovie>> favouriteMovies = viewModel.getFavouriteMovies();

        favouriteMovies.observe(this, new Observer<List<FavouriteMovie>>() {
            @Override
            public void onChanged(List<FavouriteMovie> favouriteMovies) {
                // Дополнительный список, т.к. мы не можем в список с объектами родительского класса вставить объекты дочернего класса.
                if (favouriteMovies != null) {
                    List<Movie> movies = new ArrayList<Movie>(favouriteMovies);
                    adapter.setMovies(movies);
                }
            }
        });

        adapter.setOnPosterClickLister(new MovieAdapter.OnPosterClickLister() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = adapter.getMovies().get(position);
                Intent intent = new Intent(FavoriteActivity.this, DetailActivity.class);
                intent.putExtra(INTENT_KEY_ID, movie.getId());
                startActivity(intent);
            }
        });
    }

    // Меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavourite:
                Intent intentToFavourite = new Intent(this, FavoriteActivity.class);
                startActivity(intentToFavourite);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}