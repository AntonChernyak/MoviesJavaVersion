package ru.educationalwork.moviesjavaversion.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.educationalwork.moviesjavaversion.data.Movie;

/**
 * Преобразование JSON  в объект Movie
 * <p>
 * Смотрим в API и достаём оттуда ключи начиная с массива result. Такие же поля должны быть и у объекта Movie
 */
public class JSONUtils {

    private static final String KEY_RESULTS = "results";
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_POSTER_PATH = "poster_path"; // постер. Тут только конечная часть пути. Базовый url и размер отдельно
    private static final String KEY_BACKDROP_PATH = "backdrop_path"; // фоновое изображение
    private static final String KEY_VOTE_AVERAGE = "vote_average"; // рейтинг
    private static final String KEY_RELEASE_DATE = "release_date";

    /**
     * В документации в Getting Started находим, что полный путь: https://image.tmdb.org/t/p/w500/poster_path,
     * где до w500 --- базовый url, а сам w500 --- размер картинки
     * <p>
     * Размеры картинки смотрим в пункте Configuration (https://developers.themoviedb.org/3/configuration/get-api-configuration)
     * в "Try it out" после ввода ключа API в Variables. Отсюда интересует poster_sizes. Для малых изображений возьмем w185, для крупных w780.
     */

    // Для обработки постеров
    private static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    private static final String SMALL_POSTER_SIZE = "w185";
    private static final String BIG_POSTER_SIZE = "w780";

    // после запроса в сеть в NetworkUtils мы получаем массив с фильмами в формате JSON, отработаем его
    public static ArrayList<Movie> getMoviesFromJSON(JSONObject jsonObject) {
        ArrayList<Movie> result = new ArrayList<>();

        if (jsonObject == null) return result;

        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);

            // теперь из полученного ассива json получим фильмы
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objectMovie = jsonArray.getJSONObject(i);
                int id = objectMovie.getInt(KEY_ID);
                int voteCount = objectMovie.getInt(KEY_VOTE_COUNT);
                String title = objectMovie.getString(KEY_TITLE);
                String originalTitle = objectMovie.getString(KEY_ORIGINAL_TITLE);
                String overview = objectMovie.getString(KEY_OVERVIEW);
                String posterPath = BASE_POSTER_URL + SMALL_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String bigPosterPath = BASE_POSTER_URL + BIG_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String backdropPath = objectMovie.getString(KEY_BACKDROP_PATH);
                double voteAverage = objectMovie.getDouble(KEY_VOTE_AVERAGE);
                String releaseDate = objectMovie.getString(KEY_RELEASE_DATE);

                Movie movie = new Movie(id, voteCount, title, originalTitle, overview, posterPath, bigPosterPath, backdropPath, voteAverage, releaseDate);
                result.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}
