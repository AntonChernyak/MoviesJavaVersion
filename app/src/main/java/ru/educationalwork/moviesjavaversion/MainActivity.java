package ru.educationalwork.moviesjavaversion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import ru.educationalwork.moviesjavaversion.data.Movie;
import ru.educationalwork.moviesjavaversion.utils.JSONUtils;
import ru.educationalwork.moviesjavaversion.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    private SwitchCompat switchSort;
    private RecyclerView recyclerViewPosters;
    private  MovieAdapter movieAdapter;
    private TextView textViewTopRated;
    private TextView textViewPopularity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchSort = findViewById(R.id.switchSort);
        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        textViewPopularity = findViewById(R.id.textViewPopularity);
        textViewTopRated = findViewById(R.id.textViewTopRated);

        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, 2));
        movieAdapter = new MovieAdapter();
        switchSort.setChecked(true);
        recyclerViewPosters.setAdapter(movieAdapter);

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

        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(methodOfSort, 1);
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        movieAdapter.setMovies(movies);
    }
}