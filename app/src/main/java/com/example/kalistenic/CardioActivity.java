package com.example.kalistenic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
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

public class CardioActivity extends AppCompatActivity implements WorkoutAdapter.WorkoutListener{

    private Spinner exerciseSpinner;
    private EditText timeEditText;

    private Button startButton, maintochartBtn;
    private DatabaseHelper dbHelper;
    private ListView listView;

    private TextView mTimeTextView;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private boolean timerRunning = false;
    private WorkoutAdapter workoutAdapter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardio);
        initWidgets();
        populateExerciseCardioSpinner();
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        populateExerciseCardioLV(userId);
        bottomNavigationView.setSelectedItemId(R.id.action_cardio);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_main:
                        startActivity(new Intent(CardioActivity.this, MainActivity.class));

                        return true;
                    case R.id.action_cardio:
//                        startActivity(new Intent(CardioActivity.this, CardioActivity.class));
                        return true;
                    default:
                        return false;
                }
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timerRunning) {
                    startTimer();
                    startButton.setText("Stop");
                } else {
                    stopTimer();
                    SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    int userId = prefs.getInt("userId", -1);
                    populateExerciseCardioLV(userId);
                    startButton.setText("Start");
                }
            }
        });
        maintochartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardioActivity.this, exercise_graphs.class);
                startActivity(intent);
            }
        });
    }

    private void populateExerciseCardioSpinner() {
        dbHelper = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getCardioExerciseNames(userId));

        exerciseSpinner.setAdapter(adapter);
    }
    private List<String> getCardioExerciseNames(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define the columns you want to retrieve
        String[] projection = {"name"};

        // Build the query to retrieve the names of the exercises with isCardio checked
        String selection = "isCardio=? AND user_id=?";
        String[] selectionArgs = {"1", String.valueOf(userId)};
        Cursor cursor = db.query("exercises", projection, selection, selectionArgs, null, null, null);

        List<String> cardioExerciseNames = new ArrayList<>();
        while (cursor.moveToNext()) {
            cardioExerciseNames.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        }
        cursor.close();
        db.close();

        return cardioExerciseNames;
    }
    private void populateExerciseCardioLV(int user_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Get the current date as a string in the format "yyyy-MM-dd"
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Define the columns you want to retrieve
        String[] projection = {"workout_id as _id","workout.exercise_id", "timeSpentSec"};

        // Build the query to retrieve the reps and sets for the current date, sorted by time in descending order,
        // only for exercises that have isCardio checked (isCardio = 1)
        String selection = "workout.date=? AND workout.user_id=? AND exercises.isCardio=?";
        String[] selectionArgs = {currentDate, String.valueOf(user_id), "1"};
        String sortOrder = "time DESC";
        String tables = "workout LEFT JOIN exercises ON workout.exercise_id = exercises.exercise_id";
        Cursor cursor = db.query(tables, projection, selection, selectionArgs, null, null, sortOrder);
        workoutAdapter = new WorkoutAdapter(this, cursor, db, this, false, dbHelper);
        listView.setAdapter(workoutAdapter);
    }

    public void onDataChanged(){

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        populateExerciseCardioLV(userId);
    }

    private void startTimer() {
        int timeInSeconds = Integer.parseInt(timeEditText.getText().toString());
        timeLeftInMillis = timeInSeconds * 1000;
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();

            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                timerRunning = false;
                updateCountdownText();
                startButton.setText("Start");
                addCardioWork();
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                int userId = prefs.getInt("userId", -1);
                populateExerciseCardioLV(userId);
            }
        }.start();

        timerRunning = true;
    }

    private void updateCountdownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTimeTextView.setText(timeLeftFormatted);
    }

    private void addCardioWork() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        dbHelper = new DatabaseHelper(CardioActivity.this);

        String exerciseName = exerciseSpinner.getSelectedItem().toString();
        int exercise_id = dbHelper.getExerciseIdByName(exerciseName);
        int timeSpent =Integer.parseInt(timeEditText.getText().toString()) - (int) (timeLeftInMillis / 1000);

        dbHelper.insertCardioWorkout(exercise_id, timeSpent, userId);

        Toast.makeText(this, "Workout saved successfully", Toast.LENGTH_SHORT).show();
    }

    // Function to stop the timer
    private void stopTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        addCardioWork();
    }

    private void initWidgets() {


        exerciseSpinner = findViewById(R.id.exercise_spinner_cardio);
//        setsEditText = findViewById(R.id.sets_edittext_cardio);
        timeEditText = findViewById(R.id.time_edittext_cardio);
        startButton = findViewById(R.id.start_button);
        maintochartBtn = findViewById(R.id.maintochartBtn_cardio);

        listView = findViewById(R.id.exerciseLV_cardio);
        mTimeTextView = findViewById(R.id.timeRemainingTV);
        bottomNavigationView = findViewById(R.id.BottomNavigationView);











    }

}