package ru.educationalwork.moviesjavaversion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import ru.educationalwork.moviesjavaversion.data.Movie;
import ru.educationalwork.moviesjavaversion.utils.JSONUtils;
import ru.educationalwork.moviesjavaversion.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPosters;
    private  MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, 2));
        movieAdapter = new MovieAdapter();
        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(NetworkUtils.POPULARITY, 1);
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        movieAdapter.setMovies(movies);
        recyclerViewPosters.setAdapter(movieAdapter);
    }
}