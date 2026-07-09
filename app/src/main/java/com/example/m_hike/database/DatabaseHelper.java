package com.example.m_hike.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Information
    private static final String DATABASE_NAME = "mhike.db";
    private static final int DATABASE_VERSION = 1;

    // ===========================
    // USERS TABLE
    // ===========================

    public static final String TABLE_USERS = "users";

    public static final String USER_ID = "id";
    public static final String USER_NAME = "user_name";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_PASSWORD = "password";
    public static final String USER_AVATAR = "avt_path";
    public static final String USER_CREATED_AT = "created_at";

    // Create Users Table
    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    USER_NAME + " TEXT NOT NULL, " +
                    USER_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    USER_PASSWORD + " TEXT NOT NULL, " +
                    USER_AVATAR + " TEXT, " +
                    USER_CREATED_AT + " TEXT" +
                    ");";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create Users Table
        db.execSQL(CREATE_USERS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        onCreate(db);

    }
}