package ru.educationalwork.moviesjavaversion.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Работа с сетью
 * <p>
 * Ссылка на API: https://www.themoviedb.org/ . Регистрируемся, запрашиваем ключ, регистрируем приложение.
 * Документация по API: https://developers.themoviedb.org/3 . Раздел Discover --> Try it out --> вставляем ключ
 * --> отправляем запрос --> получаем JSON. Тут же берем базовый URL (до вопросительного знака)
 * <p>
 * https://developers.themoviedb.org/3/movies/get-movie-videos --- отсюда про трейлеры. Полная API-ссылка:
 * https://api.themoviedb.org/3/movie/{movie_id}/videos?api_key=<<api_key>>&language=en-US, где
 * базовый url --- до знака ?
 * основные параметры --- после знака ?, но до &
 * неосновные параметры --- после &
 * <p>
 * https://developers.themoviedb.org/3/movies/get-movie-reviews --- для отзывов. Полная API-ссылка:
 * https://api.themoviedb.org/3/movie/{movie_id}/reviews?api_key=<<api_key>>&language=en-US&page=1
 * Мы будем выводить все страницы, поэтому page не указываем
 */

public class NetworkUtils {

    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie"; // основная информация
    private static final String BASE_URL_VIDEOS = "https://api.themoviedb.org/3/movie/%s/videos"; // трейлеры
    private static final String BASE_URL_REVIEWS = "https://api.themoviedb.org/3/movie/%s/reviews"; // отзывы

    // Из API сохраним нужные нам параметры, которые мы можем включить в запрос
    private static final String PARAMS_API_KEY = "api_key";
    private static final String PARAMS_API_LANGUAGE = "language";
    private static final String PARAMS_SORT_BY = "sort_by";
    private static final String PARAMS_PAGE = "page";
    private static final String PARAMS_MIN_VOTE_COUNT = "vote_count.gte";

    // Теперь сохраним значения параметров
    private static final String API_KEY = "54006cca47fc8c9aec32e7516a2f4e64";
    //private static final String LANGUAGE_VALUE = "ru-RU"; // или en-US. Upd: Теперь язык передаем в качестве параметра. Выставится автоматически
    private static final String SORT_BY_POPULARITY = "popularity.desc"; // по популярности
    private static final String SORT_BY_TOP_RATED = "vote_average.desc"; // по рейтингу
    private static final String MIN_VOTE_COUNT_VALUE = "5000";
    // номер страницы --- число, которое вегда будет разным. Нечего сохранять

    // Числовые параметры для метода, определяющего способ сортировки
    public static final int POPULARITY = 0;
    public static final int TOP_RATED = 1;

    /**
     * Метод, формирующий запрос. Сначала парсим строку в адрес, затем прикрепляем запросы в формате
     * (параметр, значение) с помощью метода  appendQueryParameter().
     * В итоге получаем ссылку по которой лежит JSON
     */
    // для URL с основной информацией
    public static URL buildURL(int sortBy, int page, String lang) {
        URL result = null;

        // определим значение параметра сортироки в зависимости от типа сортировки
        String methodOfSort;
        if (sortBy == POPULARITY) {
            methodOfSort = SORT_BY_POPULARITY;
        } else methodOfSort = SORT_BY_TOP_RATED;

        // пишем запрос
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_API_LANGUAGE, lang)
                .appendQueryParameter(PARAMS_SORT_BY, methodOfSort)
                .appendQueryParameter(PARAMS_PAGE, Integer.toString(page))
                .appendQueryParameter(PARAMS_MIN_VOTE_COUNT, MIN_VOTE_COUNT_VALUE)
                .build();

        try {
            result = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // для URL  трейлерами
    public static URL buildURLToVideos(int id, String lang) {
        Uri uri = Uri.parse(String.format(BASE_URL_VIDEOS, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_API_LANGUAGE, lang)
                .build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // для URL с отзывами
    public static URL buildURLToReviews(int id) {
        Uri uri = Uri.parse(String.format(BASE_URL_REVIEWS, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                //.appendQueryParameter(PARAMS_API_LANGUAGE, LANGUAGE_VALUE) // отображаем отзывы на всех языках. На русском тут почти нет :(
                .build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Метод для загрузки данных из интернета
     */
    private static class JSONLoadTask extends AsyncTask<URL, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(URL... urls) {
            JSONObject result = null;

            if (urls == null || urls.length == 0) {
                return null;
            }
            // Если с url всё в порядке, то создаём соединение
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) urls[0].openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                // Чтобы читать сразу строками создадим BufferedReader
                BufferedReader reader = new BufferedReader(inputStreamReader);

                StringBuilder builder = new StringBuilder();
                String line = reader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = reader.readLine();
                }
                try {
                    result = new JSONObject(builder.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // не забыть закрыть соединение!
                if (connection != null) {
                    connection.disconnect();
                }
            }
            if (result != null) {
                Log.i("MyResult_LoadTask", result.length() + " " + result.toString());
            }
            return result;
        }
    }

    /**
     * Загрузка данных из сети с помощью AsyncTaskLoader. Обычный AsyncTask не подойдет (например,
     * при перевороте экрана загрузка прервется, а приложение упадёт скорее всего
     */
    public static class JSONLoader extends AsyncTaskLoader<JSONObject> {

        private Bundle bundle;
        private OnStartLoadingListener onStartLoadingListener;

        // слушатель, реагирующий на начало загрузки
        public interface OnStartLoadingListener {
            void onStartLoading();
        }

        // setter
        public void setOnStartLoadingListener(OnStartLoadingListener onStartLoadingListener) {
            this.onStartLoadingListener = onStartLoadingListener;
        }

        // Источник данных (тут url) бычно передают через Bundle
        public JSONLoader(@NonNull Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        // Чтобы при инициализации этого загрузчика происходила загрузка переопределим метод onStartLoading()
        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (onStartLoadingListener != null) {
                onStartLoadingListener.onStartLoading();
            }
            forceLoad(); // продолжает загрузку
        }

        @Nullable
        @Override
        public JSONObject loadInBackground() {
            if (bundle == null) {
                return null;
            }
            String urlAsString = bundle.getString("url");
            URL url = null;
            try {
                url = new URL(urlAsString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JSONObject result = null;

            if (url == null) {
                return null;
            }
            // Если с url всё в порядке, то создаём соединение
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                // Чтобы читать сразу строками создадим BufferedReader
                BufferedReader reader = new BufferedReader(inputStreamReader);

                StringBuilder builder = new StringBuilder();
                String line = reader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = reader.readLine();
                }
                try {
                    result = new JSONObject(builder.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // не забыть закрыть соединение!
                if (connection != null) {
                    connection.disconnect();
                }
            }
            if (result != null) {
                Log.i("MyResult_LoadTask", result.length() + " " + result.toString());
            }
            return result;
        }
    }

    /**
     * Метод получает JSON из сети
     * Не забыть добавить в манифест разрешение!
     * <p>
     * По сути это конкретная реализация предыдущего приватного метода
     */
    // Для общих данных
    public static JSONObject getJSONFromNetwork(int sortBy, int page, String lang) {
        JSONObject result = null;
        URL url = buildURL(sortBy, page, lang);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Для трейлеров
    public static JSONObject getJSONForVideos(int id, String lang) {
        JSONObject result = null;
        URL url = buildURLToVideos(id, lang);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Для отзывов
    public static JSONObject getJSONForReviews(int id) {
        JSONObject result = null;
        URL url = buildURLToReviews(id);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

}
