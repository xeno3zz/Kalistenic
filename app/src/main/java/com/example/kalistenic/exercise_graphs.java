package com.example.kalistenic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class exercise_graphs extends AppCompatActivity {

    private Chart chart;
    private Spinner exerciseChartSpinner;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_graphs);
        initWidgets();
        populateExerciseSpinner();
        exerciseChartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String exercise = exerciseChartSpinner.getSelectedItem().toString();
                Cursor cursor = db.query("exercises", new String[]{"exercise_id", "isCardio"}, "name=?", new String[]{exercise}, null, null, null);
                if (!cursor.moveToFirst()) {
                    cursor.close();
                    return;
                }
                int exerciseId = cursor.getInt(cursor.getColumnIndexOrThrow("exercise_id"));
                boolean isCardio = cursor.getInt(cursor.getColumnIndexOrThrow("isCardio")) == 1;
                cursor.close();

                Map<String, Integer> repetitionsForExercise;
                Map<String, Integer> timeSpentForExercise;

                if (isCardio) {
                    SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    int userId = prefs.getInt("userId", -1);
                    timeSpentForExercise = dbHelper.getTimeSpentForCardioExercise(exerciseId, userId);
                    LineData lineData = createLineDataCardio(timeSpentForExercise);
                    displayChart(lineData);
                } else {
                    SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    int userId = prefs.getInt("userId", -1);
                    repetitionsForExercise = dbHelper.getRepetitionsForExercise(exerciseId, userId);
                    LineData lineData = createLineData(repetitionsForExercise);
                    displayChart(lineData);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initWidgets() {
        chart = findViewById(R.id.chart);

        exerciseChartSpinner = findViewById(R.id.chart_spinner);
    }
    private void populateExerciseSpinner() {
        dbHelper = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getExerciseNames(userId));

        exerciseChartSpinner.setAdapter(adapter);
    }
    private List<String> getExerciseNames(int userId) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define the columns you want to retrieve
        String[] projection = {"name"};

        // Build the query to retrieve the names of the exercises with isCardio checked
        String selection = "user_id=?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query("exercises", projection, selection, selectionArgs, null, null, null);

        List<String> exerciseNames = new ArrayList<>();
        while (cursor.moveToNext()) {
            exerciseNames.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        }
        cursor.close();
        db.close();

        return exerciseNames;
    }
    private LineData createLineData(Map<String, Integer> repetitionsMap) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, Integer> entry : repetitionsMap.entrySet()) {
            entries.add(new Entry(i, entry.getValue()));
            labels.add(entry.getKey());
            i++;
        }
        LineDataSet dataSet = new LineDataSet(entries, "Repetitions");
        LineData lineData = new LineData(dataSet);
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        return lineData;
    }
    private LineData createLineDataCardio(Map<String, Integer> timeSpentMap){
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, Integer> entry : timeSpentMap.entrySet()) {
            entries.add(new Entry(i, entry.getValue()));
            labels.add(entry.getKey());
            i++;
        }
        LineDataSet dataSet = new LineDataSet(entries, "Time Spent (Total Amount of Secs)");
        LineData lineData = new LineData(dataSet);
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        return lineData;
    }
    private void displayChart(LineData lineData) {
        chart.getDescription().setEnabled(false);
        chart.setData(lineData);
        chart.invalidate();
    }

}