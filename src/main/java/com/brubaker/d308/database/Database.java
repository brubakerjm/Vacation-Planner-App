package com.brubaker.d308.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.brubaker.d308.dao.ExcursionDao;
import com.brubaker.d308.dao.VacationDao;
import com.brubaker.d308.entities.Excursion;
import com.brubaker.d308.entities.Vacation;

@androidx.room.Database(entities = {Vacation.class, Excursion.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class Database extends RoomDatabase {

    // Instance to prevent multiple database connections
    private static volatile Database INSTANCE;

    public abstract VacationDao vacationDao();
    public abstract ExcursionDao excursionDao();

    // Static method to create or retrieve database instance
    public static Database getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (Database.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    Database.class, "vacation_database")
                            .fallbackToDestructiveMigration() // TODO: Included for scheme changes, but may not be necessary in production build
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}

