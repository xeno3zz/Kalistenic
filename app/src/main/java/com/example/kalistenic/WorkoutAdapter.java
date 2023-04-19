package com.example.kalistenic;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class WorkoutAdapter extends CursorAdapter {

    private DatabaseHelper dbHelper;
    private final LayoutInflater inflater;
    private final Context context;
    private final SQLiteDatabase db;
    private ListView listView;
    private final WorkoutListener listener;
    private boolean isMain;

    public WorkoutAdapter(Context context, Cursor cursor, SQLiteDatabase db, WorkoutListener listener, boolean isMain, DatabaseHelper dbHelper) {
        super(context, cursor, 0);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.db = db;
        this.listener = listener;
        this.isMain = isMain;
        this.dbHelper = dbHelper;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.workout_cell, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if(isMain){

            TextView exerciseNameTextView = view.findViewById(R.id.exName_cellTV);
            int exerciseId = cursor.getInt(cursor.getColumnIndexOrThrow("exercise_id"));
            String exerciseName = dbHelper.getExerciseNameById(exerciseId);
            exerciseNameTextView.setText(exerciseName);
//            exerciseNameTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow("exercise")));

            TextView repsTextView = view.findViewById(R.id.exReps_cellTV);
            repsTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow("reps")));

            TextView setsTextView = view.findViewById(R.id.exSets_cellTV);
            setsTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow("sets")));
            Button deleteButton = view.findViewById(R.id.delete_button);
            final long id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(id);
                    listener.onDataChanged();
                }
            });
        }
        else{
            TextView timeSpentTextView = view.findViewById(R.id.exTimespent_cellTV);
            timeSpentTextView.setVisibility(View.VISIBLE);
            long timeSpentSeconds = cursor.getInt(cursor.getColumnIndexOrThrow("timeSpentSec"));
            String timeSpentString = String.format("%02d:%02d:%02d", timeSpentSeconds / 3600, (timeSpentSeconds % 3600) / 60, timeSpentSeconds % 60);

            TextView exerciseNameTextView = view.findViewById(R.id.exName_cellTV);
            int exerciseId = cursor.getInt(cursor.getColumnIndexOrThrow("exercise_id"));
            String exerciseName = dbHelper.getExerciseNameById(exerciseId);
            exerciseNameTextView.setText(exerciseName);

            TextView repsTextView = view.findViewById(R.id.exReps_cellTV);
            repsTextView.setVisibility(View.GONE);

            TextView setsTextView = view.findViewById(R.id.exSets_cellTV);
            setsTextView.setVisibility(View.GONE);





            timeSpentTextView.setText(timeSpentString);
            Button deleteButton = view.findViewById(R.id.delete_button);
            final long id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(id);
                    listener.onDataChanged();
                }
            });
        }

    }
    private void deleteItem(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.delete("workout", "workout_id=?", new String[]{String.valueOf(id)});
        notifyDataSetChanged();
        listener.onDataChanged();
        db.close();
    }


    public interface WorkoutListener {
        void onDataChanged();
    }
//    @Override
//    public long getItemId(int position) {
//        Cursor cursor = getCursor();
//        if (cursor.moveToPosition(position)) {
//            return cursor.getLong(cursor.getColumnIndexOrThrow("workout_id"));
//        }
//        return super.getItemId(position);
//    }
}
