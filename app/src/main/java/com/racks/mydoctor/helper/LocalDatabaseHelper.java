package com.racks.mydoctor.helper;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.racks.mydoctor.models.UserDetailDao;
import com.racks.mydoctor.models.UserDetailModel;

@Database(entities = {UserDetailModel.class}, version = 2, exportSchema = false)
public abstract class LocalDatabaseHelper extends RoomDatabase {
    private static LocalDatabaseHelper instance;

    public abstract UserDetailDao userDetailDao();

    public static synchronized LocalDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            LocalDatabaseHelper.class, "user_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
