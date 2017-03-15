package com.example.khajan.myconversationprivacy.service;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.khajan.myconversationprivacy.R;
import com.github.mmin18.widget.RealtimeBlurView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GlobalTouchService extends Service implements OnTouchListener {

    private String TAG = this.getClass().getSimpleName();
    // window manager
    private WindowManager mWindowManager;
    // linear layout will use to detect touch event
    private LinearLayout touchLayout;
    RelativeLayout realtimeBlurView;

    boolean clickedOnce;

    RealtimeBlurView blurView;
    SeekBar blurRadius;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    public void onCreate() {
        super.onCreate();
        // create linear layout
        touchLayout = new LinearLayout(this);
        // set layout width 30 px and height is equal to full screen
        LayoutParams lp = new LayoutParams(30, LayoutParams.MATCH_PARENT);
//        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        touchLayout.setLayoutParams(lp);
        // set on touch listener
        touchLayout.setOnTouchListener(this);
        touchLayout.setClickable(false);
        touchLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));


        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.my_blur_layout, null);
        realtimeBlurView = (RelativeLayout) layout.findViewById(R.id.real_time_blur);


        blurView = (RealtimeBlurView) realtimeBlurView.findViewById(R.id.blur_view);
        blurRadius = (SeekBar) realtimeBlurView.findViewById(R.id.blur_radius);

        blurRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateRadius();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

//        touchLayout.addView(realtimeBlurView);

        Log.d(TAG, "onCreate: Realtime Blurview is " + realtimeBlurView);

        EventBus.getDefault().register(this);


        try {
            final Context myContext = createPackageContext("com.whatsapp",
                    Context.CONTEXT_IGNORE_SECURITY);
//            remoteViews(myContext);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // fetch window manager object
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        // set layout parameter of window manager
        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, // width of layout 30 px
                500, // height is equal to full screen
                WindowManager.LayoutParams.TYPE_PHONE, // Type Ohone, These are non-application windows providing user interaction with the phone (in particular incoming calls).
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, // this window won't ever get key input focus
                PixelFormat.TRANSLUCENT);

        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        Log.i(TAG, "add View");

//        mWindowManager.addView(touchLayout, mParams);
        mWindowManager.addView(realtimeBlurView, mParams);


        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                Log.d(TAG, "onDown: ");
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {
                Log.d(TAG, "onShowPress: ");
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                Log.d(TAG, "onSingleTapUp: ");
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                Log.d(TAG, "onScroll: " + motionEvent.getAction());
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
                Log.d(TAG, "onLongPress: ");
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                Log.d(TAG, "onFling: " + motionEvent.getAction());
                return false;
            }
        });

        View.OnTouchListener gestureListener = new View.OnTouchListener() {


            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);

            }

        };
