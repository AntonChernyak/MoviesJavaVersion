package ru.educationalwork.moviesjavaversion.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Movie.class, FavouriteMovie.class}, version = 3, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    private static MovieDatabase database;
    private static final String DB_NAME = "movies.db";
    private static final Object LOCK = new Object();

    // Паттерн Singleton
    public static MovieDatabase getInstance(Context context) {
        // Блок синхронизации. Чтобы при || потоках не было создания сразу двух БД
        synchronized (LOCK) {
            if (database == null) {
                database = Room.databaseBuilder(context, MovieDatabase.class, DB_NAME)
                        .fallbackToDestructiveMigration() // при обновлении верси БД все старые данные удаляются. Нет миграции т.е.
                        .build();
            }
        }
        return database;
    }

    // Метод вернет dao
    public abstract MovieDao movieDao();
}