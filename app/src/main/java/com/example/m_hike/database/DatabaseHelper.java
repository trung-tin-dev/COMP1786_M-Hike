package com.example.m_hike.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import com.example.m_hike.model.User;

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

//    Insert User
    public boolean insertUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(USER_NAME, user.getUserName());
        values.put(USER_EMAIL, user.getUserEmail());
        values.put(USER_PASSWORD, user.getPassword());
        values.put(USER_AVATAR, user.getAvatarPath());
        values.put(USER_CREATED_AT, user.getCreatedAt());

        long result = db.insert(TABLE_USERS, null, values);

        db.close();

        return result != -1;
    }

//    Check email Exist
    public boolean isEmailExists(String email) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USERS,
                null,
                USER_EMAIL + "=?",
                new String[]{email},
                null,
                null,
                null
        );

        boolean exists = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return exists;
    }

//    Check Login
    public boolean checkLogin(String email, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USERS,
                null,
                USER_EMAIL + "=? AND " + USER_PASSWORD + "=?",
                new String[]{email, password},
                null,
                null,
                null
        );

        boolean success = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return success;
    }

//    Get User Name
    public String getUsernameByEmail(String email) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{USER_NAME},
                USER_EMAIL + "=?",
                new String[]{email},
                null,
                null,
                null
        );

        String username = "";

        if (cursor.moveToFirst()) {
            username = cursor.getString(0);
        }

        cursor.close();
        db.close();

        return username;
    }
}