package com.example.khajan.myconversationprivacy;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.nightonke.blurlockview.BlurLockView;

/**
 * Created by khajan on 15/12/16.
 */

public class LockScreen extends Activity {

    BlurLockView blurLockView;
    private static final String TAG = "LockScreen";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        blurLockView = (BlurLockView) findViewById(R.id.blurlockview);
        // Set the view that need to be blurred
//        blurLockView.setBlurredView(imageView1);


//        / Set the password
//        blurLockView.setCorrectPassword(getIntent().getStringExtra("PASSWORD"));
        blurLockView.setCorrectPassword("1234");

        blurLockView.setOnPasswordInputListener(new BlurLockView.OnPasswordInputListener() {
            @Override
            public void correct(String inputPassword) {
                Log.d(TAG, "correct: ");
                Toast.makeText(LockScreen.this, "Correct Password!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void incorrect(String inputPassword) {
                Log.d(TAG, "incorrect: ");
                Toast.makeText(LockScreen.this, "Incorrect Password!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void input(String inputPassword) {
                Log.d(TAG, "input: " + inputPassword);

            }
        });
    }
}
