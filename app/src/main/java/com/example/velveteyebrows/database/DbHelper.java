package com.example.velveteyebrows.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {


    public static final String DB_NAME = "Eyebrows_DB";
    public static final int SCHEMA_VERSION = 3;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("Create table if not exists Service ( " +
                "ServiceId Integer not null primary key autoincrement, " +
                "Title Text not null, " +
                "Cost Real not null, " +
                "Duration Integer not null," +
                "Description Text, " +
                "Discount Real, " +
                "Image Blob, " +
                "IsFav Integer default 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("Drop table if exists Services");
        db.execSQL("Drop table if exists Service");
        onCreate(db);
    }
}
