package com.example.khajan.myconversationprivacy.database;

import io.realm.RealmObject;

/**
 * Created by khajan on 20/12/16.
 */

public class AppInfo extends RealmObject {

    private String appPackageName;


    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

}
