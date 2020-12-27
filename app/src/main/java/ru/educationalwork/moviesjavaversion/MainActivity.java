package ru.educationalwork.moviesjavaversion;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*        String url = NetworkUtils.buildURL(NetworkUtils.POPULARITY, 1).toString();
        Log.i("MyResult", url);*/

/*        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(NetworkUtils.YOP_RATED, 3);
        if (jsonObject == null) {
            Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "", Toast.LENGTH_SHORT).show();*/

        JSONObject jsonObject3 = NetworkUtils.getJSONFromNetwork(NetworkUtils.POPULARITY, 5);
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject3);
        StringBuilder builder = new StringBuilder();
        for (Movie m: movies){
            builder.append(m.getTitle()).append("\n");
        }
        Log.i("MyResult", builder.toString());

    }
}