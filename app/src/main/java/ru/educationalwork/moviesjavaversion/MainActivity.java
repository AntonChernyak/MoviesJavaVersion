package ru.educationalwork.moviesjavaversion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.educationalwork.moviesjavaversion.adapters.MovieAdapter;
import ru.educationalwork.moviesjavaversion.data.MainViewModel;
import ru.educationalwork.moviesjavaversion.data.Movie;
import ru.educationalwork.moviesjavaversion.utils.JSONUtils;
import ru.educationalwork.moviesjavaversion.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {

    private SwitchCompat switchSort;
    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;
    private TextView textViewTopRated;
    private TextView textViewPopularity;
    private ProgressBar progressBarLoading;

    private MainViewModel viewModel;

    private static final int LOADER_ID = 100;
    private LoaderManager loaderManager;

    // для пагинации
    private static int page = 1;
    private static int methodOfSort;
    private static boolean isLoading = false;

    private static String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lang = Locale.getDefault().getLanguage();

        loaderManager = LoaderManager.getInstance(this); // паттерн Singleton. Получае экземпляр загрузчика, который отвечает за все загрузки, которые происходят в приложении

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        switchSort = findViewById(R.id.switchSort);
        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        textViewPopularity = findViewById(R.id.textViewPopularity);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        progressBarLoading = findViewById(R.id.progressBarLoading);

        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, getColumnCount()));
        movieAdapter = new MovieAdapter();
        recyclerViewPosters.setAdapter(movieAdapter);

        switchSort.setChecked(true);
        // Выбор сортировки
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                page = 1;
                setMethodOfSort(isChecked);
            }
        });
        switchSort.setChecked(false);

        movieAdapter.setOnPosterClickLister(new MovieAdapter.OnPosterClickLister() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = movieAdapter.getMovies().get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });

        // пагинация
        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                if (!isLoading) { // если загрузка не началась
                    // то начинаем
                    downloadData(methodOfSort, page);
                }
            }
        });

        viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                // Если чети нет, то берем данные из БД
                if (page == 1) {
                    movieAdapter.setMovies(movies);
                }
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
        if (isTopRated) {
            textViewTopRated.setTextColor(getResources().getColor(R.color.colorAccent, Resources.getSystem().newTheme()));
            textViewPopularity.setTextColor(getResources().getColor(R.color.white_color, Resources.getSystem().newTheme()));
            methodOfSort = NetworkUtils.TOP_RATED;
        } else {
            textViewTopRated.setTextColor(getResources().getColor(R.color.white_color, Resources.getSystem().newTheme()));
            textViewPopularity.setTextColor(getResources().getColor(R.color.colorAccent, Resources.getSystem().newTheme()));
            methodOfSort = NetworkUtils.POPULARITY;
        }
        downloadData(methodOfSort, page);

    }

    // считаем число колонок
    private int getColumnCount(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // получаем ширину в dp
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return Math.max(width / 185, 2);
    }

    // Загрузка данных
    private void downloadData(int methodOfSort, int page) {
        URL url = NetworkUtils.buildURL(methodOfSort, page, lang); // создаём url
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());
        // добавляем загрузчик. restart --- проверит, есть ли уже loader
        loaderManager.restartLoader(LOADER_ID, bundle, this); // callback == this, т.к. все слушатеи loader реализовали в активити
    }

    /*
        Следующие 3 метода переопределяем из-за интерфейса LoaderManager.LoaderCallbacks<>
     */
    // Начало загрузки
    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle args) {
        // id тут --- уникальный идентификатор загрузчика. Указываем сами.
        NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this, args);
        jsonLoader.setOnStartLoadingListener(new NetworkUtils.JSONLoader.OnStartLoadingListener() {
            @Override
            public void onStartLoading() {
                isLoading = true;
                progressBarLoading.setVisibility(View.VISIBLE);
            }
        });
        return jsonLoader; // получили данные
    }

    // получнные данные пердаются сюда. Получаем из них фильмы. После завершения загрузки
    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject data) {
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(data);
        if (movies != null && !movies.isEmpty()) {
            // при переключении сортировки у нас устанавливается page = 1
            if (page == 1) { // также если интернет есть, то устанавливается page = 1, очищаем БД и список. Если нет то берем из БД
                viewModel.deleteAllMovies();
                movieAdapter.clear();
            }
            // добавляем полученный список фильмов в БД
            for (Movie movie : movies) {
                viewModel.insertMovie(movie);
            }
            // добавляем список в адаптер
            movieAdapter.addMovies(movies);
            page++;
        }
        // после завершения загрузки удаляем loader
        loaderManager.destroyLoader(LOADER_ID);
        isLoading = false;
        progressBarLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }

    // Меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

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