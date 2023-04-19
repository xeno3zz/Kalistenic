package com.example.kalistenic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Spinner exerciseSpinner;
    private EditText setsEditText;
    private EditText repsEditText;
    private Button saveButton, maintochartBtn, cardioExerciseActivityStart, addExerciseBtn;
    private DatabaseHelper dbHelper;
    private ListView listView;
    private TextView idTV;
//    private ExerciseDataAdapter exerciseDataAdapter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
        populateExerciseSpinner();
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        if (userId != -1) {
            idTV.setText(String.valueOf(userId));
        }
        else {
            idTV.setText("User ID not found");
        }
        addExerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddExercise.class);
                startActivity(intent);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWork();
            }
        });


    }

    private void initWidgets() {
        exerciseSpinner = findViewById(R.id.exercise_spinner);
        setsEditText = findViewById(R.id.sets_edittext);
        repsEditText = findViewById(R.id.reps_edittext);
        saveButton = findViewById(R.id.save_button);
        maintochartBtn = findViewById(R.id.maintochartBtn);
        listView = findViewById(R.id.exerciseLV);
        bottomNavigationView = findViewById(R.id.BottomNavigationView);
        addExerciseBtn = findViewById(R.id.AddExerciseBtn);
        idTV = findViewById(R.id.idTV);

    }
    private void addWork() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        dbHelper = new DatabaseHelper(MainActivity.this);

        String exerciseName = exerciseSpinner.getSelectedItem().toString();
        int exercise_id = dbHelper.getExerciseIdByName(exerciseName);
        int sets = Integer.parseInt(setsEditText.getText().toString());
        int reps = Integer.parseInt(repsEditText.getText().toString());

        dbHelper.insertWorkout(exercise_id, sets, reps, userId);

        Toast.makeText(this, "Workout saved successfully", Toast.LENGTH_SHORT).show();
        setsEditText.setText("");
        repsEditText.setText("");
    }
//    private void addWorkCardio(){
//        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
//        int userId = prefs.getInt("userId", -1);
//        dbHelper = new DatabaseHelper(MainActivity.this);
//        String exerciseName = exerciseSpinner.getSelectedItem().toString();
//        int exercise_id = dbHelper.getExerciseIdByName(exerciseName);
//
//    }

//    private void populateExerciseLV() {
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        // Get the current date as a string in the format "yyyy-MM-dd"
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        String currentDate = dateFormat.format(new Date());
//
//        // Define the columns you want to retrieve
//        String[] projection = {"workout_id","exercise_id", "reps", "sets"};
//
//        // Build the query to retrieve the reps and sets for the current date, sorted by time in descending order
//        String selection = "date=?";
//        String[] selectionArgs = {currentDate};
//        String sortOrder = "time DESC";
//        Cursor cursor = db.query("workout", projection, selection, selectionArgs, null, null, sortOrder);
//        exerciseDataAdapter = new ExerciseDataAdapter(this, cursor, db, this, true);
//        listView.setAdapter(exerciseDataAdapter);
//    }
    private void populateExerciseSpinner() {
        dbHelper = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getExerciseNames(userId));

        exerciseSpinner.setAdapter(adapter);
    }

    private List<String> getExerciseNames(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("exercises", new String[] {"name"}, "user_id" + "=?", new String[] {String.valueOf(userId)}, null, null, null);
        List<String> exerciseNames = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
//                String exerciseName = cursor.getString(0);
//                exerciseNames.add(exerciseName);
                exerciseNames.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return exerciseNames;
    }
}