package com.example.kalistenic;

public class Workout {
    private int user_id;
    private int exercise_id;
    private int sets;
    private int reps;
    private int timeSpentSec;
    private String date;
    private String time;

    public Workout(int user_id, int exercise_id, int sets, int reps){
        this.user_id = user_id;
        this.exercise_id = exercise_id;
        this.sets = sets;
        this.reps = reps;
    }
    public Workout(int user_id, int exercise_id, int timeSpentSec){
        this.user_id = user_id;
        this.exercise_id = exercise_id;
        this.timeSpentSec = timeSpentSec;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getExercise_id() {
        return exercise_id;
    }

    public void setExercise_id(int exercise_id) {
        this.exercise_id = exercise_id;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getTimeSpentSec() {
        return timeSpentSec;
    }

    public void setTimeSpentSec(int timeSpentSec) {
        this.timeSpentSec = timeSpentSec;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
