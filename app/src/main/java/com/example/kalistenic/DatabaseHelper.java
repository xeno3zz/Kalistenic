package com.example.kalistenic;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Kalista.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_WORKOUT = "workout";
    private static final String COLUMN_WORKOUT_ID = "workout_id";
    private static final String COLUMN_EXERCISE_ID = "exercise_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_SETS = "sets";
    private static final String COLUMN_REPS = "reps";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_TIMESPENT_INSECONDS = "timeSpentSec";

    private static final String TABLE_EXERCISES = "exercises";
    private static final String COLUMN_EXERCISE_NAME = "name";
    private static final String COLUMN_EXERCISE_DESCRIPTION = "description";
    private static final String COLUMN_ISCARDIO = "isCardio";

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORDHASH = "passwordHash";
    private static final String COLUMN_FIRSTNAME = "firstname";
    private static final String COLUMN_LASTNAME = "lastname";
    private static final String COLUMN_SALT = "salt";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableSql = "CREATE TABLE " + TABLE_WORKOUT + " ("
                + COLUMN_WORKOUT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_EXERCISE_ID + " INTEGER REFERENCES " + TABLE_EXERCISES + "(" + COLUMN_EXERCISE_ID + ") ON DELETE CASCADE ON UPDATE CASCADE, "
                + COLUMN_USER_ID + " INTEGER REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE ON UPDATE CASCADE, "
                + COLUMN_SETS + " INTEGER, "
                + COLUMN_REPS + " INTEGER, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_TIMESPENT_INSECONDS + " INTEGER, "
                + COLUMN_TIME + " TEXT)";
        db.execSQL(createTableSql);

        String createTableSql1 = "CREATE TABLE " + TABLE_EXERCISES + " ("
                + COLUMN_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_ID + " INTEGER REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE ON UPDATE CASCADE, "
                + COLUMN_EXERCISE_NAME + " TEXT, "
                + COLUMN_ISCARDIO + " BOOLEAN, "
                + COLUMN_EXERCISE_DESCRIPTION + " TEXT)";
        db.execSQL(createTableSql1);

        String createTableSql2 = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_EMAIL + " TEXT, "
                + COLUMN_PASSWORDHASH + " TEXT, "
                + COLUMN_FIRSTNAME + " TEXT, "
                + COLUMN_LASTNAME + " TEXT, "
                + COLUMN_SALT + " TEXT)";
        db.execSQL(createTableSql2);


    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    public void addUser(String email, String passwordHash, String firstName, String lastName, String salt) {
//        // Get a writable instance of the database
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        // Create a new ContentValues object to store the user data
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_EMAIL, email);
//        values.put(COLUMN_PASSWORDHASH, passwordHash);
//        values.put(COLUMN_FIRSTNAME, firstName);
//        values.put(COLUMN_LASTNAME, lastName);
//        values.put(COLUMN_SALT, salt);
//
//        // Insert the new user data into the database and return the ID of the new row
//        long id = db.insert(TABLE_USERS, null, values);
//        db.close();

        // Create a new instance of the User class with the provided parameters
        User user = new User(email, passwordHash, firstName, lastName, salt);

        // Get a writable instance of the database
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new ContentValues object to store the user data
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORDHASH, user.getPasswordHash());
        values.put(COLUMN_FIRSTNAME, user.getFirstName());
        values.put(COLUMN_LASTNAME, user.getLastName());
        values.put(COLUMN_SALT, user.getSalt());

        // Insert the new user data into the database and return the ID of the new row
        long id = db.insert(TABLE_USERS, null, values);
        db.close();

    }

    public void insertWorkout(int exercise_id, int sets, int reps, int user_id) {
        Workout workout = new Workout(user_id, exercise_id, sets, reps);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXERCISE_ID, exercise_id);
        values.put(COLUMN_SETS, sets);
        values.put(COLUMN_REPS, reps);
        values.put(COLUMN_DATE, getCurrentDate());
        values.put(COLUMN_TIME, getCurrentTime());
        values.put(COLUMN_USER_ID, user_id);
        db.insert(TABLE_WORKOUT, null, values);
       db.close();


//        Exercise exercise = new Exercise(user_id, name, isCardio, description);
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_EXERCISE_NAME, exercise.getName());
//        values.put(COLUMN_ISCARDIO, exercise.getIsCardio());
//        values.put(COLUMN_EXERCISE_DESCRIPTION, exercise.getDescription());
//        values.put(COLUMN_USER_ID, exercise.getUser_id());
//        db.insert(TABLE_EXERCISES, null, values);
    }



    public void addExercise(int user_id, String name, int isCardio, String description){


        Exercise exercise = new Exercise(user_id, name, isCardio, description);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXERCISE_NAME, exercise.getName());
        values.put(COLUMN_ISCARDIO, exercise.getIsCardio());
        values.put(COLUMN_EXERCISE_DESCRIPTION, exercise.getDescription());
        values.put(COLUMN_USER_ID, exercise.getUser_id());
        db.insert(TABLE_EXERCISES, null, values);

    }


    public int getExerciseIdByName(String exerciseName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("exercises",
                new String[]{"exercise_id"},
                "name = ?",
                new String[]{exerciseName},
                null,
                null,
                null);
        int exerciseId = -1; // default value if not found
        if (cursor.moveToFirst()) {
            exerciseId = cursor.getInt(cursor.getColumnIndexOrThrow("exercise_id"));
        }
        cursor.close();
        db.close();
        return exerciseId;
    }


    public int getUserId(String email, String password) {
        int userId = -1;
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { "user_id" };
        String selection = "email = ? ";
        String[] selectionArgs = { email };
        Cursor cursor = db.query("users", projection, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
        }
        cursor.close();
        return userId;
    }
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getCurrentTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return timeFormat.format(date);
    }

}
