package com.example.kalistenic;

public class UserSingleton {
    private static UserSingleton instance;
    private int userId;

    private UserSingleton() {}
    public static UserSingleton getInstance() {
        if (instance == null) {
            instance = new UserSingleton();
        }
        return instance;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getUserId() {
        return userId;
    }
}
