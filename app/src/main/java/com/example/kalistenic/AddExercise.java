package com.example.kalistenic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

public class AddExercise extends AppCompatActivity {
    private EditText exerciseName, exerciseDescription;
    private Button saveBtn;
    private CheckBox checkBox;
    private DatabaseHelper dbHelper;
    private boolean isCardioChecked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);
        initWidgets();
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCardioChecked = isChecked;
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                int userId = prefs.getInt("userId", -1);
                dbHelper = new DatabaseHelper(AddExercise.this);
                String ExerciseName = exerciseName.getText().toString();
                String ExerciseDescription = exerciseDescription.getText().toString();
                int isCardio = isCardioChecked ? 1 : 0;
                dbHelper.addExercise(userId, ExerciseName, isCardio, ExerciseDescription);
                Intent intent = new Intent(AddExercise.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initWidgets() {
        exerciseName = findViewById(R.id.exerciseNameET);
        exerciseDescription = findViewById(R.id.exerciseDescriptionET);
        saveBtn = findViewById(R.id.saveExerciseBtn);
        checkBox = findViewById(R.id.cardioCheckBox);
    }
}