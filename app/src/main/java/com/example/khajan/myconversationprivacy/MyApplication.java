package com.example.khajan.myconversationprivacy;

import android.app.Application;

import java.util.HashSet;

/**
 * Created by khajan on 13/12/16.
 */

public class MyApplication extends Application {

    public static HashSet<String> imageList = new HashSet<>();


    @Override
    public void onCreate() {
        super.onCreate();
        imageList.add("Khajan.JPG");
        imageList.add("Khajan.gif");
    }
}
