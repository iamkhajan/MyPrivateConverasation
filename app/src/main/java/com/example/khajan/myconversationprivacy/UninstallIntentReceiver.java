package com.example.khajan.myconversationprivacy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by khajan on 13/12/16.
 */

public class UninstallIntentReceiver extends BroadcastReceiver {
    private static final String TAG = "UninstallIntentReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // fetching package names from extras
        String[] packageNames = intent.getStringArrayExtra("android.intent.extra.PACKAGES");

        if (packageNames != null) {
            Log.d(TAG, "onReceive: called ");
            for (String packageName : packageNames) {
                if (packageName != null && packageName.equals("com.example.khajan.myconversationprivacy")) {
                    // User has selected our application under the Manage Apps settings
                    // now initiating background thread to watch for activity
                    Log.d(TAG, "onReceive: got for my package ");
                    new ListenActivities(context).start();

                }
            }
        }
    }

}
