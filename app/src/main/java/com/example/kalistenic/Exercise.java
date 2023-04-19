package com.example.kalistenic;

public class Exercise {
    private int user_id;
    private String name;
    private int isCardio;
    private String description;

    public Exercise(int user_id, String name, int isCardio, String description) {
        this.user_id = user_id;

        this.name = name;
        this.isCardio = isCardio;
        this.description = description;

    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsCardio() {
        return isCardio;
    }

    public void setIsCardio(int isCardio) {
        this.isCardio = isCardio;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
