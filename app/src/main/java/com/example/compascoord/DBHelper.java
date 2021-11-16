package com.example.compascoord;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "infoDb";
    public static final String TABLE_INFOS = "infos";

    public static final String KEY_ID = "_id";
    public static final String KEY_DEGREE = "degree";
    public static final String KEY_COORD1 = "coord1";
    public static final String KEY_COORD2 = "coord2";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_INFOS + "(" + KEY_ID
                + " integer primary key," + KEY_DEGREE + " text," + KEY_COORD1 + " text," + KEY_COORD2 + "text" + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_INFOS);
        onCreate(db);

    }
}