//        touchLayout.setOnTouchListener(gestureListener);

    }


    @Override
    public void onDestroy() {
        if (mWindowManager != null) {
            if (realtimeBlurView != null) {
                mWindowManager.removeView(realtimeBlurView);
                realtimeBlurView = null;
            }
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
            Log.i(TAG, "Action :" + event.getAction() + "\t X :" + event.getRawX() + "\t Y :" + event.getRawY());
//            showAlert(v);
//            Toast.makeText(this, "Hey View clicked : ", Toast.LENGTH_SHORT).show();
            int yValue = (int) event.getRawY();
            if (isBetween(yValue, 850, 950)) {

                if (clickedOnce)
                    return true;

                clickedOnce = true;
//                Toast.makeText(this, "Hey Khajan lets do this : " + isKeyboardShown(touchLayout), Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Hey Khajan lets do this : " + checkKeyboard(), Toast.LENGTH_SHORT).show();
                onDestroy();
                forceTouchAppWithAttributes(getApplicationContext(), event.getRawX(), event.getRawY());

                try {
//                    executeShell();
                    executeShellCommand();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }


        return false;
    }

    private void updateRadius() {
        blurView.setBlurRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, blurRadius.getProgress(), getResources().getDisplayMetrics()));
//        blurRadiusText.setText(blurRadius.getProgress() + "dp");
    }


    public static boolean isBetween(int value, int min, int max) {
        return ((value > min) && (value < max));
    }

    boolean checkKeyboard() {
        int heightDiff = touchLayout.getRootView().getHeight() - touchLayout.getHeight();
        if (heightDiff > dpToPx(this, 200)) { // if more than 200 dp, it's probably a keyboard...
            // ... do something here
            return true;
        }
        return false;
    }


    public void showAlert(View view) {
        AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(this);
        myAlertBuilder.setMessage("Hi Khajan")
                .setTitle("Khajan")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onDestroy();
                    }
                });
        AlertDialog myAlert = myAlertBuilder.create();
        myAlert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        myAlert.show();
    }


    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }


    private void forceTouchAppWithAttributes(Context context, float x, float y) {
        ActivateAppRunnable activateAppAsyncTask = new ActivateAppRunnable();
        activateAppAsyncTask.setParamsForActivation(context, x, y, 1000);
        new Thread(activateAppAsyncTask).start();
    }


    private class ActivateAppRunnable implements Runnable {
        private float pozPercentX = 0f;
        private float pozPercentY = 0f;
        private long timeToWaitForForceClick = 0;
        private Context context;

        public void setParamsForActivation(Context context, float pozPercentX,
                                           float pozPercentY, long timeToWait) {
            this.pozPercentX = pozPercentX;
            this.pozPercentY = pozPercentY;
            this.timeToWaitForForceClick = timeToWait;
            this.context = context;
        }

        @Override
        public void run() {
            try {
                DisplayMetrics displayMetrics = context.getResources()
                        .getDisplayMetrics();
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;

                Thread.sleep(timeToWaitForForceClick);
                Log.d(TAG, "Device width and height :: " + width + ", "
                        + height);
//
//                float pozX = width * pozPercentX / 100;
//                float pozY = height * pozPercentY / 100;
                float pozX = pozPercentX;
                float pozY = pozPercentY;
                Log.d(TAG, "Click width and height :: " + pozX + ", "
                        + pozY);

                Instrumentation m_Instrumentation = new Instrumentation();
//                m_Instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                m_Instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_B);

                // pozx goes from 0 to SCREEN WIDTH , pozy goes from 0 to SCREEN
                // HEIGHT
                m_Instrumentation.sendPointerSync(MotionEvent.obtain(
                        SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_DOWN, pozX, pozY, 0));
                m_Instrumentation.sendPointerSync(MotionEvent.obtain(
                        SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_UP, pozX, pozY, 0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    void executeShellCommand() {
//        String command = "pm install -r " + "";
        String command = "input tap 400 400" + "";
//        String command = "adb shell dumpsys window InputMethod | grep \"mHasSurface\"";
//        String command = "dumpsys window InputMethod | grep \"mHasSurface\"";
//        String command = " logcat|grep \"ActivityManager: START\"";
        //executeShellCommand(command);
        new InstallAppAsyncTask(getApplicationContext(), "").execute(command);
    }


    private static class InstallAppAsyncTask extends AsyncTask<String, Void, String> {

        private Context context;
        private String appPackage;

        public InstallAppAsyncTask(Context context, String appPackage) {
            this.context = context;
            this.appPackage = appPackage;
        }

        @Override
        protected String doInBackground(String... params) {
            String cmd = params[0];

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executeShellCommand(cmd);


            return null;
        }

        @Override
        protected void onPostExecute(String result) {


            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

//            notificationManager.cancel(appPackage, 234);
            super.onPostExecute(result);

        }
    }


    private static String executeShellCommand(String command) {
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("GlobalTouchService", "executeShellCommand: " + e.getCause());
        }

        String response = output.toString();
        Log.d("GlobalTouchService", "executeShellCommand: " + response);
        return response;


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(boolean event) {
        if (event) {
            if (realtimeBlurView != null)
                realtimeBlurView.setVisibility(View.VISIBLE);
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                realtimeBlurView.setVisibility(View.GONE);
                realtimeBlurView.invalidate();
            }
        }, 2000);
    }
}
