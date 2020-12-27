package ru.educationalwork.moviesjavaversion.utils;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
 */
public class NetworkUtils {

    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";

    // Из API сохраним нужные нам параметры, которые мы можем включить в запрос
    private static final String PARAMS_API_KEY = "api_key";
    private static final String PARAMS_API_LANGUAGE = "language";
    private static final String PARAMS_SORT_BY = "sort_by";
    private static final String PARAMS_PAGE = "page";

    // Теперь сохраним значения параметров
    private static final String API_KEY = "54006cca47fc8c9aec32e7516a2f4e64";
    private static final String LANGUAGE_VALUE = "ru-RU"; // или en-US
    private static final String SORT_BY_POPULARITY = "popularity.desc"; // по популярности
    private static final String SORT_BY_TOP_RATED = "vote_average.desc"; // по рейтингу
    // номер страницы --- число, которое вегда будет разным. Нечего сохранять

    // Числовые параметры для метода, определяющего способ сортировки
    public static final int POPULARITY = 0;
    public static final int TOP_RATED = 1;

    /**
     * Метод, формирующий запрос. Сначала парсим строку в адрес, затем прикрепляем запросы в формате
     * (параметр, значение) с помощью метода  appendQueryParameter().
     * В итоге получаем ссылку по которой лежит JSON
     */
    public static URL buildURL(int sortBy, int page) {
        URL result = null;

        // определим значение параметра сортироки в зависимости от типа сортировки
        String methodOfSort;
        if (sortBy == POPULARITY) {
            methodOfSort = SORT_BY_POPULARITY;
        } else methodOfSort = SORT_BY_TOP_RATED;

        // пишем запрос
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_API_LANGUAGE, LANGUAGE_VALUE)
                .appendQueryParameter(PARAMS_SORT_BY, methodOfSort)
                .appendQueryParameter(PARAMS_PAGE, Integer.toString(page))
                .build();

        try {
            result = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result;
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
            Log.i("MyResult_LoadTask", result.toString());
            return result;
        }
    }

    /**
     *  Метод получает JSON из сети
     *  Не забыть добавить в манифест разрешение!
     *
     *  По сути это конкретная реализация предыдущего приватного метода
     */
    public static JSONObject getJSONFromNetwork(int sortBy, int page) {
        JSONObject result = null;
        URL url = buildURL(sortBy, page);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("MyResult_getJSON", result.toString());
        return result;
    }


}
