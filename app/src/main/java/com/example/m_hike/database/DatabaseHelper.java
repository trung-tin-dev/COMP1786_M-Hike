package com.example.m_hike.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

import com.example.m_hike.model.User;
import com.example.m_hike.model.Hike;

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

    // ===========================
    // HIKES TABLE
    // ===========================
    public static final String TABLE_HIKES = "hikes";
    public static final String HIKE_ID = "id";
    public static final String HIKE_USER_ID = "user_id";
    public static final String HIKE_NAME = "name";
    public static final String HIKE_LOCATION = "location";
    public static final String HIKE_DATE = "date";
    public static final String HIKE_PARKING = "parking_available";
    public static final String HIKE_LENGTH = "length";
    public static final String HIKE_DIFFICULTY = "difficulty";
    public static final String HIKE_DURATION = "estimated_duration";
    public static final String HIKE_DESCRIPTION = "description";
    public static final String HIKE_START_TIME = "start_time";
    public static final String HIKE_END_TIME = "end_time";
    public static final String HIKE_STATUS = "status";
    public static final String HIKE_CREATED_AT = "created_at";
    public static final String HIKE_UPDATED_AT = "updated_at";
    public static final String HIKE_DELETED_AT = "deleted_at";

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

//    Create Hike Table
    private static final String CREATE_HIKES_TABLE =
            "CREATE TABLE " + TABLE_HIKES + " (" +
                    HIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    HIKE_USER_ID + " INTEGER NOT NULL, " +
                    HIKE_NAME + " TEXT NOT NULL, " +
                    HIKE_LOCATION + " TEXT NOT NULL, " +
                    HIKE_DATE + " TEXT NOT NULL, " +
                    HIKE_PARKING + " INTEGER NOT NULL, " +
                    HIKE_LENGTH + " REAL NOT NULL, " +
                    HIKE_DIFFICULTY + " TEXT NOT NULL, " +
                    HIKE_DURATION + " TEXT NOT NULL, " +
                    HIKE_DESCRIPTION + " TEXT, " +
                    HIKE_START_TIME + " TEXT, " +
                    HIKE_END_TIME + " TEXT, " +
                    HIKE_STATUS + " TEXT NOT NULL, " +
                    HIKE_CREATED_AT + " TEXT, " +
                    HIKE_UPDATED_AT + " TEXT, " +
                    HIKE_DELETED_AT + " TEXT" +
                    ");";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        db.execSQL(CREATE_USERS_TABLE);
        // Create Hikes Table
        db.execSQL(CREATE_HIKES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIKES);
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
//        db.close();
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
//        db.close();
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
//        db.close();
        return success;
    }

//    Get Username
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
//        db.close();
        return username;
    }

    // ===========================
    // INSERT HIKE
    // ===========================
    public boolean insertHike(Hike hike) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(HIKE_USER_ID, hike.getUserId());
        values.put(HIKE_NAME, hike.getName());
        values.put(HIKE_LOCATION, hike.getLocation());
        values.put(HIKE_DATE, hike.getDate());

        values.put(HIKE_PARKING,
                hike.isParkingAvailable() ? 1 : 0);

        values.put(HIKE_LENGTH, hike.getLength());

        values.put(HIKE_DIFFICULTY,
                hike.getDifficulty());

        values.put(HIKE_DURATION,
                hike.getEstimatedDuration());

        values.put(HIKE_DESCRIPTION,
                hike.getDescription());

        values.put(HIKE_START_TIME,
                hike.getStartTime());

        values.put(HIKE_END_TIME,
                hike.getEndTime());

        values.put(HIKE_STATUS,
                hike.getStatus());

        values.put(HIKE_CREATED_AT,
                hike.getCreatedAt());

        values.put(HIKE_UPDATED_AT,
                hike.getUpdatedAt());

        values.put(HIKE_DELETED_AT,
                hike.getDeletedAt());

        long result = db.insert(TABLE_HIKES,
                null,
                values);

//        db.close();

        return result != -1;
    }

    // ===========================
    // GET ALL HIKES
    // ===========================
    public List<Hike> getAllHikes(int userId) {

        List<Hike> hikeList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_HIKES,
                null,
                HIKE_USER_ID + "=? AND " +
                        HIKE_DELETED_AT + " IS NULL",
                new String[]{
                        String.valueOf(userId)
                },
                null,
                null,
                HIKE_CREATED_AT + " DESC"
        );

        if (cursor.moveToFirst()) {

            do {

                Hike hike = new Hike();

                hike.setId(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(HIKE_ID)));

                hike.setUserId(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(HIKE_USER_ID)));

                hike.setName(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(HIKE_NAME)));

                hike.setLocation(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(HIKE_LOCATION)));

                hike.setDate(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(HIKE_DATE)));

                hike.setParkingAvailable(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(HIKE_PARKING)) == 1);

                hike.setLength(
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(HIKE_LENGTH)));

                hike.setDifficulty(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(HIKE_DIFFICULTY)));

                hike.setEstimatedDuration(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(HIKE_DURATION)));

                hike.setDescription(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(HIKE_DESCRIPTION)));

                hike.setStartTime(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(HIKE_START_TIME)));

                hike.setEndTime(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(HIKE_END_TIME)));

                hike.setStatus(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(HIKE_STATUS)));

                hike.setCreatedAt(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(HIKE_CREATED_AT)));

                hike.setUpdatedAt(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(HIKE_UPDATED_AT)));

                hike.setDeletedAt(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(HIKE_DELETED_AT)));

                hikeList.add(hike);

            } while (cursor.moveToNext());

        }

        cursor.close();
//        db.close();

        return hikeList;

    }
}