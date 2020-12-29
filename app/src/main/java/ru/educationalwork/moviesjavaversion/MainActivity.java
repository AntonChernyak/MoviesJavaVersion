package ru.educationalwork.moviesjavaversion;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.educationalwork.moviesjavaversion.data.MainViewModel;
import ru.educationalwork.moviesjavaversion.data.Movie;
import ru.educationalwork.moviesjavaversion.utils.JSONUtils;
import ru.educationalwork.moviesjavaversion.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    private SwitchCompat switchSort;
    private RecyclerView recyclerViewPosters;
    private  MovieAdapter movieAdapter;
    private TextView textViewTopRated;
    private TextView textViewPopularity;

    private MainViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        switchSort = findViewById(R.id.switchSort);
        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        textViewPopularity = findViewById(R.id.textViewPopularity);
        textViewTopRated = findViewById(R.id.textViewTopRated);

        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, 2));
        movieAdapter = new MovieAdapter();
        recyclerViewPosters.setAdapter(movieAdapter);

        switchSort.setChecked(true);
         // Выбор сортировки
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setMethodOfSort(isChecked);
            }
        });
        switchSort.setChecked(false);

        movieAdapter.setOnPosterClickLister(new MovieAdapter.OnPosterClickLister() {
            @Override
            public void onPosterClick(int position) {
                Toast.makeText(MainActivity.this, "On Clicked: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                Toast.makeText(MainActivity.this, "Конец списка", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                movieAdapter.setMovies(movies);
            }
        });

    }

    // Чтобы менять сортировку можно было не только по Switch, но и по клику на текст
    public void onClickSetPopularity(View view) {
        setMethodOfSort(false);
        switchSort.setChecked(false);
    }

    public void onClickSetOnRated(View view) {
        setMethodOfSort(true);
        switchSort.setChecked(true);
    }

    private void setMethodOfSort(boolean isTopRated) {
        int methodOfSort;
        if (isTopRated){
            textViewTopRated.setTextColor(getResources().getColor(R.color.colorAccent, Resources.getSystem().newTheme()));
            textViewPopularity.setTextColor(getResources().getColor(R.color.white_color, Resources.getSystem().newTheme()));
            methodOfSort = NetworkUtils.TOP_RATED;
        } else{
            textViewTopRated.setTextColor(getResources().getColor(R.color.white_color, Resources.getSystem().newTheme()));
            textViewPopularity.setTextColor(getResources().getColor(R.color.colorAccent, Resources.getSystem().newTheme()));
            methodOfSort = NetworkUtils.POPULARITY;
        }
        downloadData(methodOfSort, 1);

    }

    // Загрузка данных
    private void downloadData(int methodOfSort, int page){
        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(methodOfSort, 1);
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        if (movies != null && !movies.isEmpty()) {
            viewModel.deleteAllMovies();
            for (Movie movie : movies) {
                viewModel.insertMovie(movie);
            }
        }
    }
}