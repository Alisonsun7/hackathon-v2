package com.comp2100.comp2100miniproject;

import android.app.Application;
import android.util.Log;

import dao.RandomContentGenerator;
import dao.UserDAO;
import dao.model.User;

public class Comp2100App extends Application {
    public static User currentUser;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("Comp2100App", "Global initialization code runs once!");

        // Generate random data
        RandomContentGenerator.populateRandomData();

        // Register current user
        currentUser = UserDAO.getInstance().register("Tester", "123456");
    }
}
