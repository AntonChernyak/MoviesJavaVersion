package ru.educationalwork.moviesjavaversion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import ru.educationalwork.moviesjavaversion.adapters.ReviewAdapter;
import ru.educationalwork.moviesjavaversion.adapters.TrailerAdapter;
import ru.educationalwork.moviesjavaversion.data.FavouriteMovie;
import ru.educationalwork.moviesjavaversion.data.MainViewModel;
import ru.educationalwork.moviesjavaversion.data.Movie;
import ru.educationalwork.moviesjavaversion.data.Review;
import ru.educationalwork.moviesjavaversion.data.Trailer;
import ru.educationalwork.moviesjavaversion.utils.JSONUtils;
import ru.educationalwork.moviesjavaversion.utils.NetworkUtils;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewBigPoster;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRating;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;
    private ImageView imageViewAddToFavourite;
    private ScrollView scrollViewInfo;

    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;

    private int id;
    private Movie movie;
    private FavouriteMovie favouriteMovie;

    private MainViewModel viewModel;
    private String lang;

    public static final String INTENT_KEY_ID = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        lang = Locale.getDefault().getLanguage();
        initViews();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(INTENT_KEY_ID)) {
            id = intent.getIntExtra(INTENT_KEY_ID, -1);
        } else {
            finish();
        }

        setData();

        setFavourite();
        setTrailersRecyclerSettings();
        setReviewsRecyclerSettings();

        // установим начальное положение ScrollView на самом верху
        scrollViewInfo.smoothScrollTo(0, 0);
    }

    public void initViews() {
        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);
        imageViewAddToFavourite = findViewById(R.id.imageViewAddToFavorite);
        scrollViewInfo = findViewById(R.id.scrollViewInfo);
        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
    }

    private void setData() {
        viewModel = new ViewModelProvider(DetailActivity.this).get(MainViewModel.class);
        movie = viewModel.getMovieById(id);

        Picasso.get()
                .load(movie.getBigPosterPath())
                .placeholder(R.drawable.ic_baseline_error_outline_24)
                .into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewOverview.setText(movie.getOverview());
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewRating.setText(Double.toString(movie.getVoteAverage()));
    }

    private void setTrailersRecyclerSettings() {
        TrailerAdapter trailerAdapter = new TrailerAdapter();
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setAdapter(trailerAdapter);

        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent intentToYouTube = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentToYouTube);
            }
        });

        // Получаем массив JSON объектов
        JSONObject jsonObjectTrailers = NetworkUtils.getJSONForVideos(movie.getId(), lang);
        // Получаем список трейлеров из массива с JSON
        ArrayList<Trailer> trailers = JSONUtils.getTrailersFromJSON(jsonObjectTrailers);
        // Заполняем адаптер данными
        trailerAdapter.setTrailers(trailers);
    }

    private void setReviewsRecyclerSettings() {
        ReviewAdapter reviewAdapter = new ReviewAdapter();
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setAdapter(reviewAdapter);

        JSONObject jsonObjectReviews = NetworkUtils.getJSONForReviews(movie.getId());
        ArrayList<Review> reviews = JSONUtils.getReviewsFromJSON(jsonObjectReviews);

        reviewAdapter.setReviews(reviews);
    }

    public void onClickChangeFavorite(View view) {
        if (favouriteMovie == null) {
            viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
            Toast.makeText(this, getResources().getString(R.string.add_to_favourite), Toast.LENGTH_SHORT).show();
        } else {
            viewModel.deleteFavouriteMovie(favouriteMovie);
            Toast.makeText(this, getResources().getString(R.string.remove_from_favourite), Toast.LENGTH_SHORT).show();
        }
        setFavourite();
    }

    private void setFavourite() {
        favouriteMovie = viewModel.getFavouriteMovieById(id);
        if (favouriteMovie == null) {
            imageViewAddToFavourite.setColorFilter(getResources().getColor(R.color.gray_color_500));
        } else {
            imageViewAddToFavourite.setColorFilter(getResources().getColor(R.color.red_color_500));
        }
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